package main.java.com.javarpg;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AudioPlayer {
    private Clip clip;
    private FloatControl volumeControl;

    // 构造函数，加载并播放音乐
    public AudioPlayer(String musicPath) {
        try {
            AudioInputStream audioIn = null;
            File musicFile = null;

            // 方法1: 尝试从编译后的输出目录加载（推荐）
            String classPath = System.getProperty("java.class.path");
            System.out.println("Classpath: " + classPath);

            // 检查classpath中是否包含out/production/Java_RPG
            if (classPath.contains("out/production/Java_RPG")) {
                String outputDir = classPath.substring(0, classPath.indexOf(";"));
                musicFile = new File(outputDir, musicPath);
                if (musicFile.exists()) {
                    audioIn = AudioSystem.getAudioInputStream(musicFile);
                    System.out.println("成功从输出目录加载音乐: " + musicFile.getAbsolutePath());
                }
            }

            // 方法2: 尝试从当前工作目录的上级目录的resources中加载
            if (audioIn == null) {
                String workingDir = System.getProperty("user.dir");
                // 当前工作目录是src，所以我们需要向上一级到Java_RPG目录
                File parentDir = new File(workingDir).getParentFile();
                if (parentDir != null) {
                    musicFile = new File(parentDir, "src/main/resources/" + musicPath);
                    if (musicFile.exists()) {
                        audioIn = AudioSystem.getAudioInputStream(musicFile);
                        System.out.println("成功从resources目录加载音乐: " + musicFile.getAbsolutePath());
                    }
                }
            }

            // 方法3: 尝试直接从当前工作目录加载
            if (audioIn == null) {
                musicFile = new File(musicPath);
                if (musicFile.exists()) {
                    audioIn = AudioSystem.getAudioInputStream(musicFile);
                    System.out.println("成功从当前目录加载音乐: " + musicFile.getAbsolutePath());
                } else {
                    // 最后尝试在工作目录下的music文件夹
                    musicFile = new File("music/" + musicPath);
                    if (musicFile.exists()) {
                        audioIn = AudioSystem.getAudioInputStream(musicFile);
                        System.out.println("成功从工作目录的music文件夹加载音乐: " + musicFile.getAbsolutePath());
                    }
                }
            }

            if (audioIn != null) {
                clip = AudioSystem.getClip();
                clip.open(audioIn);

                // 设置音量控制
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                }

                // 设置循环播放
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            } else {
                System.err.println("无法找到音乐文件: " + musicPath);
                System.err.println("已尝试的路径:");
                System.err.println("- 输出目录: " + (classPath.contains("out/production/Java_RPG") ? new File(classPath.substring(0, classPath.indexOf(";")), musicPath).getAbsolutePath() : "不适用"));
                System.err.println("- Resources目录: " + (new File(System.getProperty("user.dir")).getParentFile() != null ? new File(new File(System.getProperty("user.dir")).getParentFile(), "src/main/resources/" + musicPath).getAbsolutePath() : "不适用"));
                System.err.println("- 当前目录: " + new File(musicPath).getAbsolutePath());
                System.err.println("- 工作目录music文件夹: " + new File("music/" + musicPath).getAbsolutePath());
            }

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 调节音量 (0-100)
    public void setVolume(int volume) {
        if (volumeControl != null) {
            // 将 0-100 转换为分贝值 (-80.0 to 6.0206)
            float dB = (float) (Math.log10(volume / 100.0) * 20);
            dB = Math.max(dB, volumeControl.getMinimum());
            dB = Math.min(dB, volumeControl.getMaximum());
            volumeControl.setValue(dB);
        }
    }

    // 停止播放并释放资源
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}