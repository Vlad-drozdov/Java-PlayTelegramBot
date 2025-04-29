package org.example;

public class LocalMessage {
    private final Boolean isBot;
    private final String message;

    public LocalMessage(boolean isBot, String message){
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
