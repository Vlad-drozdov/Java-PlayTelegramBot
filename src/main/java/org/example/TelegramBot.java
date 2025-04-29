package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {

    private final BotPassword password =  new BotPassword();

    private final HistorySave historySave = new HistorySave();


    private HashMap<Long, User> users = new HashMap<>();

    private boolean isRegister = false;
    private int regStep = 0;

    private boolean isGame1 = false;
    private int questNum =-1;

    private boolean isGame2 = false;
    private Message gameMessage = null;
    private InlineKeyboardMarkup game2Markup;
    private int xGame2 = 0;
    private int yGame2 = 0;


    @Override
    public String getBotUsername() {return password.getBotScreenName();}
    @Override
    public String getBotToken() {return password.getKey();}

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            String userMessage = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            updateHistory(update, chatId, userMessage);
            historySave.save(false,chatId,userMessage);
            if (isRegister){
                register(chatId,userMessage);
            }else if (isGame1){
                game1(chatId,userMessage);
            } else if (userMessage.startsWith("/")){
                commands(update);
            } else {
                sendYes(chatId, userMessage);
            }

        }else if (update.hasCallbackQuery()) {

            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals("yes")) {
                register(chatId,"Так");
            } else if (callbackData.equals("no")) {
                register(chatId,"Ні");
            } else if (callbackData.equals("up")){
                xGame2--;
                if (xGame2<0){
                    xGame2=0;
                    send(chatId,"Не можна виходити за межі");
                }else {
                    game2(chatId);
                }
            } else if (callbackData.equals("down")){
                xGame2++;
                if (xGame2>3){
                    xGame2=3;
                    send(chatId,"Не можна виходити за межі");
                } else {
                    game2(chatId);
                }
            }else if (callbackData.equals("left")){
                yGame2--;
                if (yGame2<0){
                    yGame2=0;
                    send(chatId,"Не можна виходити за межі");
                }else {
                    game2(chatId);
                }
            }else if (callbackData.equals("right")){
                yGame2++;
                if (yGame2>4){
                    yGame2=4;
                    send(chatId,"Не можна виходити за межі");
                }else {
                    game2(chatId);
                }
            }
        }
        else {
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

        if (command.equals("/start")){
           send(chatId, "Привіт!)");
        } else if (command.equals("/history")){
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
                sendButtons(chatId,"Ваше ім'я "+update.getMessage().getFrom().getUserName()+"?");
            }
        } else if (command.equals("/my_user_data")){
            getUserData(chatId);
        } else if (command.equals("/start_game1")){
            isGame1 = true;
            newNum(chatId);
            send(chatId,"Число загадане");
        }else if (command.equals("/start_game2")){
            isGame2 = true;
            xGame2 = 0;
            yGame2 = 0;
            sendArrowButton(chatId,"Ваша позиція: 1");
        } else {
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
                System.out.println("ok");
                send(chatId,"Ім'я підтверджене!");
                regStep+=2;
                send(chatId,"Введіть дату народження вигляду DD.MM.YYYY");
            } else if (userMessage.equals("Ні")){
                System.out.println("ok");
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

    private void game1(long chatId, String userMessage) {
        int num = Integer.valueOf(userMessage);
        if (num == questNum){
            send(chatId,"Ти переміг! Гра завершена.");
            isGame1 = false;
        }
        else if (questNum < num)
            send(chatId,"Загадане число менше");
        else if (questNum > num)
            send(chatId,"Загадане число більше");
    }

    private void game2(long chatId){
        int[][] arr = {
                {1, 2, 3, 4, 5},
                {6, 7, 8, 9, 10},
                {11, 12, 13, 14, 15},
                {16, 17, 18, 19, 20}
        };
        int position = arr[xGame2][yGame2];

        editMessageWithButtons(chatId,gameMessage.getMessageId(),"Ваша позиція: "+position,game2Markup);
    }

    private void editMessageWithButtons(long chatId, int messageId, String newText,InlineKeyboardMarkup markup) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(String.valueOf(chatId));
        editMessage.setMessageId(messageId);
        editMessage.setText(newText);
        editMessage.setReplyMarkup(markup);

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendArrowButton(long chatId, String msg) {
        InlineKeyboardButton up = new InlineKeyboardButton();
        up.setText("⬆️");
        up.setCallbackData("up");

        InlineKeyboardButton down = new InlineKeyboardButton();
        down.setText("⬇️");
        down.setCallbackData("down");

        InlineKeyboardButton right = new InlineKeyboardButton();
        right.setText("➡️");
        right.setCallbackData("right");

        InlineKeyboardButton left = new InlineKeyboardButton();
        left.setText("⬅️");
        left.setCallbackData("left");

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(Arrays.asList(up));
        rows.add(Arrays.asList(left,right));
        rows.add(Arrays.asList(down));

        game2Markup = new InlineKeyboardMarkup();
        game2Markup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(msg);
        message.setReplyMarkup(game2Markup);

        try {
            Message sent = execute(message);
            gameMessage = sent;
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendButtons(long chatId, String msg) {
        InlineKeyboardButton yes = new InlineKeyboardButton();
        yes.setText("Так");
        yes.setCallbackData("yes");

        InlineKeyboardButton no = new InlineKeyboardButton();
        no.setText("Ні");
        no.setCallbackData("no");

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(Arrays.asList(yes,no));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(msg);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendOutHistory(long chatId, String botMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(botMessage);
        historySave.save(true,chatId,botMessage);
        try {
            execute(message);
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
            execute(message);
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

    private void sendHistory(long chatId, ArrayList<LocalMessage> messages) {
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

