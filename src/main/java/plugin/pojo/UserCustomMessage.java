package plugin.pojo;

import com.zhipu.oapi.service.v4.model.ChatMessage;

import java.util.List;

public class UserCustomMessage {
    private String command;
    private List<ChatMessage> messages;


    public UserCustomMessage() {
    }

    public UserCustomMessage(String command, List<ChatMessage> messages) {
        this.command = command;
        this.messages = messages;
    }

    /**
     * 获取
     * @return command
     */
    public String getCommand() {
        return command;
    }

    /**
     * 设置
     * @param command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * 获取
     * @return messages
     */
    public List<ChatMessage> getMessages() {
        return messages;
    }

    /**
     * 设置
     * @param messages
     */
    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "UserCustomMessage{command = " + command + ", messages = " + messages + "}";
    }
}
