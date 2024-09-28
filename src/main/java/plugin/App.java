package plugin;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import plugin.listener.UserMessageListener;
import plugin.utils.ConfigUtil;

import java.io.IOException;

import static plugin.utils.ConfigUtil.config;


public final class App extends JavaPlugin {
    public static final App INSTANCE = new App();
    public static MiraiLogger logger;
    private String owner, bot;

    private App() {
        super(new JvmPluginDescriptionBuilder("com.plugin.chatAI-InGroup-v2", "0.1.0")
                .name("ChatAI-InGroup-v2")
                .author("SLHAF")
                .build());
    }


    @Override
    public void onEnable() {
        //加载配置
        try {
            logger = getLogger();
            ConfigUtil.load();
            owner = config.get("owner").substring(1);
            bot = config.get("bot").substring(1);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        getLogger().info("ChatAI-InGroup-v2 loaded!");


        //群聊监听器
        GlobalEventChannel.INSTANCE.filterIsInstance(GroupMessageEvent.class)
                .filter(event -> {
                    String msg = event.getMessage().contentToString();
                    return (msg.startsWith(".") && msg.length() != 1) || msg.startsWith("@"+bot) || msg.startsWith("/c ");
                }).registerListenerHost(new UserMessageListener());

        //私聊监听器
        GlobalEventChannel.INSTANCE.filterIsInstance(FriendMessageEvent.class)
                .filter(event -> true)
                .registerListenerHost(new UserMessageListener());

    }

    @Override
    public void onDisable() {
        getLogger().info("ChatAI-InGroup-v2 disabled!");
    }
}