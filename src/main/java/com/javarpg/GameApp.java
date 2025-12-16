package main.java.com.javarpg;

import javax.swing.JOptionPane; 

public class GameApp {
    public static void main(String[] args) {
        String story = "在一个遥远的魔法王国，黑暗势力逐渐蔓延。\n" +
                       "年轻的魔法师cyxxx啊，你肩负着拯救世界的使命。\n" +
                       "踏上旅程，与邪恶的生物战斗，揭开古老的秘密，成为传说中的英雄！";
        JOptionPane.showMessageDialog(null, story, "游戏开始", JOptionPane.INFORMATION_MESSAGE);

        Magician player = new Magician();
        new GameMenu(player); 

    }
}
