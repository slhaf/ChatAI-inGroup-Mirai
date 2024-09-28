package plugin.utils;

import cn.hutool.core.bean.BeanUtil;
import lombok.Getter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import plugin.pojo.Config;

import java.io.*;
import java.util.HashMap;

import static plugin.App.logger;

/**
 * @author SLHAF
 */
public class ConfigUtil {
    private static final String CONFIG_PATH = "./config/ChatAIinGroup/config.yaml";
    private static final Yaml yaml;
    private static Config config;

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
            config.setModelNormal("glm-4-flash");
            config.setModelCode("glm-4-flash");
            config.setBot("your_bot_qq_number(e.g. Q1145141919810)");
            config.setTimeout("M3600000");
            config.setTimeCheck("M60000");
            HashMap<String, String> commands = new HashMap<>();
            commands.put("/c ", "你是一位智能编程助手，你会为用户回答关于编程、代码、计算机方面的任何问题，并提供格式规范、可以执行、准确安全的代码，并在必要时提供详细的解释。 请用中文回答。");
            commands.put("/example", "预设内容");
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
    public static void modelNormalChange(String modelName) {
        try {
            config.setModelNormal(modelName);
            dump();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void modelCodeChange(String modelName) {
        try {
            config.setModelCode(modelName);
            dump();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void dump() throws IOException {
        yaml.dump(BeanUtil.beanToMap(config), new FileWriter(CONFIG_PATH));
    }
}
