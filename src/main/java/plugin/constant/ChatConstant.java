package plugin.constant;

/**
 * @author SLHAF
 */
public class ChatConstant {

    /**
     * 匹配含有图片信息的消息
     */
    public static final String MATCH_MESSAGE = ".*[mirai:image:(https?://[\\w./?&amp;=]+)].*";

    /**
     * 匹配图片信息
     */
    public static final String MATCH_IMAGE = "\\[mirai:image:(.*?)]";

    /**
     * 单次对话标志
     */
    public static final String ONCE_MESSAGE_START = ".";

    /**
     * 普通对话标志
     */
    public static final String NORMAL_MESSAGE_START = "@";

    /**
     * code对话标志
     */
    public static final String CODE_MESSAGE_START = "/c ";

    /**
     * 切换模型
     */
    public static final String CHANGE_MODEL = "切换模型";

    /**
     * 所有者
     */
    public static final String OWNER = "owner";

    /**
     * 清理消息
     */
    public static final String CLEAR = "clear";
}
