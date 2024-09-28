package plugin.utils;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;

import static plugin.App.logger;

/**
 * @author SLHAF
 */
public class ConfigUtil {
    public static HashMap<String, String> config;
    private static final String CONFIG_PATH = "./config/ChatAIinGroup/config.yaml";
    private static final Yaml yaml;

    private ConfigUtil() {
    }

    static{
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
    }

    /**
     * 检查配置
     * @throws IOException 配置文件写入出错
     */
    public static void load() throws IOException, ClassNotFoundException {
        //检查配置文件
        File file = new File(CONFIG_PATH);
        if (!file.exists()) {
            //创建配置文件
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("apikey: \r\n");
            writer.write("accessKeyId: \r\n");
            writer.write("accessKeySecret: \r\n");
            writer.write("owner: \r\n");
            writer.write("model_normal: \r\n");
            writer.write("model_code: \r\n");
            writer.write("bot: \r\n");
            writer.write("timeout: M3600000\r\n");
            writer.write("time_check: M60000");
            writer.flush();
            writer.close();
            logger.warning("配置文件创建成功，请关闭后进行配置");
            System.exit(0);
        } else {
            //读取配置文件
            InputStream inputStream = new FileInputStream(CONFIG_PATH);
            config = yaml.load(inputStream);
            inputStream.close();
            logger.info(config.toString());
            logger.info("读取配置文件完毕");
            Class.forName("plugin.utils.AIUtil");
            Class.forName("plugin.utils.OCRUtil");
        }
    }

    /**
     * 配置改变（模型）
     * @param modelName 模型名称
     */
    public static void modelNormalChange(String modelName) {
        try {
            config.put("model_normal", modelName);
            yaml.dump(config, new FileWriter(CONFIG_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void modelCodeChange(String modelName) {
        try {
            config.put("model_code", modelName);
            yaml.dump(config, new FileWriter(CONFIG_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
