package main.java.com.javarpg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameMap extends JFrame implements KeyListener, MouseListener {
    // 核心对象
    private Magician m;
    private Godzila g;
    
    // **新增：背景地图/地形数组**
    // 存储地图符号：# 墙壁，. 路径，^ 玩家初始位，! 怪物刷新区
    private String[][] terrain = {
        {"#", "#", "#", "#"},
        {"#", "#", ".", "!"},
        {"#", ".", ".", "#"},
        {"#", "^", "#", "#"} 
    };

    // 玩家在地图中的坐标 (y=行, x=列)
    int x = 1, y = 3; 
    
    // 实体地图：仅用于存放 Character 实体（Player/Enemy），空位为 null
    ArrayList<ArrayList<Character>> gm = new ArrayList<>();
    
    // 战斗引擎：处理所有战斗计算
    private BattleEngine battleEngine = new BattleEngine();
    
    // UI 组件：用于实时更新战斗信息
    private JLabel enemyInfo;
    private JLabel playerInfo;
    
    private int mhp_initial; // 初始/最大HP用于显示
    private int ghp_initial;
    
    public GameMap(Magician m, Godzila g) {
        this.m = m;
        this.g = g;
        // 保存初始最大值，用于 UI 显示
        this.mhp_initial = m.getMaxHP();
        this.ghp_initial = g.getMaxHP();
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initJFrame();
                initGM(); // 现在只初始化实体列表 gm
                addGameMap();
                setVisible(true);
            }
        });
    }

    private void initJFrame() {
        this.setSize(800, 600);
        this.setTitle("Java RPG - 地图探索");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.addKeyListener(this);
    }
    
    // 修正：只初始化实体地图 gm，全部用 null 填充，并放置玩家
    private void initGM() {
        // 根据 terrain 数组的大小初始化 gm
        for (int i = 0; i < terrain.length; i++) {
            ArrayList<Character> row = new ArrayList<>();
            for (int j = 0; j < terrain[0].length; j++) {
                row.add(null); // 初始全部是空位
            }
            gm.add(row);
        }
        
        // 放置玩家实体到初始位置 (y=3, x=1)
        if (y >= 0 && y < gm.size() && x >= 0 && x < gm.get(y).size()) {
            gm.get(y).set(x, m);
        }
    }

    // 修正：使用 terrain 数组和 gm 列表共同渲染
    private void addGameMap() {
        this.getContentPane().removeAll();
        for (int i = 0; i < gm.size(); i++) {
            for (int j = 0; j < gm.get(i).size(); j++) {
                String symbol = terrain[i][j]; // 获取背景符号

                // 如果该位置有 Character 实体 (Player/Enemy)
                if (gm.get(i).get(j) != null) {
                    symbol = "@"; // 玩家显示为 '@'
                }

                JLabel jlabel = new JLabel(symbol);
                // 设置字体和颜色，让地图更好看 (可选)
                jlabel.setFont(new Font("Monospaced", Font.BOLD, 24)); 
                jlabel.setForeground(symbol.equals("#") ? Color.GRAY : Color.BLACK);
                
                jlabel.setBounds(j * 50, i * 50, 50, 50);
                this.getContentPane().add(jlabel);
            }
        }
        this.getContentPane().repaint();
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

        // 3. 检查是否触发事件 (例如遇到怪物 !)
        if (terrain[y][x].equals("!")) {
            fight();
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

    public void fight() {
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
        
        // 初始化血量信息标签
        enemyInfo = new JLabel(g.getName() + " HP: " + g.getHP() + "/" + g.getMaxHP());
        playerInfo = new JLabel(m.getName() + " HP: " + m.getHP() + "/" + m.getMaxHP() + " MP: " + m.getMP() + "/" + m.getMaxMP());

        fightFrame.add(enemyInfo);
        fightFrame.add(playerInfo);

        // 攻击按钮 (普攻)
        JButton basicAttack = new JButton("普攻");
        basicAttack.addActionListener(e -> {
            // 调用 Magician 的方法，Magician 将任务委托给 BattleEngine
            playerAction(m.useBasicAttack(g, battleEngine), fightFrame);
        });
        fightFrame.add(basicAttack);

        // 小技能按钮
        JButton smallSkill = new JButton("小技能 (MP: 2)");
        smallSkill.addActionListener(e -> {
            playerAction(m.useSmallSkill(g, battleEngine), fightFrame);
        });
        fightFrame.add(smallSkill);

        // 大招按钮
        JButton bigSkill = new JButton("大招 (MP: 4)");
        bigSkill.addActionListener(e -> {
            playerAction(m.useBigSkill(g, battleEngine), fightFrame);
        });
        fightFrame.add(bigSkill);
        
        fightFrame.setVisible(true);
    }

    /**
     * 处理玩家行动后的流程：刷新UI -> 检查胜利 -> 怪物回合 -> 检查失败
     * @param success 玩家行动是否成功 (例如: 蓝量不足则失败)
     * @param frame 当前战斗窗口
     */
    private void playerAction(boolean success, JFrame frame) {
        if (!success) {
            show("行动失败！可能是蓝量不足。");
            return;
        }

        // 1. 刷新 UI
        updateCombatUI();

        // 2. 检查玩家是否获胜 (怪物死亡)
        if (!g.isAlive()) {
            endBattle(frame, true);
            return;
        }

        // 3. 怪物回合
        System.out.println("\n--- 怪物回合 ---");
        g.actInBattle(m, battleEngine);

        // 4. 刷新 UI
        updateCombatUI();

        // 5. 检查玩家是否失败 (玩家死亡)
        if (!m.isAlive()) {
            endBattle(frame, false);
        }
    }

    private void updateCombatUI() {
        enemyInfo.setText(g.getName() + " HP: " + g.getHP() + "/" + g.getMaxHP());
        playerInfo.setText(m.getName() + " HP: " + m.getHP() + "/" + m.getMaxHP() + " MP: " + m.getMP() + "/" + m.getMaxMP());
    }

    private void endBattle(JFrame frame, boolean playerWon) {
        frame.dispose(); // 关闭战斗窗口
        
        if (playerWon) {
            // 结算奖励并更新玩家状态 (LevelUp/Exp)
            String winMessage = battleEngine.processBattleWin(m, g);
            
            // 重新实例化怪物，以便下次战斗 (注意：这会创建新的 Godzila 实例)
            this.g = new Godzila();
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