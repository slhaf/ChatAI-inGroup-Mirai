package plugin.utils;

import cn.hutool.core.bean.BeanUtil;
import net.mamoe.mirai.utils.LoggerAdapters;
import net.mamoe.mirai.utils.MiraiLogger;
import org.slf4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import plugin.constant.ChatConstant;
import plugin.constant.ConfigConstant;
import plugin.pojo.Config;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


/**
 * @author SLHAF
 */
public class ConfigUtil {
    private static final String CONFIG_PATH = "./config/ChatAIinGroup/config.yaml";
    private static final Yaml yaml;
    private static Config config;
    private static final Logger log = org. slf4j. LoggerFactory. getLogger("ChatAIinGroup");
    public static MiraiLogger logger = LoggerAdapters.asMiraiLogger(log);;

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
            ArrayList<Long> blacklist = new ArrayList<>();
            blacklist.add(123456789L);
            blacklist.add(987654321L);
            config.setBlacklist(blacklist);
            LinkedHashMap<String, String> commands = new LinkedHashMap<>();
            commands.put("default", "null");
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


    public static String customModelChange(String instruction, String customModel) throws IOException {
        HashMap<String, String> customCommands = config.getCustomCommands();
        if (!customCommands.containsKey(instruction)) {
            return "该预设不存在!";
        }
        String customContent = customCommands.get(instruction).split(ConfigConstant.CUSTOM_SPLIT)[1];
        customCommands.put(instruction, customModel + "|" + customContent);
        dump();
        return "模型切换成功: [" + instruction + "->" + customCommands.get(instruction) + "]";
    }

    private static void dump() throws IOException {
        FileWriter writer = new FileWriter(CONFIG_PATH);
        yaml.dump(BeanUtil.beanToMap(config), writer);
    }

    /**
     * 关闭当前群聊发言
     *
     * @param id 群聊id
     */
    public static String shutUp(long id) {
        config.getBlacklist().add(id);
        return "ChatAI-InGroup 已关闭";
    }

    /**
     * 开启当前群聊发言
     *
     * @param id 群聊id
     */
    public static String speak(long id) {
        config.getBlacklist().remove(id);
        return "ChatAI-InGroup 已开启";
    }

    /**
     * 添加预设
     *
     * @param instruction   指令
     * @param customModel   预设模型
     * @param customContent 预设内容
     * @return 运行结果
     * @throws IOException 配置保存出错
     */
    public static String addCustom(String instruction, String customModel, String customContent) throws IOException {
        HashMap<String, String> customCommands = config.getCustomCommands();
        if (customCommands.containsKey(instruction)) {
            return "已存在当前指令! \r\n[" + instruction + "->" + customCommands.get(instruction) + "]";
        }
        String content = customModel + "|" + customContent;
        customCommands.put(instruction, content);
        dump();
        return "预设添加完毕! \r\n[" + instruction + "->" + content + "]";
    }

    /**
     * 改变指令对应预设
     *
     * @param instruction   指令
     * @param customContent 预设内容
     * @return 运行结果
     */
    public static String customContentChange(String instruction, String customContent) throws IOException {
        HashMap<String, String> customCommands = config.getCustomCommands();
        if (!customCommands.containsKey(instruction)) {
            return "该指令不存在!";
        }
        String modelName = customCommands.get(instruction).split(ConfigConstant.CUSTOM_SPLIT)[0];
        customCommands.put(instruction, modelName + "|" + customContent);
        dump();
        return "预设更改成功! \r\n[" + instruction + "->" + customCommands.get(instruction) + "]";
    }

    public static String removeCustom(String arguments) throws IOException {
        HashMap<String, String> customCommands = getConfig().getCustomCommands();
        if (!customCommands.containsKey(arguments + ChatConstant.BLANK)) {
            return "该预设不存在";
        }
        String content = customCommands.get(arguments + ChatConstant.BLANK);
        customCommands.remove(arguments + ChatConstant.BLANK);
        dump();
        return "删除预设[" + arguments + "->" + content + "]成功";
    }
}
