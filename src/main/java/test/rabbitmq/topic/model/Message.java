package test.rabbitmq.topic.model;

public class Message {
    public String key;

    public Message(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
