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
}