package plugin;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import plugin.constant.ChatConstant;
import plugin.listener.FriendMessageListener;
import plugin.listener.GroupMessageListener;
import plugin.listener.OwnerMessageListener;
import plugin.pojo.Config;
import plugin.utils.ConfigUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;


/**
 * @author SLHAF
 */
public final class App extends JavaPlugin {
    public static final App INSTANCE = new App();

    private App() {
        super(new JvmPluginDescriptionBuilder("com.plugin.chatAI-InGroup-v2", "0.1.0")
                .name("ChatAI-InGroup-v2")
                .author("SLHAF")
                .build());
    }


    @Override
    public void onEnable() {
        String owner, bot;
        HashMap<String, String> customCommands;
        List<Long> blacklist;
        //加载配置
        try {
            ConfigUtil.load();
            Thread.sleep(1500);
            Config config = ConfigUtil.getConfig();
            owner = config.getOwner().substring(1);
            bot = config.getBot().substring(1);
            customCommands = config.getCustomCommands();
            blacklist = config.getBlacklist();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        getLogger().info("ChatAI-InGroup-v2 loaded!");

        //群聊监听器
        GlobalEventChannel.INSTANCE.filterIsInstance(GroupMessageEvent.class)
                .filter(event -> {
                    String msg = event.getMessage().contentToString();
                    long groupId = event.getGroup().getId();
                    return ((msg.startsWith(".") && msg.length() != 1) || msg.startsWith("@" + bot) || customCommands.containsKey(msg.split(" ")[0] + ChatConstant.BLANK)) && !blacklist.contains(groupId);
                }).registerListenerHost(new GroupMessageListener());

        //所有者监听--群聊
        GlobalEventChannel.INSTANCE.filterIsInstance(GroupMessageEvent.class)
                .filter(event -> {
                    String msg = event.getMessage().contentToString();
                    String sender = String.valueOf(event.getSender().getId());
                    return msg.startsWith(ChatConstant.SET) && sender.equals(owner);
                }).registerListenerHost(new OwnerMessageListener());

        //私聊监听器
        GlobalEventChannel.INSTANCE.filterIsInstance(FriendMessageEvent.class)
                .filter(event -> {
                    String msg = event.getMessage().contentToString();
                    String sender = String.valueOf(event.getFriend().getId());
                    return !(msg.startsWith(ChatConstant.SET) && sender.equals(owner)) && !msg.equals(ChatConstant.HELP);
                })
                .registerListenerHost(new FriendMessageListener());

        //所有者监听--私聊
        GlobalEventChannel.INSTANCE.filterIsInstance(FriendMessageEvent.class)
                .filter(event -> {
                    String msg = event.getMessage().contentToString();
                    String sender = String.valueOf(event.getFriend().getId());
                    return msg.startsWith(ChatConstant.SET) && sender.equals(owner);
                }).registerListenerHost(new OwnerMessageListener());

        //帮助监听
        GlobalEventChannel.INSTANCE
                .filterIsInstance(MessageEvent.class)
                .filter(event -> event.getMessage().contentToString().equals(ChatConstant.HELP))
                .subscribeAlways(MessageEvent.class, event -> {
                    synchronized (customCommands) {
                        final String[] helpMsg = {"""
                                ————<群聊命令>————
                                
                                @<bot> <content>
                                /<command> <content>
                                
                                例：
                                @机器人 你好
                                /c 你好
                                
                                ————<控制命令>————
                                
                                $ clearAll
                                $ shutUp
                                $ speak
                                $ 添加预设|<预设指令>|<模型名称>|<预设内容>
                                $ 切换模型|<预设指令>|<模型名称>
                                $ 更改预设|<预设指令>|<预设内容>
                                $ 删除预设|<预设指令>
                                
                                例：
                                $ 添加预设|/c|glm-4-flash|你是一只猫娘...
                                
                                """};
                        helpMsg[0] += "————<预设列表>————";
                        customCommands.forEach((s, s2) -> helpMsg[0] += "\r\n\r\n" + s + "-> \r\n" + s2);
                        event.getSubject().sendMessage(helpMsg[0]);
                    }
                });

    }

    @Override
    public void onDisable() {
        getLogger().info("ChatAI-InGroup-v2 disabled!");
    }
}