package plugin.utils;

import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import plugin.constant.AIConstant;
import plugin.constant.ChatConstant;
import plugin.pojo.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static plugin.App.logger;

/**
 * @author SLHAF
 */
public class AIUtil {
    private static final String APIKEY;
    private static final ClientV4 CLIENT;
    private static final String REQUEST_ID_TEMPLATE = "ChatAI_InGroup_v2";
    private static final HashMap<Long, List<ChatMessage>> userMessagesNormal = new HashMap<>();
    private static final HashMap<Long, Long> userLatestTimeNormal = new HashMap<>();
    private static String modelNormal;

    private static final HashMap<Long, List<ChatMessage>> userMessagesCode = new HashMap<>();
    private static final HashMap<Long, Long> userLatestTimeCode = new HashMap<>();
    private static String modelCode;

    private static final Long CHECK_TIME, TIMEOUT;

    static {
        Config config = ConfigUtil.getConfig();
        APIKEY = config.getApikey();
        CLIENT = new ClientV4.Builder(APIKEY).build();
        modelNormal = config.getModelNormal();
        modelCode = config.getModelCode();
        CHECK_TIME = Long.valueOf(config.getTimeCheck().substring(1));
        TIMEOUT = Long.valueOf(config.getTimeout().substring(1));
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(CHECK_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                synchronized (userMessagesNormal) {
                    if (!userLatestTimeNormal.isEmpty()) {
                        //查看user最近时间，如果超过30min，则清理对应记录
                        userLatestTimeNormal.forEach((id, latestTime) -> {
                            Long currentTime = System.currentTimeMillis();
                            if (currentTime - latestTime > TIMEOUT && userMessagesNormal.containsKey(id)) {
                                userMessagesNormal.remove(id);
                                logger.info("Normal记录清理:" + id);
                            }
                        });
                    }
                }

                synchronized (userMessagesCode) {
                    if (!userLatestTimeCode.isEmpty()) {
                        //查看user最近时间，如果超过30min，则清理对应记录
                        userLatestTimeCode.forEach((id, latestTime) -> {
                            Long currentTime = System.currentTimeMillis();
                            if (currentTime - latestTime > 30 * 60 * 1000 && userMessagesCode.containsKey(id)) {
                                userMessagesCode.remove(id);
                                logger.info("Code记录清理:" + id);
                            }
                        });
                    }
                }
            }
        }).start();
        logger.info("清理线程启动");
        logger.info("当前代码模型: " + modelCode);
        logger.info("当前聊天模型: " + modelNormal);
    }

    private AIUtil() {
    }

    public static String chatCode(Long id, String content, String url) {
        synchronized (userMessagesCode) {
            if (ChatConstant.CLEAR.equals(content.replace(" ", ""))) {
                userMessagesCode.remove(id);
                return "消息记录已清空";
            } else if (content.replace(" ", "").startsWith(ChatConstant.CHANGE_MODEL)) {
                content = content.replace(" ", "");
                modelCode = content.substring(4);
                ConfigUtil.modelCodeChange(modelCode);
                return "聊天模型切换为: " + modelCode;
            } else if (AIConstant.CURRENT_MODEL.equals(content.replace(" ", ""))) {
                return "当前模型为: " + modelCode;
            }
            //查看本次id是否有记录存在
            if (!userMessagesCode.containsKey(id)) {
                //创建消息list
                List<ChatMessage> chatMessage = new ArrayList<>();
                chatMessage.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), "你是一位智能编程助手，你会为用户回答关于编程、代码、计算机方面的任何问题，并提供格式规范、可以执行、准确安全的代码，并在必要时提供详细的解释。 请用中文回答。"));
                userMessagesCode.put(id, chatMessage);
            }
            return getChatResponse(id, content, url,modelCode, userMessagesCode, userLatestTimeCode);
        }
    }

    public static String chatNormal(Long id, String content,String url) {
        synchronized (userMessagesNormal) {
            if (ChatConstant.CLEAR.equals(content.replace(" ", ""))) {
                userMessagesNormal.remove(id);
                return "消息记录已清空";
            } else if (content.replace(" ", "").startsWith(ChatConstant.CHANGE_MODEL)) {
                content = content.replace(" ", "");
                modelNormal = content.substring(4);
                ConfigUtil.modelNormalChange(modelNormal);
                return "聊天模型切换为: " + modelNormal;
            } else if (ChatConstant.CHANGE_MODEL.equals(content.replace(" ", ""))) {
                return "当前模型为: " + modelNormal;
            }
            //查看本次id是否有记录存在
            if (!userMessagesNormal.containsKey(id)) {
                //创建消息list
                List<ChatMessage> chatMessage = new ArrayList<>();
                userMessagesNormal.put(id, chatMessage);
            }
            return getChatResponse(id, content, url,modelNormal, userMessagesNormal, userLatestTimeNormal);
        }
    }

    public static String chatOnce(String content,String url) {
        if (content.replace(" ", "").startsWith(ChatConstant.CHANGE_MODEL)) {
            content = content.replace(" ", "");
            modelNormal = content.substring(4);
            ConfigUtil.modelNormalChange(modelNormal);
            return "代码模型切换为: " + modelNormal;
        } else if (AIConstant.CURRENT_MODEL.equals(content.replace(" ", ""))) {
            return "当前模型为: " + modelNormal;
        }
        String result = "";
        if(url != null){
            if (!OCRUtil.isSupported){
                return "当前不支持文字识别，请检查阿里云OCR相关配置。";
            } else {
                String contentOfImage = OCRUtil.getContentOfImage(url);
                if (contentOfImage == null){
                    result = "未识别出图片内容。";
                }else if (AIConstant.ERROR.equals(contentOfImage)){
                    result = "识别图片内容出错，请查看控制台。";
                }else {
                    content = content.replace("[图片]", "\r\n[" + contentOfImage + "]\r\n");
                }
            }
        }
        String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), content));
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(modelNormal)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .build();
        ModelApiResponse invokeModelApiResp = CLIENT.invokeModelApi(chatCompletionRequest);
        int code = invokeModelApiResp.getCode();
        if (code == 200) {
            printTokenInfo(invokeModelApiResp);
            return invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent().toString();
        } else {
            logger.warning("code: " + code);
            logger.warning("ErrorCode:" + invokeModelApiResp.getData().getError().getCode());
            logger.warning("msg:" + invokeModelApiResp.getData().getError().getMessage());
            return invokeModelApiResp.getMsg()+"\r\n<"+result+">";
        }
    }


    private static String getChatResponse(Long id, String content, String url,String model, HashMap<Long, List<ChatMessage>> userMessages, HashMap<Long, Long> userLatestTime) {
        userLatestTime.put(id, System.currentTimeMillis());

        String requestId = String.format(REQUEST_ID_TEMPLATE, System.currentTimeMillis());
        //处理url内容
        String result = "";
        if(url != null){
            if (!OCRUtil.isSupported){
                logger.warning("unSupportedOCR");
                return "当前不支持文字识别，请检查阿里云OCR相关配置。";
            } else {
                String contentOfImage = OCRUtil.getContentOfImage(url);
                if (contentOfImage == null){
                    result = "未识别出图片内容。";
                }else if (AIConstant.ERROR.equals(contentOfImage)){
                    result = "识别图片内容出错，请查看控制台。";
                }else {
                    content = content.replace("[图片]", "\r\n[" + contentOfImage + "]\r\n");
                }
            }
        }
        logger.info("final content:" + content);

        //添加消息
        userMessages.get(id).add(new ChatMessage(ChatMessageRole.USER.value(), content));

        //创建并发送请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(model)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(userMessages.get(id))
                .requestId(requestId)
                .build();

        ModelApiResponse invokeModelApiResp = CLIENT.invokeModelApi(chatCompletionRequest);
        int code = invokeModelApiResp.getCode();
        if (code == 200) {
            printTokenInfo(invokeModelApiResp);
            String response = invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent().toString();
            userMessages.get(id).add(new ChatMessage(ChatMessageRole.ASSISTANT.value(), response));
            return response;
        } else {
            logger.warning("code: " + code);
            logger.warning("ErrorCode:" + invokeModelApiResp.getData().getError().getCode());
            logger.warning("msg:" + invokeModelApiResp.getData().getError().getMessage());
            return invokeModelApiResp.getMsg()+"\r\n<"+result+">";
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

}
