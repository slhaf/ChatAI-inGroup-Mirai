package plugin.listener;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import org.jetbrains.annotations.NotNull;
import plugin.constant.ChatConstant;
import plugin.pojo.Config;
import plugin.utils.AIUtil;
import plugin.utils.ConfigUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static plugin.App.logger;

/**
 * @author SLHAF
 */
public class UserMessageListener extends SimpleListenerHost {

    private static final Config config = ConfigUtil.getConfig();

    public enum Methods {

        /**
         * 正常对话
         */
        NORMAL,

        /**
         * 单次对话
         */
        ONCE,

        /**
         * 预设：code
         */
        CODE,

        /**
         * 未匹配
         */
        NONE
    }

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
        logger.error(exception.getMessage());
    }

    @EventHandler
    public void groupMessageHandler(GroupMessageEvent event) {
        //处理消息
        String id = String.valueOf(event.getSender().getId());
        String content = event.getMessage().contentToString();
        String miraiCode = event.getMessage().serializeToMiraiCode();
        String url = null;
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
        Methods method = Methods.NONE;

        if (content.contains(ChatConstant.CHANGE_MODEL) && !id.equals(config.getOwner().substring(1))) {
            event.getGroup().sendMessage(new At(Long.parseLong(id)).plus("没有权限！"));
            return;
        }

        //消息头处理
        if (content.startsWith(ChatConstant.ONCE_MESSAGE_START)) {
            content = content.substring(1);
            method = Methods.ONCE;
        } else if (content.startsWith(ChatConstant.NORMAL_MESSAGE_START + event.getBot().getId())) {
            content = content.substring((ChatConstant.NORMAL_MESSAGE_START + event.getBot().getId()).length());
            method = Methods.NORMAL;
        } else if (content.startsWith(ChatConstant.CODE_MESSAGE_START)) {
            content = content.substring(3);
            method = Methods.CODE;
        }
        //消息内容处理
        if (content.isBlank()) {
            content = "在吗";
        }
        //发送请求并获取回应
        String response = switch (method) {
            case CODE -> AIUtil.chatCode(Long.valueOf(id), content, url);
            case NORMAL -> AIUtil.chatNormal(Long.valueOf(id), content, url);
            case ONCE -> AIUtil.chatOnce(content, url);
            default -> "ERROR!";
        };
        event.getGroup().sendMessage(new At(Long.parseLong(id)).plus("\r\n").plus(response));
    }

    @EventHandler
    public void friendMessageHandler(FriendMessageEvent event) {
        String id = String.valueOf(event.getFriend().getId());
        String content = event.getMessage().contentToString();
        String miraiCode = event.getMessage().serializeToMiraiCode();
        String url = null;
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
        Methods method = Methods.NORMAL;


        if (content.contains(ChatConstant.CHANGE_MODEL) && !id.equals(config.getOwner().substring(1))) {
            event.getFriend().sendMessage("没有权限！");
            return;
        }

        //处理消息头
        if (content.startsWith(ChatConstant.CODE_MESSAGE_START)) {
            content = content.substring(3);
            method = Methods.CODE;
        } else if (content.startsWith(ChatConstant.ONCE_MESSAGE_START)) {
            content = content.substring(1);
            method = Methods.ONCE;
        }
        //发送请求并获取回应
        String response = switch (method) {
            case CODE -> AIUtil.chatCode(Long.valueOf(id), content, url);
            case ONCE -> AIUtil.chatOnce(content, url);
            case NORMAL -> AIUtil.chatNormal(Long.valueOf(id), content, url);
            default -> "ERROR!";
        };
        event.getFriend().sendMessage(response);
    }
}
