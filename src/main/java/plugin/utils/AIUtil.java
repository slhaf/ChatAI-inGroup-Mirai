package plugin.utils;

import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import plugin.constant.AIConstant;
import plugin.constant.ChatConstant;
import plugin.constant.ConfigConstant;
import plugin.pojo.Config;
import plugin.pojo.UserCustomLatestTime;
import plugin.pojo.UserCustomMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static plugin.utils.ConfigUtil.logger;


/**
 * @author SLHAF
 */
public class AIUtil {
    private static final String APIKEY;
    private static final ClientV4 CLIENT;
    private static final String REQUEST_ID_TEMPLATE = "ChatAI_InGroup_v2";
    private static final HashMap<Long, List<ChatMessage>> userDefaultMessages = new HashMap<>();
    private static final HashMap<Long, Long> userLatestTimeOfDefault = new HashMap<>();
    private static String defaultModel;

    private static final HashMap<Long, List<UserCustomMessage>> userCustomMessages = new HashMap<>();
    private static final HashMap<Long, List<UserCustomLatestTime>> userLatestTimeOfCustom = new HashMap<>();
    /*private static String modelCode;*/
    /**
     * 结构:
     * /c : glm-4-flush|预设内容
     */
    private static HashMap<String, String> customCommands;

    private static final Long CHECK_TIME, TIMEOUT;

    static {
        Config config = ConfigUtil.getConfig();
        APIKEY = config.getApikey();
        CLIENT = new ClientV4.Builder(APIKEY).build();
        defaultModel = config.getDefaultModel();
        customCommands = config.getCustomCommands();
        CHECK_TIME = Long.valueOf(config.getTimeCheck().substring(1));
        TIMEOUT = Long.valueOf(config.getTimeout().substring(1));
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(CHECK_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (!userLatestTimeOfDefault.isEmpty()) {
                    //查看user最近时间，如果超过TIMEOUT，则清理对应记录
                    userLatestTimeOfDefault.forEach((id, latestTime) -> {
                        synchronized (userDefaultMessages.get(id)) {
                            Long currentTime = System.currentTimeMillis();
                            if (currentTime - latestTime > TIMEOUT && userDefaultMessages.containsKey(id)) {
                                userDefaultMessages.remove(id);
                                logger.info("default记录清理:" + id);
                            }
                        }
                    });
                }


                if (!userLatestTimeOfCustom.isEmpty()) {
                    //查看user最近时间，如果超过TIMEOUT，则清理对应记录
                    userLatestTimeOfCustom.forEach((id, userCustomLatestTimeList) -> {
                        synchronized (userCustomMessages.get(id)) {
                            ArrayList<Integer> indexToRemove = new ArrayList<>();
                            Long currentTime = System.currentTimeMillis();
                            for (int i = 0; i < userCustomLatestTimeList.size(); i++) {
                                if (currentTime - userCustomLatestTimeList.get(i).getLatestTime() > TIMEOUT){
                                    logger.info("custom记录清理: "+id+" "+userCustomLatestTimeList.get(i).getChatCommand());
                                    userCustomMessages.get(id).remove(i);
                                    indexToRemove.add(i);
                                }
                            }
                            //移除最近操作时间
                            for (int i : indexToRemove) {
                                userCustomLatestTimeList.remove(i);
                            }
                        }
                    });
                }

            }
        }).start();
        logger.info("清理线程启动");
        logger.info("当前默认聊天模型: " + defaultModel);
    }

    private AIUtil() {
    }

    public static String customChat(Long id, String content, String url, String chatCommand) {
        if (ChatConstant.CLEAR.equals(content.replace(ChatConstant.BLANK, ""))) {
            if (!userCustomMessages.containsKey(id)) {
                return "不存在消息记录";
            }
            for (UserCustomMessage userCustomMessage : userCustomMessages.get(id)) {
                if (userCustomMessage.getCommand().equals(chatCommand)) {
                    userCustomMessage.getMessages().clear();
                }
            }
            return "消息记录已清空";
        } else if (AIConstant.CURRENT_MODEL.equals(content.replace(ChatConstant.BLANK, ""))) {
            String modelName = customCommands.get(chatCommand).split(ConfigConstant.CUSTOM_SPLIT)[0];
            return "当前模型为: " + modelName;
        }
        //查看本次id是否有记录存在
        if (!userCustomMessages.containsKey(id)) {
            //不存在消息记录
            //创建消息list
            List<ChatMessage> chatMessages = new ArrayList<>();
            if (!customCommands.get(chatCommand).split(ConfigConstant.CUSTOM_SPLIT)[1].equals(ConfigConstant.NULL)) {
                ChatMessage customMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), customCommands.get(chatCommand).split(ConfigConstant.CUSTOM_SPLIT)[1]);
                chatMessages.add(customMessage);
            }
            List<UserCustomMessage> userCustomMessageList = new ArrayList<>();
            userCustomMessageList.add(new UserCustomMessage(chatCommand, chatMessages));
            userCustomMessages.put(id, userCustomMessageList);
        }else {
            //存在消息记录，但需要判断是否存在该指令对应的消息记录
            List<UserCustomMessage> userCustomMessageList = userCustomMessages.get(id);
            boolean found = false;
            for (UserCustomMessage userCustomMessage : userCustomMessageList) {
                if (userCustomMessage.getCommand().equals(chatCommand)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                //如果不存在该指令对应的消息记录
                UserCustomMessage userCustomMessage = new UserCustomMessage();
                userCustomMessage.setCommand(chatCommand);
                List<ChatMessage> chatMessages = new ArrayList<>();
                //判断是否存在预设
                if (!customCommands.get(chatCommand).split(ConfigConstant.CUSTOM_SPLIT)[1].equals(ConfigConstant.NULL)){
                    ChatMessage customMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), customCommands.get(chatCommand).split(ConfigConstant.CUSTOM_SPLIT)[1]);
                    chatMessages.add(customMessage);
                }
                userCustomMessage.setMessages(chatMessages);
                userCustomMessageList.add(userCustomMessage);
            }
        }
        String modelName = customCommands.get(chatCommand).split(ConfigConstant.CUSTOM_SPLIT)[0];
        synchronized (userCustomMessages.get(id)) {
            return getChatResponse(id, content, url, modelName, chatCommand);
        }
    }

    public static String defaultChat(Long id, String content, String url) {
        if (ChatConstant.CLEAR.equals(content.replace(ChatConstant.BLANK, ""))) {
            if (!userDefaultMessages.containsKey(id)) {
                return "不存在消息记录";
            }
            userDefaultMessages.remove(id);
            return "消息记录已清空";
        } else if (ChatConstant.CURRENT_MODEL.equals(content.replace(ChatConstant.BLANK, ""))) {
            return "当前模型为: " + defaultModel;
        }
        //查看本次id是否有记录存在
        if (!userDefaultMessages.containsKey(id)) {
            //创建消息list
            List<ChatMessage> chatMessages = new ArrayList<>();
            if (customCommands.containsKey(ConfigConstant.DEFAULT) && !ConfigConstant.NULL.equals(customCommands.get(ConfigConstant.DEFAULT))) {
                ChatMessage customMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), customCommands.get(ConfigConstant.DEFAULT));
                chatMessages.add(customMessage);
            }
            userDefaultMessages.put(id, chatMessages);
        }
        synchronized (userDefaultMessages.get(id)) {
            return getChatResponse(id, content, url, defaultModel, null);
        }
    }

    public static String chatOnce(String content, String url) {

        if (AIConstant.CURRENT_MODEL.equals(content.replace(ChatConstant.BLANK, ""))) {
            return "当前模型为: " + defaultModel;
        }
        String result = "";
        if (url != null) {
            if (!OCRUtil.isSupported) {
                return "当前不支持文字识别，请检查阿里云OCR相关配置。";
            } else {
                String contentOfImage = OCRUtil.getContentOfImage(url);
                if (contentOfImage == null) {
                    result = "未识别出图片内容。";
                } else if (AIConstant.ERROR.equals(contentOfImage)) {
                    result = "识别图片内容出错，请查看控制台。";
                } else {
                    content = content.replace("[图片]", "\r\n[" + contentOfImage + "]\r\n");
                }
            }
        }
        String requestId = REQUEST_ID_TEMPLATE + "_once_" + System.currentTimeMillis();
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), content));
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(defaultModel)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .build();
        ModelApiResponse invokeModelApiResp = CLIENT.invokeModelApi(chatCompletionRequest);
        int code = invokeModelApiResp.getCode();
        if (code == AIConstant.NORMAL_CODE) {
            printTokenInfo(invokeModelApiResp);
            return invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent().toString();
        } else {
            logger.warning("code: " + code);
            logger.warning("ErrorCode:" + invokeModelApiResp.getData().getError().getCode());
            logger.warning("msg:" + invokeModelApiResp.getData().getError().getMessage());
            return invokeModelApiResp.getMsg() + "\r\n<" + result + ">";
        }
    }


    /**
     * 取得回复内容
     * <br>chatMessages内容只有两个static变量，只需根据传入的chatCommand是否为null进行判断
     *
     * @param id             聊天用户ID
     * @param content        聊天用户发送的内容，content已在Listener处进行处理
     * @param url            获取到的图片url，如果没有图片，则为null
     * @param model          本次对话所需的模型名称
     *                       <br>当为userDefaultMessages时：
     *                       <br><code>
     *                       <br>   qq1 : [msg1,msg2,msg3]
     *                       <br>   qq2 : [msg1,msg2,msg3]
     *                       <br></code>
     *                       <br>当为userCustomMessages时：
     *                       <br><code>
     *                       <br>   qq1 : [{command1,[msg1.msg2,msg3]},{command2,[msg1,msg2,msg3]},{command3,[msg1,msg2,msg3]}]
     *                       <br>   qq2 : [{command1,[msg1.msg2,msg3]},{command2,[msg1,msg2,msg3]},{command3,[msg1,msg2,msg3]}]
     *                       <br></code>
     * @param chatCommand 聊天指令
     * @return 得到的模型响应内容
     */
    private static String getChatResponse(Long id, String content, String url, String model, String chatCommand) {
        String requestId = REQUEST_ID_TEMPLATE + "_" + model + "_" + System.currentTimeMillis();
        //处理url内容
        String result = "";
        if (url != null) {
            if (!OCRUtil.isSupported) {
                logger.warning("unSupportedOCR");
                return "当前不支持文字识别，请检查阿里云OCR相关配置。";
            } else {
                String contentOfImage = OCRUtil.getContentOfImage(url);
                if (contentOfImage == null) {
                    result = "未识别出图片内容。";
                } else if (AIConstant.ERROR.equals(contentOfImage)) {
                    result = "识别图片内容出错，请查看控制台。";
                } else {
                    content = content.replace("[图片]", "\r\n[" + contentOfImage + "]\r\n");
                }
            }
        }
        logger.info("final content:" + content);

        //根据primaryUserMessages中的内容来定义userMessages并更新latestTime
        List<ChatMessage> chatMessages = null;
        if (chatCommand == null) {
            chatMessages = userDefaultMessages.get(id);
            userLatestTimeOfDefault.put(id, System.currentTimeMillis());
        } else {
            List<UserCustomMessage> userCustomMessageList = userCustomMessages.get(id);
            for (UserCustomMessage userCustomMessage : userCustomMessageList) {
                //在调用时已确保存在指令对应的消息记录
                if (userCustomMessage.getCommand().equals(chatCommand)) {
                    chatMessages = userCustomMessage.getMessages();
                    break;
                }
            }
            //更新最新操作时间
            boolean foundTime = false;
            List<UserCustomLatestTime> userCustomLatestTimeList;
            if (userLatestTimeOfCustom.containsKey(id)) {
                //如果存在该id对应的任一聊天记录
                userCustomLatestTimeList = userLatestTimeOfCustom.get(id);
                for (UserCustomLatestTime userCustomLatestTime : userCustomLatestTimeList) {
                    if (userCustomLatestTime.getChatCommand().equals(chatCommand)) {
                        userCustomLatestTime.setLatestTime(System.currentTimeMillis());
                        foundTime = true;
                        break;
                    }
                }
            }else {
                //如果没有记录存在
                userCustomLatestTimeList = new ArrayList<>();
                userCustomLatestTimeList.add(new UserCustomLatestTime(chatCommand,System.currentTimeMillis()));
                userLatestTimeOfCustom.put(id,userCustomLatestTimeList);
                foundTime = true;
            }
            if (!foundTime) {
                userCustomLatestTimeList.add(new UserCustomLatestTime(chatCommand, System.currentTimeMillis()));
            }
        }

        if (chatMessages == null) {
            return "消息记录读取失败";
        }

        //添加消息
        chatMessages.add(new ChatMessage(ChatMessageRole.USER.value(), content));

        //创建并发送请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(model)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(chatMessages)
                .requestId(requestId).build();

        ModelApiResponse invokeModelApiResp = CLIENT.invokeModelApi(chatCompletionRequest);
        int code = invokeModelApiResp.getCode();
        if (code == AIConstant.NORMAL_CODE) {
            printTokenInfo(invokeModelApiResp);
            String response = invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent().toString();
            chatMessages.add(new ChatMessage(ChatMessageRole.ASSISTANT.value(), response));
            return response;
        } else {
            logger.warning("code: " + code);
            logger.warning("ErrorCode:" + invokeModelApiResp.getData().getError().getCode());
            logger.warning("msg:" + invokeModelApiResp.getData().getError().getMessage());
            return invokeModelApiResp.getMsg() + "\r\n<" + result + ">";
        }
    }

    private static void printTokenInfo(ModelApiResponse invokeModelApiResp) {
        int promptTokens = invokeModelApiResp.getData().getUsage().getPromptTokens();
        int completionTokens = invokeModelApiResp.getData().getUsage().getCompletionTokens();
        int totalTokens = invokeModelApiResp.getData().getUsage().getTotalTokens();
        logger.info("prompt_tokens: " + promptTokens);
        logger.info("completion_tokens: " + completionTokens);
        logger.info("total_tokens: " + totalTokens);
    }

    /**
     * 清空所有聊天记录
     */
    public static String clearAll() {
        int size = 0;
        size += userDefaultMessages.size();
        userDefaultMessages.clear();
        userLatestTimeOfDefault.clear();

        for (Long id : userCustomMessages.keySet()) {
            size += userCustomMessages.get(id).size();
        }
        userCustomMessages.clear();
        userLatestTimeOfCustom.clear();
        return "本次清空" + size + "条会话";
    }
}
