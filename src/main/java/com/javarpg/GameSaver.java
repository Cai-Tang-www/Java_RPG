package main.java.com.javarpg;

import java.io.*;

public class GameSaver {
    // 存档文件路径
    private static final String SAVE_FILE_PATH = "savegame.dat";

    // 保存游戏
    public static boolean saveGame(SaveData data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE_PATH))) {
            oos.writeObject(data);
            System.out.println("游戏已保存至: " + new File(SAVE_FILE_PATH).getAbsolutePath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 读取游戏
    public static SaveData loadGame() {
        File file = new File(SAVE_FILE_PATH);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (SaveData) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // 检查是否存在存档
    public static boolean hasSaveFile() {
        return new File(SAVE_FILE_PATH).exists();
    }
}
