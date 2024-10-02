package plugin.listener;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import org.jetbrains.annotations.NotNull;
import plugin.constant.ChatConstant;
import plugin.constant.MethodsConstant;
import plugin.pojo.Config;
import plugin.utils.AIUtil;
import plugin.utils.ConfigUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static plugin.utils.ConfigUtil.logger;


/**
 * @author SLHAF
 */
public class GroupMessageListener extends SimpleListenerHost {

    private static final Config config = ConfigUtil.getConfig();

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
        logger.error(exception.getMessage());
    }

    /**
     * 负责提取链接、获取消息内容（去除指令头），提取指令、提取发送者id
     * @param event 接收群聊消息
     */
    @EventHandler
    public void groupMessageHandler(GroupMessageEvent event) {
        //处理消息
        String id = String.valueOf(event.getSender().getId());
        String content = event.getMessage().contentToString();
        String miraiCode = event.getMessage().serializeToMiraiCode();
        String url = null;
        String chatCommand = null;
        if (miraiCode.matches(ChatConstant.MATCH_MESSAGE)) {
            String regex = ChatConstant.MATCH_IMAGE;
            Pattern pattern = Pattern.compile(regex);

            // 创建Matcher对象
            Matcher matcher = pattern.matcher(miraiCode);

            // 查找并提取链接
            if (matcher.find()) {
                //提取第一个括号内的内容
                url = matcher.group(1);
            }
        }

        MethodsConstant method = MethodsConstant.NONE;

        //消息头处理
        if (content.startsWith(ChatConstant.ONCE_MESSAGE_START)) {
            //单次对话
            content = content.substring(1);
            method = MethodsConstant.ONCE;
        } else if (content.startsWith(ChatConstant.DEFAULT_MESSAGE_START + event.getBot().getId())) {
            //默认对话
            content = content.substring((ChatConstant.DEFAULT_MESSAGE_START + event.getBot().getId()).length());
            method = MethodsConstant.NORMAL;
        } else if (config.getCustomCommands().containsKey(content.split(ChatConstant.BLANK)[0])) {
            //预设对话
            content = content.split(ChatConstant.BLANK)[1];
            method = MethodsConstant.CUSTOM;
            chatCommand = content.split(ChatConstant.BLANK)[0];
        }
        //消息内容处理
        if (content.isBlank()) {
            content = "在吗";
        }
        //发送请求并获取回应
        String response = switch (method) {
            case CUSTOM -> AIUtil.customChat(Long.valueOf(id), content, url,chatCommand);
            case NORMAL -> AIUtil.defaultChat(Long.valueOf(id), content, url);
            case ONCE -> AIUtil.chatOnce(content, url);
            default -> "ERROR!";
        };
        event.getGroup().sendMessage(new At(Long.parseLong(id)).plus("\r\n").plus(response));
    }
}
