package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Random;

public class TelegramBot extends TelegramLongPollingBot {
    private final String key = "7688253824:AAHPnk7iMtVnljx_s7I9zVFM4yhBq6GNYlo";
    private final String botSystemName = "gfdfgdfgdfg_bot";
    private final String botScreenName = "JavaTelegramBot_test";
    private int questNum =-1;

    @Override
    public String getBotUsername() {
        return botSystemName;
    }

    @Override
    public String getBotToken() {
        return key;
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            String userMessage = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            System.out.println("Получено сообщение: " + userMessage);

            if (userMessage.equals("/start")||userMessage.toLowerCase().equals("привіт")){
                send(chatId,"Привіт! Хочеш пограти? Якщо так напиши /new і я загадаю число від 0 до 10, а ти будеш вгадувати))");
            }else if (userMessage.equals("/new")){
                newNum(chatId);
            }else if (questNum!=-1){
                pazzle(chatId,userMessage);
            }else {
                send(chatId,"Число ще не загадане напишіть /new");
            }

//            send(chatId, userMessage);

        }else {
            System.out.println(update.getMessage());
            System.out.println("Отримано не текстове повідомлення");
        }
    }

    private void newNum(long chatId) {
        Random r = new Random();
        questNum = r.nextInt(0,11);
        send(chatId, "Число загадане");
    }

    private void pazzle(long chatId, String userMessage) {
        int num = Integer.valueOf(userMessage);
        if (num == questNum){
            send(chatId,"Ти переміг");
            newNum(chatId);
        }
        else if (questNum < num)
            send(chatId,"Загадане число менше");
        else if (questNum > num)
            send(chatId,"Загадане число більше");




    }

    private void send(long chatId, String userMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(userMessage);

        try {
            execute(message); // типо подтвердить отправку
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
