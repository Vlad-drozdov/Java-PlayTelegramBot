package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {

    private final BotPassword password =  new BotPassword();
    private final HistorySave historySave = new HistorySave();
    private final MessageManager msg = new MessageManager(this);

    private HashMap<Long, User> users = new HashMap<>();

    private int questNum =-1;


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
            if (users.get(chatId).isRegister()){
                register(chatId,userMessage);
            }else if (users.get(chatId).isGame1()){
                game1(chatId,userMessage);
            } else if (userMessage.startsWith("/")){
                commands(update);
            } else {
                msg.sendYes(chatId, userMessage);
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
                    msg.send(chatId,"Не можна виходити за межі");
                }else {
                    game2(chatId);
                }
            } else if (callbackData.equals("down")){
                xGame2++;
                if (xGame2>3){
                    xGame2=3;
                    msg.send(chatId,"Не можна виходити за межі");
                } else {
                    game2(chatId);
                }
            }else if (callbackData.equals("left")){
                yGame2--;
                if (yGame2<0){
                    yGame2=0;
                    msg.send(chatId,"Не можна виходити за межі");
                }else {
                    game2(chatId);
                }
            }else if (callbackData.equals("right")){
                yGame2++;
                if (yGame2>4){
                    yGame2=4;
                    msg.send(chatId,"Не можна виходити за межі");
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
           msg.send(chatId, "Привіт!)");
        } else if (command.equals("/history")){
            msg.sendHistory(chatId,users.get(chatId).getHistory());
        } else if (command.equals("/remove_history")){
            users.get(chatId).clearHistory();
            msg.send(chatId,"Історію очищено!");
        } else if (command.equals("/register")){
            if (users.get(chatId).getRegStep() == 3){
                msg.send(chatId,"Ви вже пройшли реєстацію");
            }else {
                users.get(chatId).setRegister(true);
                msg.send(chatId,"Реєстрація");
                buttonsYesNo(chatId,"Ваше ім'я "+update.getMessage().getFrom().getUserName()+"?");
            }
        } else if (command.equals("/my_user_data")){
            getUserData(chatId);
        } else if (command.equals("/start_game1")){
            users.get(chatId).setGame1(true);
            newNum(chatId);
            msg.send(chatId,"Число загадане");
        }else if (command.equals("/start_game2")){
            users.get(chatId).setGame2(true);
            xGame2 = 0;
            yGame2 = 0;
            game2Buttons(chatId,"Ваша позиція: 1");
        } else {
            msg.send(chatId,"Команда не знайдена");
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
        msg.send(id,userData);
    }

    private void register(long chatId, String userMessage){
        if (users.get(chatId).getRegStep() == 0){
            if (userMessage.equals("Так")){
                System.out.println("ok");
                msg.send(chatId,"Ім'я підтверджене!");
                users.get(chatId).setRegStep(users.get(chatId).getRegStep()+2);
                msg.send(chatId,"Введіть дату народження вигляду DD.MM.YYYY");
            } else if (userMessage.equals("Ні")){
                System.out.println("ok");
                users.get(chatId).setRegStep(users.get(chatId).getRegStep()+1);
                msg.send(chatId,"Введіть справжнє ім'я");
            } else {
                msg.send(chatId,"Невірна відповіть, спробуйте ще раз.");
            }
        } else if (users.get(chatId).getRegStep()==1){
            users.get(chatId).setName(userMessage);
            msg.send(chatId,"Ім'я збережено!");
            users.get(chatId).setRegStep(users.get(chatId).getRegStep()+1);
            msg.send(chatId,"Введіть дату народження вигляду DD.MM.YYYY");
        } else if (users.get(chatId).getRegStep() == 2){
            users.get(chatId).setDate(userMessage);
            msg.send(chatId,"Дату збережено!");
            users.get(chatId).setRegStep(users.get(chatId).getRegStep()+1);
            msg.send(chatId,"Введіть назву міста, де ви зараз живете");
        } else if (users.get(chatId).getRegStep()==3){
            users.get(chatId).setCity(userMessage);
            msg.send(chatId,"Місто збережено!");
            msg.send(chatId,"Реєстрацію завершено");
            users.get(chatId).setRegister(false);
        }
    }

    private void newNum(long chatId) {
        Random r = new Random();
        questNum = r.nextInt(0,101);
    }

    private void game1(long chatId, String userMessage) {
        int num = Integer.valueOf(userMessage);
        if (num == questNum){
            msg.send(chatId,"Ти переміг! Гра завершена.");
            users.get(chatId).setGame1(false);
        }
        else if (questNum < num)
            msg.send(chatId,"Загадане число менше");
        else if (questNum > num)
            msg.send(chatId,"Загадане число більше");
    }

    private void game2(long chatId){
        int[][] arr = {
                {1, 2, 3, 4, 5},
                {6, 7, 8, 9, 10},
                {11, 12, 13, 14, 15},
                {16, 17, 18, 19, 20}
        };
        int position = arr[xGame2][yGame2];

        msg.editMessageWithButtons(chatId,gameMessage.getMessageId(),"Ваша позиція: "+position,game2Markup);
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    public void game2Buttons(long chatId, String msg) {
        InlineKeyboardButton up = createButton("⬆️","up");
        InlineKeyboardButton down = createButton("⬇️","down");
        InlineKeyboardButton right = createButton("➡️","right");
        InlineKeyboardButton left = createButton("⬅️","left");

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

    public void buttonsYesNo(long chatId, String msg) {
        InlineKeyboardButton yes = createButton("Так","yes");
        InlineKeyboardButton no = createButton("Ні","no");

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(Arrays.asList(yes,no));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);

        this.msg.sendMessageWithButtons(chatId, msg, markup);
    }

    public HistorySave getHistorySave() {
        return historySave;
    }

    public HashMap<Long, User> getUsers() {
        return users;
    }

}

