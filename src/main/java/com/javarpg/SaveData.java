package main.java.com.javarpg;

import java.io.Serializable;

public class SaveData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public Magician player;
    public Inventory inventory;
    public int mapX;
    public int mapY;
    public String mapName; // 新增：保存当前地图名称

    public SaveData(Magician player, Inventory inventory, int mapX, int mapY, String mapName) {
        this.player = player;
        this.inventory = inventory;
        this.mapX = mapX;
        this.mapY = mapY;
        this.mapName = mapName;
    }
}
