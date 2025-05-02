package org.example;

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

    private boolean isGame1 = false;

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
}