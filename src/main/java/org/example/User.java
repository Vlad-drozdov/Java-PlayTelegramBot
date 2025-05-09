package org.example;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class User{

    private String name;
    private final long id;
    private LocalDate birthday;
    private String city;
    ArrayList<LocalMessage> history = new ArrayList<>();

    private boolean isRegister = false;
    private int regStep = 0;

    private boolean isGame2 = false;
    private Message game2Message = null;
    private boolean isGame2IsLoad = false;
    private int xGame2 = 0;
    private int yGame2 = 0;

    private boolean isGame1 = false;

    private Message onlineGameRoomMessage = null;
    private boolean isOnlineGame = false;
    private String choice = null;
    private Message onlineGameChoiceMessage = null;
    private Room room = null;
    private boolean onlineGameAgain = false;

    public User(String name, long id){
        this.name = name;
        this.id = id;
    }

    public void addMessage(boolean isBot, String message) {
        history.add(new LocalMessage(isBot,message));
    }

    public void clearHistory() {
        history.clear();
    }

    public ArrayList<LocalMessage> getHistory() {
        return history;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        birthday = LocalDate.parse(date,formatter);
    }

    public void setCity(String city){
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getCity() {
        return city;
    }

    public boolean isRegister() {
        return isRegister;
    }

    public int getRegStep() {
        return regStep;

    }

    public boolean isGame2() {
        return isGame2;
    }

    public boolean isGame1() {
        return isGame1;
    }

    public void setGame2(boolean game2) {
        isGame2 = game2;
    }

    public void setGame1(boolean game1) {
        isGame1 = game1;
    }

    public void setRegister(boolean register) {
        isRegister = register;
    }

    public void setRegStep(int regStep) {
        this.regStep = regStep;
    }

    public int getyGame2() {
        return yGame2;
    }

    public void setyGame2(int yGame2) {
        this.yGame2 = yGame2;
    }

    public int getxGame2() {
        return xGame2;
    }

    public void setxGame2(int xGame2) {
        this.xGame2 = xGame2;
    }

    public boolean isGame2IsLoad() {
        return isGame2IsLoad;
    }

    public void setGame2IsLoad(boolean game2IsLoad) {
        isGame2IsLoad = game2IsLoad;
    }

    public boolean isOnlineGame() {
        return isOnlineGame;
    }

    public void setOnlineGame(boolean onlineGame) {
        isOnlineGame = onlineGame;
    }

    public Message getGame2Message() {
        return game2Message;
    }

    public void setGame2Message(Message game2Message) {
        this.game2Message = game2Message;
    }

    public Message getOnlineGameRoomMessage() {
        return onlineGameRoomMessage;
    }

    public void setOnlineGameRoomMessage(Message onlineGameRoomMessage) {
        this.onlineGameRoomMessage = onlineGameRoomMessage;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public Message getOnlineGameChoiceMessage() {
        return onlineGameChoiceMessage;
    }

    public void setOnlineGameChoiceMessage(Message onlineGameChoiceMessage) {
        this.onlineGameChoiceMessage = onlineGameChoiceMessage;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public boolean isOnlineGameAgain() {
        return onlineGameAgain;
    }

    public void setOnlineGameAgain(boolean onlineGameAgain) {
        this.onlineGameAgain = onlineGameAgain;
    }
}