package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TelegramBot extends TelegramLongPollingBot {

    private final String key = "7688253824:AAG9UKm8q-yUNXLp4QBApKubt0Aa2ROZjyQ";
    private final String botSystemName = "gfdfgdfgdfg_bot";
    private final String botScreenName = "JavaTelegramBot_test";

    private final HistorySave historySave = new HistorySave();

    private HashMap<Long, User> users = new HashMap<>();

    private boolean isRegister = false;
    private int regStep = 0;

    private boolean isGame = false;
    private int questNum =-1;

    @Override
    public String getBotUsername() {return botSystemName;}
    @Override
    public String getBotToken() {return key;}

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            String userMessage = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            updateHistory(update, chatId, userMessage);
            historySave.save(false,chatId,userMessage);
            if (isRegister){
                register(chatId,userMessage);
            }else if (isGame){
                game(chatId,userMessage);
            } else if (userMessage.startsWith("/")){
                commands(update);
            } else {
                sendYes(chatId, userMessage);
            }

//            System.out.println("Получено сообщение: " + userMessage);

//            if (userMessage.equals("/start")||userMessage.toLowerCase().equals("привіт")){
//                send(chatId,"Привіт! Хочеш пограти? Якщо так напиши /new і я загадаю число від 0 до 100, а ти будеш вгадувати))");
//            }else if (userMessage.equals("/new")){
//                newNum(chatId);
//            }else if (questNum!=-1){
//                pazzle(chatId,userMessage);
//            }else {
//                send(chatId,"Число ще не загадане напишіть /new");
//            }

        }else {
            System.out.println(update.getMessage());
            System.out.println("Отримано не текстове повідомлення");
        }
    }

    private void updateHistory(Update update, long chatId, String userMessage) {
        if (users.get(chatId) == null) {
            users.put(chatId,new User(update.getMessage().getFrom().getUserName(), chatId));
        }
        users.get(chatId).addMessage(false, userMessage);
    }

    private void commands(Update update){
        String command = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        if (command.equals("/history")){
            sendHistory(chatId,users.get(chatId).getHistory());
        } else if (command.equals("/remove_history")){
            users.get(chatId).clearHistory();
            send(chatId,"Історію очищено!");
        } else if (command.equals("/register")){
            if (regStep == 3){
                send(chatId,"Ви вже пройшли реєстацію");
            }else {
                isRegister = true;
                send(chatId,"Реєстрація");
                send(chatId,"Ваше ім'я "+update.getMessage().getFrom().getUserName()+"?\n Так / Ні");
            }
        } else if (command.equals("/my_user_data")){
            getUserData(chatId);
        } else if (command.equals("/start_game")){
            isGame = true;
            newNum(chatId);
            send(chatId,"Число загадане");
        }
        else {
            send(chatId,"Команда не знайдена");
        }
    }

    private void getUserData(long id){
        String userData="";
        userData+="Ім'я: "+users.get(id).getName()+"\n";
        userData+="ID: "+users.get(id).getId()+"\n";
        if (users.get(id).getBirthday() != null){
            userData+="Дата народження: "+users.get(id).getBirthday()+"\n";
        }
        if (users.get(id).getCity()!=null){
            userData+="Місто: "+users.get(id).getCity();
        }
        send(id,userData);
    }

    private void register(long chatId, String userMessage){
        if (regStep == 0){
            if (userMessage.equals("Так")){
                send(chatId,"Ім'я підтверджене!");
                regStep+=2;
                send(chatId,"Введіть дату народження вигляду DD.MM.YYYY");
            } else if (userMessage.equals("Ні")){
                regStep++;
                send(chatId,"Введіть справжнє ім'я");
            } else {
                send(chatId,"Невірна відповіть, спробуйте ще раз.");
            }
        } else if (regStep==1){
            users.get(chatId).setName(userMessage);
            send(chatId,"Ім'я збережено!");
            regStep++;
            send(chatId,"Введіть дату народження вигляду DD.MM.YYYY");
        } else if (regStep == 2){
            users.get(chatId).setDate(userMessage);
            send(chatId,"Дату збережено!");
            regStep++;
            send(chatId,"Введіть назву міста, де ви зараз живете");
        } else if (regStep==3){
            users.get(chatId).setCity(userMessage);
            send(chatId,"Місто збережено!");
            send(chatId,"Реєстрацію завершено");
            isRegister = false;
        }
    }

    private void newNum(long chatId) {
        Random r = new Random();
        questNum = r.nextInt(0,101);
    }

    private void game(long chatId, String userMessage) {
        int num = Integer.valueOf(userMessage);
        if (num == questNum){
            send(chatId,"Ти переміг! Гра завершена.");
            isGame = false;
        }
        else if (questNum < num)
            send(chatId,"Загадане число менше");
        else if (questNum > num)
            send(chatId,"Загадане число більше");
    }

    private void sendOutHistory(long chatId, String botMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(botMessage);
        historySave.save(true,chatId,botMessage);
        try {
            execute(message); // типо подтвердить отправку
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void send(long chatId, String botMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(botMessage);
        users.get(chatId).addMessage(true, message.getText());
        historySave.save(true,chatId,message.getText());
        try {
            execute(message); // типо подтвердить отправку
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendYes(long chatId, String userMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Так");
        users.get(chatId).addMessage(true, message.getText());
        historySave.save(true,chatId,message.getText());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendHistory(long chatId, ArrayList<Message> messages) {
        String historyMs = "";
        for (int i = 0; i < messages.size(); i++) {
            String person = "";
            if (messages.get(i).getBot()){
                person = "Бот: ";
            } else {
                person = "Ви: ";
            }
            historyMs+= person+messages.get(i).getMessage()+"\n";
        }
        if (historyMs.isEmpty()){
            send(chatId,"Історія порожня");
        } else {
            send(chatId,"Історія:");
            sendOutHistory(chatId, historyMs);
        }
    }
}
































