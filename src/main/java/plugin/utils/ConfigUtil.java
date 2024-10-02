package plugin.utils;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.utils.LoggerAdapters;
import net.mamoe.mirai.utils.MiraiLogger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import plugin.constant.ConfigConstant;
import plugin.pojo.Config;

import java.io.*;
import java.util.LinkedHashMap;


/**
 * @author SLHAF
 */
@Slf4j
public class ConfigUtil {
    private static final String CONFIG_PATH = "./config/ChatAIinGroup/config.yaml";
    private static final Yaml yaml;
    private static Config config;
    public static MiraiLogger logger = LoggerAdapters.asMiraiLogger(log);

    private ConfigUtil() {
    }

    static {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
        config = new Config();
    }

    /**
     * 检查配置
     *
     * @throws IOException 配置文件写入出错
     */
    public static void load() throws IOException, ClassNotFoundException {
        //检查配置文件
        File file = new File(CONFIG_PATH);
        if (!file.exists()) {
            //创建配置文件
            file.getParentFile().mkdirs();
            file.createNewFile();
            config.setApikey("your_zhipu_apikey");
            config.setAccessKeyId("your_ali_access_key_id");
            config.setAccessKeySecret("your_ali_access_key_secret");
            config.setOwner("your_bot_owner_qq_number(e.g. Q1145141919810)");
            config.setDefaultModel("glm-4-flash");
            config.setBot("your_bot_qq_number(e.g. Q1145141919810)");
            config.setTimeout("M3600000");
            config.setTimeCheck("M60000");
            LinkedHashMap<String, String> commands = new LinkedHashMap<>();
            commands.put("default","glm-4-flash|null");
            commands.put("/c ", "glm-4-flash|你是一位智能编程助手，你会为用户回答关于编程、代码、计算机方面的任何问题，并提供格式规范、可以执行、准确安全的代码，并在必要时提供详细的解释。 请用中文回答。");
            commands.put("/example ", "模型名称|预设内容");
            config.setCustomCommands(commands);
            dump();
            logger.warning("配置文件创建成功，请关闭后进行配置");
            System.exit(0);
        } else {
            //读取配置文件
            InputStream inputStream = new FileInputStream(CONFIG_PATH);
            config = BeanUtil.toBean(yaml.load(new FileInputStream(CONFIG_PATH)), Config.class);
            inputStream.close();
            logger.info(config.toString());
            logger.info("读取配置文件完毕");
            Class.forName("plugin.utils.AIUtil");
            Class.forName("plugin.utils.OCRUtil");
        }
    }

    public static Config getConfig() {
        return config;
    }

    /**
     * 配置改变（模型）
     *
     * @param modelName 模型名称
     */
    public static void defaultModelChange(String modelName) {
        try {
            config.setDefaultModel(modelName);
            dump();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void customModelChange(String command,String modelName) {
        try {
            String customContent = config.getCustomCommands().get(command).split(ConfigConstant.CUSTOM_SPLIT)[1];
            config.getCustomCommands().put(command,modelName+ConfigConstant.CUSTOM_SPLIT+customContent);
            dump();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void dump() throws IOException {
        yaml.dump(BeanUtil.beanToMap(config), new FileWriter(CONFIG_PATH));
    }
}
