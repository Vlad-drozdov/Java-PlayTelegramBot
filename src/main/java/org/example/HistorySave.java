package org.example;

import java.io.*;

public class HistorySave {

    public static void save(boolean isBot,long id,String data){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/historyes/"+id+".txt",true))) {
            writer.write(isBot+" "+data);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Помилка збереження файлу: " + e.getMessage());
        }
    }

    public static void saveNumGame2(long id,String data){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/game2positions/"+id+".txt"))) {
            writer.write(data);
        } catch (IOException e) {
            System.err.println("Помилка збереження файлу: " + e.getMessage());
        }
    }

    public static String loadNumGame2(long id) {
        String content = "";
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/game2positions/"+id+".txt"))) {
            content = reader.readLine();
            if (content == null){
                return "";
            }
        } catch (IOException e){
            System.err.println("Помилка завантаження файлу: " + e.getMessage());
        }
        return content;
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
