package pojo;

import java.util.List;

public class Data {

    /**
     * id
     */
    private String id;
    /**
     * created
     */
    private long created;
    /**
     * model
     */
    private String model;
    /**
     * choices
     */
    private List<Choices> choices;
    /**
     * status
     */
    private String status;
    /**
     * assistant_id
     */
    private String assistant_id;
    /**
     * conversation_id
     */
    private String conversation_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Choices> getChoices() {
        return choices;
    }

    public void setChoices(List<Choices> choices) {
        this.choices = choices;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssistant_id() {
        return assistant_id;
    }

    public void setAssistant_id(String assistant_id) {
        this.assistant_id = assistant_id;
    }

    public String getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }


}
