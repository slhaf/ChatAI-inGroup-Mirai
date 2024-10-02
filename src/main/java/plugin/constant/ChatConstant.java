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
    public static final String DEFAULT_MESSAGE_START = "@";

    /**
     * 分隔符（空格）
     */
    public static final String BLANK = " ";

    public static final String SET = "/set ";

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

    /**
     * 当前模型
     */
    public static final String CURRENT_MODEL = "当前模型";
}
