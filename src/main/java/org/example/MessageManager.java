package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;

public class MessageManager {
    private final TelegramBot bot;

    public MessageManager(TelegramBot bot){
        this.bot = bot;
    }

    public void deleteMessage(long chatId, int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(chatId));
        deleteMessage.setMessageId(messageId);
        try {
            bot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            System.out.println("Ошибка при удалении сообщения: " + e.getMessage());
        }
    }



    public void sendMessageWithButtons(long chatId, String text , InlineKeyboardMarkup markup) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(markup);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public Message ReturnAndSendMessageWithButtons(long chatId,String msg,InlineKeyboardMarkup markup){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(msg);
        message.setReplyMarkup(markup);
        try {
            return bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void editMessageWithButtons(long chatId, int messageId, String newText,InlineKeyboardMarkup markup) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setMessageId(messageId);
        editMessage.setText(newText);
        editMessage.setReplyMarkup(markup);
        try {
            bot.execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void messageRemoveButtons(long chatId, Message m) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setMessageId(m.getMessageId());
        editMessage.setText(m.getText());
        editMessage.setReplyMarkup(null);
        try {
            bot.execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void editMessage(long chatId, int messageId, String newText) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setMessageId(messageId);
        editMessage.setText(newText);
        try {
            bot.execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendOutHistory(long chatId, String botMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(botMessage);
        HistorySave.save(true,chatId,botMessage);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void send(long chatId, String botMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(botMessage);
        bot.getUsers().get(chatId).addMessage(true, message.getText());
        HistorySave.save(true,chatId,message.getText());
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendYes(long chatId, String userMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Так");
        bot.getUsers().get(chatId).addMessage(true, message.getText());
        HistorySave.save(true,chatId,message.getText());
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendHistory(long chatId, ArrayList<LocalMessage> messages) {
        String historyMs = "";
        for (LocalMessage message : messages) {
            String person = "";
            if (message.getBot()) {
                person = "Бот: ";
            } else {
                person = "Ви: ";
            }
            historyMs += person + message.getMessage() + "\n";
        }
        if (historyMs.isEmpty()){
            send(chatId,"Історія порожня");
        } else {
            send(chatId,"Історія:");
            sendOutHistory(chatId, historyMs);
        }
    }
}
