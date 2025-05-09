package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {

    private final BotPassword password =  new BotPassword();
    private final HistorySave historySave = new HistorySave();
    private final MessageManager msg = new MessageManager(this);

    private HashMap<Long, User> users = new HashMap<>();

    private Room room1 = new Room("1");
    private Room room2 = new Room("2");

    private int questNum =-1;
    private int[][] arr = {
            {1, 2, 3, 4, 5},
            {6, 7, 8, 9, 10},
            {11, 12, 13, 14, 15},
            {16, 17, 18, 19, 20}
    };


    private InlineKeyboardMarkup game2Markup;


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
        }
        else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals("yes")) {
                register(chatId,"Так");
            }
            else if (callbackData.equals("no")) {
                register(chatId,"Ні");
            }
            else if (callbackData.equals("up")){
                users.get(chatId).setxGame2(users.get(chatId).getxGame2()-1);
                if (users.get(chatId).getxGame2()<0){
                    users.get(chatId).setxGame2(0);
                    msg.send(chatId,"Не можна виходити за межі");
                }else {
                    game2(chatId);
                }
            }
            else if (callbackData.equals("down")){
                users.get(chatId).setxGame2(users.get(chatId).getxGame2()+1);
                if (users.get(chatId).getxGame2()>3){
                    users.get(chatId).setxGame2(3);
                    msg.send(chatId,"Не можна виходити за межі");
                } else {
                    game2(chatId);
                }
            }
            else if (callbackData.equals("left")){
                users.get(chatId).setyGame2(users.get(chatId).getyGame2()-1);
                if (users.get(chatId).getyGame2()<0){
                    users.get(chatId).setyGame2(0);
                    msg.send(chatId,"Не можна виходити за межі");
                }else {
                    game2(chatId);
                }
            }
            else if (callbackData.equals("right")){
                users.get(chatId).setyGame2(users.get(chatId).getyGame2()+1);
                if (users.get(chatId).getyGame2()>4){
                    users.get(chatId).setyGame2(4);
                    msg.send(chatId,"Не можна виходити за межі");
                }else {
                    game2(chatId);
                }
            }
            else if (callbackData.equals("room1")){
                room1.roomHandler(this,msg,update,room1,room2);
            }
            else if (callbackData.equals("room2")){
                room2.roomHandler(this,msg,update,room1,room2);
            }
            else if (callbackData.equals("exitRoom")){
                room1.exitRoom(this,msg,room1,room2,chatId);
            }
            else if (callbackData.equals("rock") || callbackData.equals("paper") || callbackData.equals("scissors")||callbackData.equals("again")||callbackData.equals("exit")) {
                Room r = users.get(chatId).getRoom();
                long P1 = r.getPlId().getFirst();
                long P2 = r.getPlId().getLast();
                long opponentId = (chatId == P1) ? P2 : P1;
                User player = users.get(chatId);
                User opponent = users.get(opponentId);

                if (callbackData.equals("again")){
                    player.setOnlineGameAgain(true);
                    if (r.getRoomSeats()!=0){
                        msg.send(chatId,"Опонент покинув гру");
                        r.exitOnlineGame(this,msg,chatId);
                    }
                    else if (!opponent.isOnlineGameAgain()){
                        player.setOnlineGameChoiceMessage(msg.ReturnAndSendMessageWithButtons(chatId,"Очікування опонента...",null));
                    }
                    else if (player.isOnlineGameAgain()&&opponent.isOnlineGameAgain()){
                        player.setOnlineGameChoiceMessage(msg.ReturnAndSendMessageWithButtons(chatId,"Оберіть: ",onlineGameButtons()));
                        opponent.setOnlineGameChoiceMessage(msg.ReturnAndSendMessageWithButtons(opponentId,"Оберіть: ",onlineGameButtons()));
                        player.setOnlineGameAgain(false);
                        opponent.setOnlineGameAgain(false);
                        return;
                    }
                }
                else if (callbackData.equals("exit")){
                    r.exitOnlineGame(this,msg,chatId);
                    return;
                }

                switch (callbackData) {
                    case "rock":
                        player.setChoice("🪨");
                        break;
                    case "paper":
                        player.setChoice("📃");
                        break;
                    case "scissors":
                        player.setChoice("✂️");
                        break;
                }

                String playerChoice = player.getChoice();
                String opponentChoice = opponent.getChoice();

                if (opponentChoice != null) {
                    msg.editMessageWithButtons(chatId,player.getOnlineGameChoiceMessage().getMessageId(), "Ваш вибір: " + playerChoice + "\nПротивник вибрав: " + opponentChoice + "\n" + getGameResult(playerChoice, opponentChoice),onlineGameAgain());
                    msg.editMessageWithButtons(opponentId,opponent.getOnlineGameChoiceMessage().getMessageId(), "Ваш вибір: " + opponentChoice + "\nПротивник вибрав: " + playerChoice + "\n" + getGameResult(opponentChoice, playerChoice),onlineGameAgain());
                    Countdown10S countdown10S = new Countdown10S(this,msg,r,player,opponent);
                    countdown10S.start();
                    player.setChoice(null);
                    opponent.setChoice(null);

                } else {
                    msg.editMessage(chatId, player.getOnlineGameChoiceMessage().getMessageId(), "Ви обрали " + playerChoice + "\nОчікування опонента...");
                }
            }


        }
        else {
            System.out.println(update.getMessage());
            System.out.println("Отримано не текстове повідомлення");
        }
    }

    public String getGameResult(String choice1, String choice2) {
        if (choice1.equals(choice2)) {
            return "☮️Нічия!";
        }
        else if ((choice1.equals("🪨") && choice2.equals("✂️")) ||
                (choice1.equals("📃") && choice2.equals("🪨")) ||
                (choice1.equals("✂️") && choice2.equals("📃"))) {
            return "✅Ви перемогли!";
        } else {
            return "❌Ви програли!";
        }
    }

    public void updateHistory(Update update, long chatId, String userMessage) {
        if (users.get(chatId) == null) {
            users.put(chatId,new User(update.getMessage().getFrom().getUserName(), chatId));
        }
        users.get(chatId).addMessage(false, userMessage);
    }

    public void commands(Update update) {
        String command = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        handleCommand(command, chatId, update);
    }

    public void commands(String command, long chatId) {
        handleCommand(command, chatId, null);
    }

    private void handleCommand(String command, long chatId, Update update) {
        if (command.equals("/start")) {
            msg.send(chatId, "Привіт!)");
        }
        else if (command.equals("/history")) {
            msg.sendHistory(chatId, users.get(chatId).getHistory());
        }
        else if (command.equals("/remove_history")) {
            users.get(chatId).clearHistory();
            msg.send(chatId, "Історію очищено!");
        }
        else if (command.equals("/register")) {
            if (users.get(chatId).getRegStep() == 3) {
                msg.send(chatId, "Ви вже пройшли реєстацію");
            } else {
                users.get(chatId).setRegister(true);
                msg.send(chatId, "Реєстрація");
                String name = (update != null) ? update.getMessage().getFrom().getUserName() : "користувач";
                buttonsYesNo(chatId, "Ваше ім'я " + name + "?");
            }
        }
        else if (command.equals("/my_user_data")) {
            getUserData(chatId);
        }
        else if (command.equals("/start_game1")) {
            users.get(chatId).setGame1(true);
            newNum(chatId);
            msg.send(chatId, "Число загадане від 1 до 100");
        }
        else if (command.equals("/start_game2")) {
            users.get(chatId).setGame2(true);
            String num = historySave.loadNumGame2(chatId);
            if (!num.isEmpty() && !users.get(chatId).isGame2IsLoad()) {
                for (int i = 0; i < arr.length; i++) {
                    for (int j = 0; j < arr[i].length; j++) {
                        if (arr[i][j] == Integer.parseInt(num)) {
                            users.get(chatId).setxGame2(i);
                            users.get(chatId).setyGame2(j);
                        }
                    }
                }
                users.get(chatId).setGame2IsLoad(true);
            }
            if (!num.isEmpty()) {
                game2Buttons(chatId, "Ваша позиція: " + num);
            } else {
                game2Buttons(chatId, "Ваша позиція: 1");
            }
        }
        else if (command.equals("/online_game")) {
            users.get(chatId).setOnlineGame(true);
            buttonsOnlineRooms(chatId, "Оберіть кімнату");
        }
        else {
            msg.send(chatId, "Команда не знайдена");
        }
    }

    public void getUserData(long id){
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

    public void register(long chatId, String userMessage){
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

    public void newNum(long chatId) {
        Random r = new Random();
        questNum = r.nextInt(1,100);
    }

    public void game1(long chatId, String userMessage) {
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

    public void game2(long chatId){

        int position = arr[users.get(chatId).getxGame2()][users.get(chatId).getyGame2()];

        historySave.saveNumGame2(chatId,position+"");

        msg.editMessageWithButtons(chatId, users.get(chatId).getGame2Message().getMessageId(),"Ваша позиція: "+position,game2Markup);
    }

    public InlineKeyboardButton createButton(String text, String callbackData) {
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

        users.get(chatId).setGame2Message(this.msg.ReturnAndSendMessageWithButtons(chatId,msg,game2Markup));

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

    public void buttonsOnlineRooms(long chatId, String msg) {
        InlineKeyboardMarkup markup = editButtonsOnlineRooms(chatId, room1.getPlayerIn(), room2.getPlayerIn());

        users.get(chatId).setOnlineGameRoomMessage(this.msg.ReturnAndSendMessageWithButtons(chatId,msg,markup));
    }

    public InlineKeyboardMarkup editButtonsOnlineRooms(long chatId, String r1, String r2) {
        InlineKeyboardButton room1 = createButton("1."+r1,"room1");
        InlineKeyboardButton room2 = createButton("2."+r2,"room2");
        InlineKeyboardButton exit = createButton("Вийти","exitRoom");

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(Arrays.asList(room1));
        rows.add(Arrays.asList(room2));
        if (users.get(chatId).getRoom()!=null){
            rows.add(Arrays.asList(exit));
        }


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup onlineGameButtons(){
        InlineKeyboardButton rock = createButton("🪨","rock");
        InlineKeyboardButton paper = createButton("📃","paper");
        InlineKeyboardButton scissors = createButton("✂️","scissors");

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(Arrays.asList(rock,paper,scissors));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup onlineGameAgain(){
        InlineKeyboardButton again = createButton("Зіграти ще раз","again");
        InlineKeyboardButton exit = createButton("Вийти","exit");


        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(Arrays.asList(again,exit));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }

    public HashMap<Long, User> getUsers() {return users;}

    public Room getRoom2() {return room2;}

    public Room getRoom1() {return room1;}
}

class Countdown extends Thread {
    private final TelegramBot bot;
    private final MessageManager msg;
    private final Room room;
    private final long id1;
    private final long id2;

    public Countdown(TelegramBot bot, Room room, MessageManager msg, long id1, long id2) {
        this.bot = bot;
        this.room = room;
        this.msg = msg;
        this.id1 = id1;
        this.id2 = id2;
    }

    @Override
    public void run() {
        for (int i = 3; i >= 0; i--) {
            if (room.getRoomSeats() == 0) {
                if (i >= 1) {
                    msg.send(id1, i + "");
                    msg.send(id2, i + "");
                } else {
                    room.setReady(false);
                    bot.getUsers().get(id1).setOnlineGameChoiceMessage(msg.ReturnAndSendMessageWithButtons(id1,"Оберіть: ",bot.onlineGameButtons()));
                    bot.getUsers().get(id2).setOnlineGameChoiceMessage(msg.ReturnAndSendMessageWithButtons(id2,"Оберіть: ",bot.onlineGameButtons()));
                    msg.deleteMessage(id1,bot.getUsers().get(id1).getOnlineGameRoomMessage().getMessageId());
                    msg.deleteMessage(id2,bot.getUsers().get(id2).getOnlineGameRoomMessage().getMessageId());
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    msg.send(id1, "Помилка очікування");
                    msg.send(id2, "Помилка очікування");
                    return;
                }
            } else {
                msg.send(id1, "Помилка: гравець покинув кімнату");
                msg.send(id2, "Помилка: гравець покинув кімнату");
                room.setReady(false);
                return;
            }
        }
    }
}
class Countdown10S extends Thread {

    private final TelegramBot bot;
    private Room r;
    private User player;
    private User opponent;
    private MessageManager msg;


    public Countdown10S(TelegramBot bot,MessageManager msg,Room r,User player, User opponent) {
        this.bot = bot;
        this.msg = msg;
        this.r = r;
        this.player = player;
        this.opponent = opponent;
    }

    @Override
    public void run() {
        for (int i = 10; i > 0; i--) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        if (player.isOnlineGame()&&opponent.isOnlineGame()){
            r.exitOnlineGame(bot,msg,player.getId());
            r.exitOnlineGame(bot,msg,opponent.getId());
        }else {
            if (!player.isOnlineGame()){
                r.exitOnlineGame(bot,msg,opponent.getId());
            }else if (!opponent.isOnlineGame()){
                r.exitOnlineGame(bot,msg,player.getId());
            }
        }


    }
}


