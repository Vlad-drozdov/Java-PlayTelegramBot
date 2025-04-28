package org.example;

import java.io.*;

public class HistorySave {

    public static void save(boolean isBot,long userId,String data){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/historyes/"+userId+".txt",true))) {
            writer.write(isBot+" "+data);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Помилка збереження файлу: " + e.getMessage());
        }
    }

//    public static String load(String data) {
//        String content = "";
//        try (BufferedReader reader = new BufferedReader(new FileReader("src/files/file.txt"))) {
//            String line;
//            while ((line = reader.readLine()) != null){
//                content = line + "\n";
//            }
//            System.out.println();
//            System.out.println("Файл успішно завантажено");
//        } catch (IOException e){
//            System.err.println("Помилка завантаження файлу: " + e.getMessage());
//        }
//        return content;
//    }
}
