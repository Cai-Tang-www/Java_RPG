package main.java.com.javarpg;

public class GameApp {
    public static void main(String[] args) {
        Magician player = new Magician();
        Godzila monster = new Godzila();
        new GameMenu(player, monster);
        //new IfFight();
        //new Fight();
    }
}
