package org.example;

public class Message {
    private final Boolean isBot;
    private final String message;

    public Message(boolean isBot, String message){
        this.isBot = isBot;
        this.message = message;
    }

    public Boolean getBot() {
        return isBot;
    }

    public String getMessage() {
        return message;
    }
}
