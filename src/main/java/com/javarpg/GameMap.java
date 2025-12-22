package main.java.com.javarpg;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.AbstractMap;
import java.lang.Math;

public class GameMap extends JFrame implements KeyListener, MouseListener {
    // 核心对象
    private Magician m;
    // private Godzila g;
    
    // **新增：背景地图/地形数组**
    // 存储地图符号：# 墙壁，. 路径，^ 玩家初始位，! 怪物刷新区

    private int h_size=5;
    private int w_size=5;

    // 玩家在地图中的坐标 (y=行, x=列)
    private int village_x = 1;
    private int village_y = 4;
    private int forest_x = 0;
    private int forest_y = 2; 
    private int cave_x = 0;
    private int cave_y = 15; 
    private int x,y;
    //存放不同地图实例
    private boolean win=false;
    //不同场景地图的存放哈希表
    private Map<String,String[][]>maps=new HashMap<>();
    //不同场景存放角色起始位置的哈希表
    private Map<String,AbstractMap.SimpleEntry<Integer,Integer>>mapspos=new HashMap<>();
    
    //不同场景存放角色数组的哈希表
    private Map<String,ArrayList<ArrayList<Character>>>gmaps =new HashMap<>();
    //不同场景存放NPC实例的哈希表
    private Map<String, Map<String, NPC>> npcMaps = new HashMap<>();


    // 实体地图：仅用于存放 Character 实体（Player/Enemy），空位为 null
    private ArrayList<ArrayList<Character>> gm=new ArrayList<>();

    //不同场景地图的地形数组
    private String [][] terrain;
    // NPC 地图：存放 NPC 实例
    private Map<String,NPC>npcMap=new HashMap<>();
    
    // 战斗引擎：处理所有战斗计算
    private BattleEngine battleEngine = new BattleEngine();
    // 背包
    private Inventory inventory = new Inventory();
    
    // UI 组件：用于实时更新战斗信息
    private JLabel enemyInfo;
    private JLabel playerInfo;
    private JPanel mapJPanel;
    
    private int mhp_initial; // 初始/最大HP用于显示
    private int ghp_initial;
    private String showmapName;
    private String mapName="home";
    
    public GameMap(Magician m,String showmapName) {
        this.m = m;
        this.showmapName=showmapName;
        // 保存初始最大值，用于 UI 显示
        this.mhp_initial = m.getMaxHP();

        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initJFrame();
                initMap();
                initPos();
                initGM(); // 现在只初始化实体列表 gm
                addGameMap();
                setVisible(true);
            }
        });
    }


    private void initJFrame() {
        this.setSize(1000,800);
        this.setTitle("Java RPG - 地图探索");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridLayout(h_size,w_size));
        this.addKeyListener(this);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 处理窗口关闭
                inventory.saveInventory();
                System.exit(0);
            }
        });
    }

    public void initMap(){
        String [][] villagemap={
            {"#", "#", "#", "#", "#"}, 
            {"#", "#", "#", "#", "#"}, 
            {"#", "#", ".", ".", "!"},
            {"N", ".", ".", "#", "#"},
            {"#", "O", "#", "#", "#"}
        };
        String [][] forestmap={
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#"}, 
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"0", ".", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", "#", "N", "#", "#", "#", "#", "#", "#"},
            {"N", ".", ".", ".", "#", "#", "#", "#", "#", "#"},
            {"#", "#", ".", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", ".", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", ".", ".", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", ".", ".", ".", ".", ".", "#", "#"},
            {"#", "#", "#", "#", ".", "#", "#", "!", "#", "#"},
        };
        String [][] cavemap={
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"}, 
            {"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", "N", ".", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", "#", ".", ".", ".", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", ".", ".", "N", ".", "#", "#", "#", "#", "#", "#"},
            {"#", "#", "#", "#", "#", "#", ".", "#", "#", ".", ".", ".", ".", "#", "#", "#"},
            {"#", "#", "#", "#", "N", ".", ".", "#", "#", "#", "#", "N", ".", ".", "#", "#"},
            {"#", "#", "#", "#", ".", ".", "N", "#", "#", "#", "#", "#", "#", ".", ".", "#"},
            {"#", "#", "#", "#", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", ".", "!"},
            {"#", "#", "#", ".", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", "#", ".", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", "N", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
            {"#", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"}, 
            {"0", ".", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
        };
        maps.put("village", villagemap);
        maps.put("forest", forestmap);
        maps.put("cave", cavemap);

    }
    public void initPos(){
        mapspos.put("village", new AbstractMap.SimpleEntry<>(village_x,village_y));
        mapspos.put("forest", new AbstractMap.SimpleEntry<>(forest_x,forest_y));
        mapspos.put("cave", new AbstractMap.SimpleEntry<>(cave_x,cave_y));
    }
    public void initGM() {
        // 根据 terrain 数组的大小初始化 gm
        for (Map.Entry<String, String[][]> entry : maps.entrySet()) {
            String mapName = entry.getKey();
            String[][] mapData = entry.getValue();
            ArrayList<ArrayList<Character>> now = new ArrayList<>();
            Map<String, NPC> nowNPC=new HashMap<>();
            for (int i = 0; i < mapData.length; i++) {
                ArrayList<Character> row = new ArrayList<>();
                for (int j = 0; j < mapData[i].length; j++) {
                    if (mapData[i][j].equals("N")){
                        String npc_y=Integer.valueOf(i).toString();
                        String npc_x=Integer.valueOf(j).toString();
                        nowNPC.put(npc_y+","+npc_x, new NPC("村民", java.util.Arrays.asList("你好，冒险者！", "最近村子不太平，小心行事。", "愿星光指引你的道路。")));
                    }
                    row.add(null); 
                }
                now.add(row);
            }
            npcMaps.put(mapName, nowNPC);
            int x=mapspos.get(mapName).getKey();
            int y=mapspos.get(mapName).getValue();
            if (y >= 0 && y < now.size() && x >= 0 && x < now.get(y).size()) {
                now.get(y).set(x, m);
            }
            gmaps.put(mapName, now);
        }
        //NPC
    }

    // 修正：使用 terrain 数组和 gm 列表共同渲染
    private void addGameMap() {
        if (win){
            show("恭喜你，勇者，战胜了十恶不赦的哥斯拉，但是你的冒险还没有结束，道路的前方依旧充满着未知，坚持下去吧，勇者！");
        }
        this.getContentPane().removeAll();
        if (this.mapName!=this.showmapName){
            this.x=mapspos.get(showmapName).getKey();
            this.y=mapspos.get(showmapName).getValue();
            this.gm=gmaps.get(showmapName);
            this.npcMap=npcMaps.get(showmapName);
            this.terrain=maps.get(showmapName);
            this.mapName=showmapName;
        }
        int yup=Math.max(0,y-h_size/2);
        int xleft=Math.max(0,x-w_size/2);
        int ydown=Math.min(terrain.length-1,y+h_size/2);
        int xright=Math.min(terrain[0].length-1,x+w_size/2);
        if (Integer.valueOf(yup).equals(0)){
            ydown=4;
        }else if (Integer.valueOf(ydown).equals(terrain.length-1)){
            yup=terrain.length-5;
        }
        if (Integer.valueOf(xleft).equals(0)){
            xright=4;
        }else if (Integer.valueOf(xright).equals(terrain[0].length-1)){
            xleft=terrain[0].length-5;
        }


        for (int i = yup; i <= ydown; i++) {
            for (int j = xleft; j <= xright; j++) {
                String symbol = terrain[i][j]; // 获取背景符号

                // 如果该位置有 Character 实体 (Player/Enemy)
                if (gm.get(i).get(j) != null) {
                    symbol = "^"; // 玩家显示为 '^'
                }

                JLabel jlabel = new JLabel(symbol);
                jlabel.setFont(new Font("Monospaced", Font.BOLD, 24)); 
                jlabel.setForeground(symbol.equals("#") ? Color.GRAY : Color.BLACK);
               
                this.getContentPane().add(jlabel);
            }
        }
        this.getContentPane().revalidate(); // 重新验证组件层次结构
        this.getContentPane().repaint();    // 重绘面板
    }
    
    
    // --- 键盘事件处理：移动逻辑 ---

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        // 记录旧位置
        int oldX = x;
        int oldY = y;
        // 尝试移动
        if (e.getKeyCode() == KeyEvent.VK_W) { // 上
            y--;
        } else if (e.getKeyCode() == KeyEvent.VK_S) { // 下
            y++;
        } else if (e.getKeyCode() == KeyEvent.VK_A) { // 左
            x--;
        } else if (e.getKeyCode() == KeyEvent.VK_D) { // 右
            x++;
        }else if (e.getKeyCode()==KeyEvent.VK_Q){ // 打开背包
            inventory.showInventory(m,this);
        }

        // 边界和碰撞检测
        if (y < 0 || y >= terrain.length || x < 0 || x >= terrain[0].length || terrain[y][x].equals("#")) {
            // 移动无效，恢复位置
            x = oldX;
            y = oldY;
            return; 
        }

        // 1. 清除旧位置的实体
        gm.get(oldY).set(oldX, null);
        
        // 2. 将玩家实体放置到新位置
        gm.get(y).set(x, m);

        // 3. 检查是否触发事件 
        String currentTerrain = terrain[y][x];
        if (currentTerrain.equals("N")) {
            String npcKey = y + "," + x;
            if (npcMap.containsKey(npcKey)) {
                NPC npc = npcMap.get(npcKey);
                show(npc.getRandomDialogue());
            }
        } else {
            Random random = new Random();
            double rate  = 0.2;
            if(terrain[y][x].equals(".")){
                if(random.nextDouble()<rate){
                    System.out.println("遇到敌人");
                    Enemy encounteredEnemy ;
                    int enemyType = random.nextInt(2);
                    if (enemyType == 0) {
                        encounteredEnemy = new Monster("史莱姆", 50, 0, 10, 5, 1, 10);
                    } else {
                        encounteredEnemy = new Monster("哥布林", 70, 10, 15, 8, 2, 15);
                    }
                    fight(encounteredEnemy);
                }
            }if(terrain[y][x].equals("!")){
                    System.out.println("遇到敌人");
                    Enemy encounteredEnemy ;
                    encounteredEnemy = new Godzila();
                    fight(encounteredEnemy);
            }
        }
        
        
        // 4. 刷新地图 UI
        addGameMap();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // 可以在此处设置一个调试用的战斗触发
            // fight(); 
        }
    }
    
    // ... (MouseListener 接口方法，保持为空) ...
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}


    // ---------------------------------------------------
    // 战斗 UI 和逻辑
    // ---------------------------------------------------

    public void fight(Enemy g) {
        // 确保战斗只在玩家存活时开始
        if (!m.isAlive()) {
             show("角色已死亡，无法战斗。");
             return;
        }

        JFrame fightFrame = new JFrame("战斗中...");
        fightFrame.setSize(500, 300);
        fightFrame.setLocationRelativeTo(this);
        fightFrame.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        fightFrame.setAlwaysOnTop(true);
        fightFrame.setFocusable(true); // 确保窗口可以获取焦点
        fightFrame.requestFocusInWindow(); // 立即请求焦点
        // 初始化血量信息标签
        enemyInfo = new JLabel(g.getName() + " HP: " + g.getHP() + "/" + g.getMaxHP());
        playerInfo = new JLabel(m.getName() + " HP: " + m.getHP() + "/" + m.getMaxHP() + " MP: " + m.getMP() + "/" + m.getMaxMP());

        fightFrame.add(enemyInfo);
        fightFrame.add(playerInfo);

        //道具按键监听
        fightFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    endBattle(fightFrame, false,g);
                }else if(e.getKeyCode() == KeyEvent.VK_1){
                    inventory.useItem(m, "HP");
                    updateCombatUI(g);
                }else if(e.getKeyCode() == KeyEvent.VK_2){
                    inventory.useItem(m, "MP");
                    updateCombatUI(g);
                }
            }
        });
        //背包
        JButton inventoryButton=new JButton("背包");
        inventoryButton.addActionListener(e->{
            Inventoryabout(fightFrame,g);
            fightFrame.requestFocusInWindow();
        });
        fightFrame.add(inventoryButton);

        // 攻击按钮 (普攻)
        JButton basicAttack = new JButton("普攻");
        basicAttack.addActionListener(e -> {
            // 调用 Magician 的方法，Magician 将任务委托给 BattleEngine
            playerAction(m.useBasicAttack(g, battleEngine), fightFrame, g);
            fightFrame.requestFocusInWindow();
        });
        fightFrame.add(basicAttack);

        // 小技能按钮
        JButton smallSkill = new JButton("日之呼吸—炎舞(小技能) (MP: 2)");
        smallSkill.addActionListener(e -> {
            playerAction(m.useSmallSkill(g, battleEngine), fightFrame,g);
            fightFrame.requestFocusInWindow();
        });
        fightFrame.add(smallSkill);

        // 大招按钮
        JButton bigSkill = new JButton("爆裂魔法Explosion(大技能) (MP: 4)");
        bigSkill.addActionListener(e -> {
            playerAction(m.useBigSkill(g, battleEngine), fightFrame,g);
            fightFrame.requestFocusInWindow();
        });
        fightFrame.add(bigSkill);

        // 逃跑按钮
        JButton fleeButton = new JButton("逃跑");
        fleeButton.addActionListener(e -> {
            fleeBattle(fightFrame, g);
        });
        fightFrame.add(fleeButton);
        
        fightFrame.setVisible(true);
    }

    // 逃跑方法
    private void fleeBattle(JFrame frame, Enemy g) {
        frame.dispose(); // 关闭战斗窗口

        // 检查敌人是否是Godzila
        if (g instanceof Godzila) {
            show("勇气是人类的赞歌，而你显然失去了勇气...");
            System.exit(0); // 结束程序
        } else {
            show("算你运气好，给你成功逃跑了。");
            // 战斗结束后，刷新一次地图，确保 UI 状态更新
            addGameMap();
        }
    }

    public void Inventoryabout(JFrame fightFrame,Enemy g){
        System.out.println("1. 开始设置回调");
        inventory.setCombatUIupdateCallback(()->{
            System.out.println("3. 回调函数体正在执行"); 
            updateCombatUI(g);
        });
        System.out.println("2. 回调设置完成");
        inventory.showInventory(m,fightFrame);
        updateCombatUI(g);

        }

    /**
     * 处理玩家行动后的流程：刷新UI -> 检查胜利 -> 怪物回合 -> 检查失败
     * @param success 玩家行动是否成功 (例如: 蓝量不足则失败)
     * @param frame 当前战斗窗口
     */
    private void playerAction(boolean success, JFrame frame,Enemy g) {
        if (!success) {
            show("行动失败！可能是蓝量不足。");
            return;
        }

        // 1. 刷新 UI
        updateCombatUI(g);

        // 2. 检查玩家是否获胜 (怪物死亡)
        if (!g.isAlive()) {
            endBattle(frame, true,g);
            return;
        }

        // 3. 怪物回合
        System.out.println("\n--- 怪物回合 ---");
        g.actInBattle(m, battleEngine);

        // 4. 刷新 UI
        updateCombatUI(g);

        // 5. 检查玩家是否失败 (玩家死亡)
        if (!m.isAlive()) {
            endBattle(frame, false,g);
        }
    }

    private void updateCombatUI(Enemy g) {
        enemyInfo.setText(g.getName() + " HP: " + g.getHP() + "/" + g.getMaxHP());
        playerInfo.setText(m.getName() + " HP: " + m.getHP() + "/" + m.getMaxHP() + " MP: " + m.getMP() + "/" + m.getMaxMP());
    }

    private void endBattle(JFrame frame, boolean playerWon,Enemy g) {
        frame.dispose(); // 关闭战斗窗口
        int HPcount=0;
        int MPcount=0;
        
        if (playerWon) {
            // 结算奖励并更新玩家状态 (LevelUp/Exp)
            if (g.getName().equals("哥斯拉")){
                HPcount=10;
                MPcount=10;
                inventory.addItem("HP",10);
                inventory.addItem("MP",10);
                if (mapName.equals("village")){
                        showmapName="forest";
                    }else if(mapName.equals("forest")){
                        showmapName="cave";
                    }else if(mapName.equals("cave")){
                        this.win=true;
                    }
            }else{
                HPcount=3;
                MPcount=3;
                inventory.addItem("HP",3);
                inventory.addItem("MP",3);
            }

            

            String winMessage = battleEngine.processBattleWin(m, g ,HPcount,MPcount);
            
            this.ghp_initial = g.getMaxHP(); 
            show(winMessage);
        } else {
            show("你被击败了，游戏结束...");
            // System.exit(0); // 退出程序
        }
        
        // 战斗结束后，刷新一次地图，确保 UI 状态更新
        addGameMap();
    }


    public void show(String s) {
        JDialog jDialog = new JDialog(this, true);
        jDialog.setSize(400, 200); 
        jDialog.setAlwaysOnTop(true);
        jDialog.setLocationRelativeTo(this);

        jDialog.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));

        // 使用 HTML 支持换行
        JLabel j = new JLabel("<html>" + s.replaceAll("\n", "<br>") + "</html>"); 
        j.setHorizontalAlignment(JLabel.CENTER);
        j.setVerticalAlignment(JLabel.CENTER);

        jDialog.add(j);
        jDialog.pack();
        jDialog.setVisible(true);
    }
}