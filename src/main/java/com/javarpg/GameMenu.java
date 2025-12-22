package main.java.com.javarpg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class GameMenu extends JFrame {

    // 预设图片路径常量。重要：如果使用 Maven 或 IDE，请将图片放在 src/main/resources/images/ 目录下。
    // 如果直接使用文件路径，请确保这些路径相对于您的运行目录是正确的。
    private static final String TITLE_IMAGE_PATH = "images/title.png";
    private static final String START_BUTTON_PATH = "images/start_button.png";
    private static final String LOAD_BUTTON_PATH = "images/load_button.png";
    private static final String SETTINGS_BUTTON_PATH = "images/settings_button.png";
    private static final String EXIT_BUTTON_PATH = "images/exit_button.png";
    private static final String BACKGROUND_MUSIC_PATH = "main/resources/music/小瀬村晶 - Light Dance -Home.wav";//背景音乐路径

    // 游戏核心对象，用于传递给 GameMap
    private final Magician player;
    // 音频播放器
    private static AudioPlayer backgroundMusic;

    public GameMenu(Magician player) { 
        this.player = player;
        // 初始化背景音乐
        backgroundMusic = new AudioPlayer(BACKGROUND_MUSIC_PATH);
        SwingUtilities.invokeLater(this::initMenu);
    }

    private void initMenu() {
        this.setTitle("Java RPG - 主菜单");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        // 设置整个窗口的默认背景色，确保没有黑屏区域
        this.getContentPane().setBackground(Color.DARK_GRAY);

        // 1. 创建并添加标题面板 (Title Panel)
        addTitlePanel();

        // 2. 创建并添加按钮面板 (Button Panel)
        addButtonPanel();
        
        this.setVisible(true);
    }

    /**
     * 创建标题面板，用于放置游戏 Logo/标题图片
     */
    private void addTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 80)); 
        titlePanel.setBackground(Color.DARK_GRAY);
        titlePanel.setOpaque(true); // **【修正点】确保面板不透明**
        
        JLabel titleLabel = new JLabel("游戏标题 (图片缺失)");
        titleLabel.setForeground(Color.YELLOW); // 确保文字对比度高
        titleLabel.setFont(new Font("Serif", Font.BOLD, 48));

        // 尝试加载图片
        try {
            // 尝试使用 ClassLoader 加载，以兼容 jar 包运行
            URL imageUrl = getClass().getClassLoader().getResource(TITLE_IMAGE_PATH);
            if (imageUrl == null) {
                // 如果资源未找到，则抛出异常，让它显示默认文字
                throw new IllegalArgumentException("Title image not found: " + TITLE_IMAGE_PATH);
            }
            
            ImageIcon icon = new ImageIcon(imageUrl);
            titleLabel.setIcon(icon);
            titleLabel.setText(""); // 图片加载成功，清空文本
        } catch (Exception e) {
            System.err.println("警告: 无法加载标题图片. 显示默认文本. (" + e.getMessage() + ")");
            titleLabel.setText("Java RPG Game"); // 图片加载失败，确保文字显示
        }

        titlePanel.add(titleLabel);
        this.add(titlePanel, BorderLayout.NORTH);
    }

    /**
     * 创建按钮面板，用于放置所有可点击的选项
     */
    private void addButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.DARK_GRAY);
        buttonPanel.setOpaque(true); 
        
        buttonPanel.add(Box.createVerticalGlue()); 

        // 创建按钮并添加到面板
        buttonPanel.add(createImageButton("开始游戏", START_BUTTON_PATH, this::startGame));
        // 添加按钮之间的间距
        buttonPanel.add(Box.createVerticalStrut(20));
        
        buttonPanel.add(createImageButton("读取存档", LOAD_BUTTON_PATH, this::loadGame));
        buttonPanel.add(Box.createVerticalStrut(20));

        buttonPanel.add(createImageButton("设置面板", SETTINGS_BUTTON_PATH, this::showSettingsPanel));
        buttonPanel.add(Box.createVerticalStrut(20));

        buttonPanel.add(createImageButton("退出游戏", EXIT_BUTTON_PATH, this::exitGame));

        buttonPanel.add(Box.createVerticalGlue()); 

        this.add(buttonPanel, BorderLayout.CENTER);
    }
    
    /**
     * 创建一个包含图片和点击事件的 JLabel (模拟按钮)
     */
    private JLabel createImageButton(String defaultText, String imagePath, Runnable action) {
        JLabel button = new JLabel(defaultText, SwingConstants.CENTER);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setForeground(Color.WHITE); // 默认字体颜色
        button.setFont(new Font("Serif", Font.BOLD, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); 

        boolean imageLoaded = false;
        try {
            URL imageUrl = getClass().getClassLoader().getResource(imagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                button.setIcon(icon);
                button.setText(""); // 图片加载成功，清空文本
                button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
                imageLoaded = true;
            }
        } catch (Exception e) {
            // 图片加载失败，保持默认文本
            System.err.println("警告: 无法加载按钮图片 " + imagePath);
        }

        // 如果图片加载失败，确保文本显示且提供最小尺寸
        if (!imageLoaded) {
            button.setText(defaultText);
            button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            button.setPreferredSize(new Dimension(300, 50)); 
        }

        // 添加鼠标点击事件
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }

            @Override 
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.CYAN); // 悬停颜色
            }

            @Override 
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE); // 恢复默认颜色
            }
        });
        
        // **【修正点】**：确保按钮容器也是不透明的，以便正确显示背景色
        button.setOpaque(false); // 按钮本身不需要背景色
        return button;
    }


    private void startGame() {
        System.out.println("--- 开始新游戏 ---");
        this.dispose(); 
        // 确保 Magician/Godzila 类和 GameMap 类存在且可访问
        new GameMap(player,"village"); 
    }

    private void loadGame() {
        JOptionPane.showMessageDialog(this, "读取存档功能待实现...", "功能提示", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSettingsPanel() {
        JDialog settingsDialog = new JDialog(this, "设置", true);
        settingsDialog.setSize(300, 200);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setLayout(new FlowLayout());

        JLabel volumeLabel = new JLabel("音量:");
        JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);

        // 设置初始音量
        backgroundMusic.setVolume(50);
        
        volumeSlider.addChangeListener(e -> {
            int volume = volumeSlider.getValue();
            backgroundMusic.setVolume(volume);
            System.out.println("音量调节到: " + volumeSlider.getValue());
        });

        settingsDialog.add(volumeLabel);
        settingsDialog.add(volumeSlider);
        
        JButton closeButton = new JButton("确定");
        closeButton.addActionListener(e -> settingsDialog.dispose());
        settingsDialog.add(closeButton);

        settingsDialog.setVisible(true);
    }
    
    private void exitGame() {
        System.exit(0);
    }
}