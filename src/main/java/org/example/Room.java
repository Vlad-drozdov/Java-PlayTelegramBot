package org.example;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Room {
    private HashMap<Long, String> players = new HashMap<>(2);
    private String num = "";
    private String playerIn = "";
    private boolean ready = false;


    public Room(String number){
        this.num = number;
    }

    public String getNum() {
        return num;
    }

    public void addPlayer(long id, String userName){
        players.put(id,userName);
    }

    public void removePlayer(long id){
        players.remove(id);
    }


    public int getRoomSeats(){
        int seats = 2;
        return seats- players.size();
    }

    public String getPlayerIn() {
        List<String> userNames = new ArrayList<>(players.values());
        int p = userNames.size();
        if (p == 1) {
            playerIn = "\n" + userNames.get(0);
        } else if (p == 2) {
            playerIn = "\n" + userNames.get(0) + "\n" + userNames.get(1);
        }
        return playerIn;
    }

    public void clearPlayerIn(){
        playerIn = "";
        players.clear();
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public ArrayList<Long> getPlId(){
        return new ArrayList<>(players.keySet());
    }

    public void roomHandler(TelegramBot bot, MessageManager msg, Update update,Room r1, Room r2){
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        if (getRoomSeats()==0){
            msg.send(chatId,"Кімната "+getNum()+" заповнена"); // сообщение
        }
        else if (bot.getUsers().get(chatId).getRoom()!=null){
            msg.send(chatId,"Ви вже в кімнаті "+ getNum()); // сообщение
        }
        else {
            addPlayer(chatId,update.getCallbackQuery().getFrom().getUserName()==null ? update.getCallbackQuery().getFrom().getFirstName() : update.getCallbackQuery().getFrom().getUserName());
            bot.getUsers().get(chatId).setRoom(this);
            updateRooms(bot,msg,r1,r2);
            msg.send(chatId,"Ви приєдналися до "+getNum()+" кімнати");// сообщение
            if (getRoomSeats()==0){
                setReady(true);
                ArrayList<Long> Ids = getPlId();
                Countdown countdown = new Countdown(bot,this,msg,Ids.getFirst(),Ids.getLast());
                countdown.start();
            }
        }
    }

    public static void exitRoom(TelegramBot bot,MessageManager msg, Room r1, Room r2, long chatId){
        if (bot.getUsers().get(chatId).getRoom().getNum().equals("1")){
            r1.removePlayer(chatId);
            r1.clearPlayerIn();
        }
        if (bot.getUsers().get(chatId).getRoom().getNum().equals("2")){
            r2.removePlayer(chatId);
            r2.clearPlayerIn();
        }
        updateRooms(bot,msg,r1,r2);
        msg.send(chatId,"Ви вийшли з кімнати "+ bot.getUsers().get(chatId).getRoom().getNum());
        bot.getUsers().get(chatId).setRoom(null);
    }

    public static void updateRooms(TelegramBot bot,MessageManager msg,Room r1, Room r2){
        for (User u : bot.getUsers().values()) {
            if (u.isOnlineGame() && u.getOnlineGameRoomMessage() != null) {
                try {
                    msg.editMessageWithButtons(u.getId(), u.getOnlineGameRoomMessage().getMessageId(), "Оберіть кімнату", bot.editButtonsOnlineRooms(u.getId(),r1.getPlayerIn(), r2.getPlayerIn()));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Помилка при оновленні повідомлення користувача " + u.getId() + ": " + e.getMessage());
                    u.setOnlineGame(false);
                    msg.deleteMessage(u.getId(),u.getOnlineGameRoomMessage().getMessageId());
                    u.setOnlineGameRoomMessage(null);
                }
            }
        }
    }

    public void exitOnlineGame(TelegramBot bot, MessageManager msg,long chatId){
        msg.messageRemoveButtons(chatId,bot.getUsers().get(chatId).getOnlineGameChoiceMessage());
        removePlayer(chatId);
        clearPlayerIn();
        bot.getUsers().get(chatId).setChoice(null);
        bot.getUsers().get(chatId).setOnlineGame(false);
        bot.getUsers().get(chatId).setRoom(null);

    }
}
