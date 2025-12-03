import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameMap extends JFrame implements KeyListener, MouseListener {
    Magician m;
    Godzila g;
    int x = 1, y = 3;
    ArrayList<ArrayList<Character>> gm = new ArrayList<>();
    ArrayList<Integer> p = new ArrayList<>();

    public GameMap(Magician m, Godzila g) {
        this.m=m;
        this.g=g;
        // 确保在EDT中初始化UI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initJFrame();
                initGM();
                addGameMap();
                setVisible(true);
            }
        });
    }

    private void addGameMap() {
        this.getContentPane().removeAll();
        for (int i = 0; i < gm.size(); i++) {
            for (int j = 0; j < gm.get(i).size(); j++) {
                JLabel jlabel = new JLabel(gm.get(i).get(j) + "");
                jlabel.setBounds(j * 50, i * 50, 50, 50);
                this.getContentPane().add(jlabel);
            }
        }
        this.getContentPane().repaint();
        this.revalidate();
    }

    private void initGM() {
        ArrayList<Character> row1 = new ArrayList<>();
        Collections.addAll(row1, '#', '#', '#', '#');
        ArrayList<Character> row2 = new ArrayList<>();
        Collections.addAll(row2, '#', '#', '.', '!');
        ArrayList<Character> row3 = new ArrayList<>();
        Collections.addAll(row3, '#', '.', '.', '#');
        ArrayList<Character> row4 = new ArrayList<>();
        Collections.addAll(row4, '#', '^', '#', '#');
        gm.add(row1);
        gm.add(row2);
        gm.add(row3);
        gm.add(row4);
        Collections.addAll(p, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1);
    }

    private void initJFrame() {
        setSize(1000, 700);
        setAlwaysOnTop(true);
        setTitle("地图打怪游戏1.0");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);
        addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        int o = e.getKeyCode();
        if (o == 87) {
            System.out.println("向上走");
            if (gm.get(y - 1).get(x) == '!') {
                Character temp = gm.get(y - 1).get(x);
                gm.get(y - 1).set(x, gm.get(y).get(x));
                gm.get(y).set(x, temp);
                y--;
                addGameMap();
                show("win");
            } else if (y > 0 && gm.get(y - 1).get(x) != '#') {
                Character temp = gm.get(y - 1).get(x);
                gm.get(y - 1).set(x, gm.get(y).get(x));
                gm.get(y).set(x, temp);
                y--;
                addGameMap();
                fightEvent();
            } else {
                show("不能向上走");
            }
        } else if (o == 83) {
            System.out.println("向下走");
            if (gm.get(y + 1).get(x) == '!') {
                Character temp = gm.get(y + 1).get(x);
                gm.get(y + 1).set(x, gm.get(y).get(x));
                gm.get(y).set(x, temp);
                y++;
                addGameMap();
                show("win");
            } else if (y < gm.size() - 1 && gm.get(y + 1).get(x) != '#') {
                Character temp = gm.get(y + 1).get(x);
                gm.get(y + 1).set(x, gm.get(y).get(x));
                gm.get(y).set(x, temp);
                y++;
                addGameMap();
            } else {
                show("不能向下走");
            }
        } else if (o == 65) {
            System.out.println("向左走");
            if (gm.get(y).get(x - 1) == '!') {
                Character temp = gm.get(y).get(x - 1);
                gm.get(y).set(x - 1, gm.get(y).get(x));
                gm.get(y).set(x, temp);
                x--;
                addGameMap();
                show("win");
            } else if (x > 0 && gm.get(y).get(x - 1) != '#') {
                Character temp = gm.get(y).get(x - 1);
                gm.get(y).set(x - 1, gm.get(y).get(x));
                gm.get(y).set(x, temp);
                x--;
                addGameMap();
            } else {
                show("不能向左走");
            }
        } else if (o == 68) {
            System.out.println("向右走");
            if (gm.get(y).get(x + 1) == '!') {
                Character temp = gm.get(y).get(x + 1);
                gm.get(y).set(x + 1, gm.get(y).get(x));
                gm.get(y).set(x, temp);
                x++;
                addGameMap();
                show("win");
            } else if (x < gm.get(y).size() - 1 && gm.get(y).get(x + 1) != '#') {
                Character temp = gm.get(y).get(x + 1);
                gm.get(y).set(x + 1, gm.get(y).get(x));
                gm.get(y).set(x, temp);
                x++;
                addGameMap();
            } else {
                show("不能向右走");
            }
        }
    }

    private void fightEvent() {
        Random r = new Random();
        int index = r.nextInt(p.size());
        if (p.get(index) == 1) {
            // 直接调用内部的IfFight方法而不是外部类
            showFightDialog();
        }
    }

    // 重命名方法避免与不存在的外部类混淆
    public void showFightDialog() {
        final JDialog fightDialog = new JDialog(this, "战斗选择", true);
        fightDialog.setSize(500, 400);
        fightDialog.setLocationRelativeTo(this);
        fightDialog.setLayout(null);

        JLabel jl = new JLabel("<html><body style='width: 400px;'>" +
                "突发事件：现在有敌人在你的路径上<br>" +
                "选择'是'将进行战斗，胜利将增加10点生命值上限，失败会重新开始<br>" +
                "选择'否'将继续行走，但你会损失5点生命值上限" +
                "</body></html>");

        jl.setBounds(50, 50, 400, 150);
        jl.setVerticalAlignment(JLabel.TOP);
        fightDialog.add(jl);

        JButton yes = new JButton("是");
        JButton no = new JButton("否");
        
        yes.setBounds(100, 250, 80, 30);
        no.setBounds(320, 250, 80, 30);
        
        yes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fightDialog.dispose();
                // 在EDT中创建并显示战斗窗口
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        createAndShowFightWindow();
                    }
                });
            }
        });
        
        no.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fightDialog.dispose();
                show("你选择了逃避，损失5点生命值");
            }
        });

        fightDialog.add(yes);
        fightDialog.add(no);
        fightDialog.setVisible(true);
    }

    // 单独的方法创建战斗窗口
    private void createAndShowFightWindow() {
        int mhp=m.HP,ghp=g.HP;
        JFrame fightFrame = new JFrame("战斗");
        fightFrame.setSize(1000, 500);
        fightFrame.setLocationRelativeTo(this);
        // 使用DISPOSE_ON_CLOSE而不是EXIT_ON_CLOSE，这样关闭战斗窗口不会退出整个程序
        fightFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        fightFrame.setLayout(null);

        JLabel enemyInfo=new JLabel(g.HP+"/"+ghp);
        enemyInfo.setBounds(100, 200, 100, 30);
        fightFrame.add(enemyInfo);

        JLabel enemy=new JLabel("怪兽");
        enemy.setBounds(100, 250, 100, 30);
        fightFrame.add(enemy);

        JLabel playerInfo=new JLabel(m.HP+"/"+mhp);
        playerInfo.setBounds(700, 200, 100, 30);
        fightFrame.add(playerInfo);

        JLabel player=new JLabel("我");
        player.setBounds(700, 250, 100, 30);
        fightFrame.add(player);
        
        JButton pa = new JButton("普攻");
        pa.setBounds(800, 150, 100, 30);
        pa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                g.HP--;
                if(g.HP<=0){
                    fightFrame.dispose();
                    show("恭喜你，战斗胜利\n奖励10点生命值上限");
                    m.HP=mhp+10;
                    g.HP=ghp+10;
                }else if(g.HP<=3){
                    show("怪兽红温了，你受到5点伤害");
                    m.HP-=5;
                }
                //刷新页面
                enemyInfo.setText(g.HP+"/"+ghp);
                playerInfo.setText(m.HP+"/"+mhp);
            }
        });

        fightFrame.add(pa);
        
        JButton smallSkill = new JButton("小技能");
        smallSkill.setBounds(800, 250, 100, 30);
        smallSkill.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                g.HP-=3;
                if(g.HP<=0){
                    fightFrame.dispose();
                    show("恭喜你，战斗胜利\n奖励10点生命值上限");
                    m.HP=mhp+10;
                    g.HP=ghp+10;
                }else if(g.HP<=3){
                    show("怪兽红温了，你受到5点伤害");
                    m.HP-=5;
                }
                //刷新页面
                enemyInfo.setText(g.HP+"/"+ghp);
                playerInfo.setText(m.HP+"/"+mhp);
            }
        });

        fightFrame.add(smallSkill);

        JButton bigSkill = new JButton("大招");
        bigSkill.setBounds(800, 350, 100, 30);
        bigSkill.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                g.HP-=5;
                if(g.HP<=0){
                    fightFrame.dispose();
                    show("恭喜你，战斗胜利\n奖励10点生命值上限");
                    m.HP=mhp+10;
                    g.HP=ghp+10;
                }else if(g.HP<=3){
                    show("怪兽红温了，你受到5点伤害");
                    m.HP-=5;
                }
                //刷新页面
                enemyInfo.setText(g.HP+"/"+ghp);
                playerInfo.setText(m.HP+"/"+mhp);
            }
        });
        fightFrame.add(bigSkill);
        
        fightFrame.setVisible(true);
        fightFrame.setAlwaysOnTop(true);
    }

    public void show(String s) {
        JDialog jDialog = new JDialog(this, true);
        jDialog.setSize(200, 150);
        jDialog.setAlwaysOnTop(true);
        jDialog.setLocationRelativeTo(this);

        jDialog.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50));

        JLabel j = new JLabel(s);
        j.setHorizontalAlignment(JLabel.CENTER);
        j.setVerticalAlignment(JLabel.CENTER);

        jDialog.add(j);
        jDialog.pack();
        jDialog.setVisible(true);
    }

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
}