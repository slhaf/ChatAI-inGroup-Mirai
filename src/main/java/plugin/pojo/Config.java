package plugin.pojo;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Config {
    /**
     * 智谱apikey
     */
    private String apikey;

    /**
     * 阿里accessKey
     */
    private String accessKeyId;
    private String accessKeySecret;

    /**
     * 基础配置
     */
    private String owner;
    private String defaultModel;
    private String bot;
    private String timeout;
    private String timeCheck;

    /**
     * 自定义预设
     */
    private LinkedHashMap<String,String> customCommands;

    public Config() {
    }

    public Config(String apikey, String accessKeyId, String accessKeySecret, String owner, String defaultModel, String bot, String timeout, String timeCheck, LinkedHashMap<String, String> customCommands) {
        this.apikey = apikey;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.owner = owner;
        this.defaultModel = defaultModel;
        this.bot = bot;
        this.timeout = timeout;
        this.timeCheck = timeCheck;
        this.customCommands = customCommands;
    }

    /**
     * 获取
     * @return apikey
     */
    public String getApikey() {
        return apikey;
    }

    /**
     * 设置
     * @param apikey
     */
    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    /**
     * 获取
     * @return accessKeyId
     */
    public String getAccessKeyId() {
        return accessKeyId;
    }

    /**
     * 设置
     * @param accessKeyId
     */
    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    /**
     * 获取
     * @return accessKeySecret
     */
    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    /**
     * 设置
     * @param accessKeySecret
     */
    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    /**
     * 获取
     * @return owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * 设置
     * @param owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * 获取
     * @return modelNormal
     */
    public String getDefaultModel() {
        return defaultModel;
    }

    /**
     * 设置
     * @param defaultModel
     */
    public void setDefaultModel(String defaultModel) {
        this.defaultModel = defaultModel;
    }

    /**
     * 获取
     * @return bot
     */
    public String getBot() {
        return bot;
    }

    /**
     * 设置
     * @param bot
     */
    public void setBot(String bot) {
        this.bot = bot;
    }

    /**
     * 获取
     * @return timeout
     */
    public String getTimeout() {
        return timeout;
    }

    /**
     * 设置
     * @param timeout
     */
    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    /**
     * 获取
     * @return timeCheck
     */
    public String getTimeCheck() {
        return timeCheck;
    }

    /**
     * 设置
     * @param timeCheck
     */
    public void setTimeCheck(String timeCheck) {
        this.timeCheck = timeCheck;
    }

    /**
     * 获取
     * @return customCommands
     */
    public HashMap<String, String> getCustomCommands() {
        return customCommands;
    }

    /**
     * 设置
     * @param customCommands
     */
    public void setCustomCommands(LinkedHashMap<String, String> customCommands) {
        this.customCommands = customCommands;
    }

    @Override
    public String toString() {
        return "Config{apikey = " + apikey + ", accessKeyId = " + accessKeyId + ", accessKeySecret = " + accessKeySecret + ", owner = " + owner + ", modelNormal = " + defaultModel + ", modelCode = " +  ", bot = " + bot + ", timeout = " + timeout + ", timeCheck = " + timeCheck + ", customCommands = " + customCommands + "}";
    }
}
