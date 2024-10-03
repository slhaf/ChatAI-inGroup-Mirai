package plugin.listener;

import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import org.jetbrains.annotations.NotNull;
import plugin.constant.AIConstant;
import plugin.constant.ChatConstant;
import plugin.constant.ConfigConstant;
import plugin.utils.AIUtil;
import plugin.utils.ConfigUtil;

import java.io.IOException;
import java.util.HashMap;

import static plugin.utils.ConfigUtil.getConfig;
import static plugin.utils.ConfigUtil.logger;

/**
 * @author SLHAF
 */
public class OwnerMessageListener extends SimpleListenerHost {

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
        logger.error(exception);
    }

    @EventHandler
    public void onGroupMessageEvent(GroupMessageEvent event) {
        String msg = event.getMessage().contentToString();
        String primaryContent = msg.split(ChatConstant.BLANK)[1];
        if (!primaryContent.contains(ChatConstant.BLANK)) {
            String response;
            response = switch (primaryContent) {
                case "clearAll" -> AIUtil.clearAll();
                case "shutUp" -> ConfigUtil.shutUp(event.getGroup().getId());
                case "speak" -> ConfigUtil.speak(event.getGroup().getId());
                default -> "无对应操作";
            };
            event.getGroup().sendMessage(response);
            return;
        }

        String command = primaryContent.split(ChatConstant.BLANK)[0];
        String arguments = primaryContent.split(ChatConstant.BLANK)[1];
        long id = event.getSender().getId();
        String response;
        try {
            response = handleCommand(command, arguments);
        } catch (Exception e) {
            logger.error(e.toString());
            response = "操作失败!\r\n" + e.getMessage();
        }
        event.getGroup().sendMessage(new At(id).plus(response));
    }

    @EventHandler
    public void onFriendMessageEvent(FriendMessageEvent event) throws IOException {
        String command;
        String arguments;
        try {
            command = event.getMessage().contentToString().split(ChatConstant.BLANK)[0];
            arguments = event.getMessage().contentToString().split(ChatConstant.BLANK)[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            event.getFriend().sendMessage("操作失败，缺少参数");
            return;
        }
        String response = handleCommand(command, arguments);
        event.getFriend().sendMessage(response);
    }

    /**
     * 对命令及其参数进行处理
     *
     * @param command   命令
     * @param arguments 参数
     * @return 处理结果
     */
    private String handleCommand(String command, String arguments) throws IOException {
        return switch (command) {
            case "添加预设" -> {
                if (arguments.matches(ConfigConstant.ADD_CUSTOM)) {
                    String instruction = arguments.split(ConfigConstant.CUSTOM_SPLIT)[0] + ChatConstant.BLANK;
                    String customModel = arguments.split(ConfigConstant.CUSTOM_SPLIT)[1];
                    String customContent = arguments.split(ConfigConstant.CUSTOM_SPLIT)[2];
                    yield ConfigUtil.addCustom(instruction, customModel, customContent);
                } else {
                    yield "格式不正确! 参数格式如下: \r\n指令|模型名称|预设\r\n例: \r\n/example|glm-4-flash|你是...\r\n注：如果不需要预设，可以将预设写为null";
                }
            }
            case "切换模型" -> {
                if (arguments.matches(ConfigConstant.MODEL_CHANGE)) {
                    String instruction = arguments.split(ConfigConstant.CUSTOM_SPLIT)[0] + ChatConstant.BLANK;
                    String modelName = arguments.split(ConfigConstant.CUSTOM_SPLIT)[1];
                    yield ConfigUtil.customModelChange(instruction, modelName);
                } else {
                    yield "格式不正确! 参数格式如下: \r\n指令|指令对应模型";
                }
            }
            case "更改预设" -> {
                if (arguments.matches(ConfigConstant.MODEL_CHANGE)) {
                    String instruction = arguments.split(ConfigConstant.CUSTOM_SPLIT)[0] + ChatConstant.BLANK;
                    String customContent = arguments.split(ConfigConstant.CUSTOM_SPLIT)[1];
                    yield ConfigUtil.customContentChange(instruction, customContent);
                } else {
                    yield "格式不正确! 参数格式如下: \r\n指令|指令对应预设";
                }
            }
            case "删除预设" -> ConfigUtil.removeCustom(arguments);
            default -> "该指令不存在!";
        };
    }
}
