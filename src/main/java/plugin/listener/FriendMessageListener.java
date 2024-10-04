package plugin.listener;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import plugin.constant.ChatConstant;
import plugin.constant.MethodsConstant;
import plugin.utils.AIUtil;
import plugin.utils.ConfigUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author SLHAF
 */
public class FriendMessageListener extends SimpleListenerHost {

    @EventHandler
    public void friendMessageHandler(FriendMessageEvent event) {
        String id = String.valueOf(event.getFriend().getId());
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
        MethodsConstant method = MethodsConstant.NORMAL;

        //处理消息头
        if (ConfigUtil.getConfig().getCustomCommands().containsKey(content.split(ChatConstant.BLANK)[0]+ChatConstant.BLANK)) {
            String[] split = content.split(ChatConstant.BLANK);
            chatCommand = split[0] + ChatConstant.BLANK;
            method = MethodsConstant.CUSTOM;
            content = split[1];
        } else if (content.startsWith(ChatConstant.ONCE_MESSAGE_START)) {
            content = content.substring(1);
            method = MethodsConstant.ONCE;
        }
        //发送请求并获取回应
        String response = switch (method) {
            case CUSTOM -> AIUtil.customChat(Long.valueOf(id), content, url, chatCommand);
            case ONCE -> AIUtil.chatOnce(content, url);
            case NORMAL -> AIUtil.defaultChat(Long.valueOf(id), content, url);
            default -> "ERROR!";
        };
        event.getFriend().sendMessage(response);
    }
}
