package plugin.pojo;

/**
 * @author SLHAF
 */
public class UserCustomLatestTime {
    private String chatCommand;
    private Long latestTime;

    public UserCustomLatestTime() {
    }

    public UserCustomLatestTime(String chatCommand, Long latestTime) {
        this.chatCommand = chatCommand;
        this.latestTime = latestTime;
    }

    /**
     * 获取
     * @return chatCommand
     */
    public String getChatCommand() {
        return chatCommand;
    }

    /**
     * 设置
     * @param chatCommand
     */
    public void setChatCommand(String chatCommand) {
        this.chatCommand = chatCommand;
    }

    /**
     * 获取
     * @return latestTime
     */
    public Long getLatestTime() {
        return latestTime;
    }

    /**
     * 设置
     * @param latestTime
     */
    public void setLatestTime(Long latestTime) {
        this.latestTime = latestTime;
    }

    public String toString() {
        return "UserCustomLatestTime{chatCommand = " + chatCommand + ", latestTime = " + latestTime + "}";
    }
}
