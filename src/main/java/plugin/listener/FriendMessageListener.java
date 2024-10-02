package plugin.listener;

import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import plugin.constant.ChatConstant;
import plugin.constant.MethodsConstant;
import plugin.pojo.Config;
import plugin.utils.AIUtil;
import plugin.utils.ConfigUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FriendMessageListener extends SimpleListenerHost {

    private static final Config config = ConfigUtil.getConfig();

    @EventHandler
    public void friendMessageHandler(FriendMessageEvent event) {
        /*String id = String.valueOf(event.getFriend().getId());
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
        MethodsConstant method = MethodsConstant.NORMAL;


        if (content.contains(ChatConstant.CHANGE_MODEL) && !id.equals(config.getOwner().substring(1))) {
            event.getFriend().sendMessage("没有权限！");
            return;
        }

        //处理消息头
        if (content.startsWith(ChatConstant.CODE_MESSAGE_START)) {
            content = content.substring(3);
            method = MethodsConstant.CUSTOM;
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
        event.getFriend().sendMessage(response);*/
    }
}
