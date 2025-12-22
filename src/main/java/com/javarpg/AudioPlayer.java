package main.java.com.javarpg;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class AudioPlayer {
    private Clip clip;
    private FloatControl volumeControl;

    // 构造函数，加载并播放音乐
    public AudioPlayer(String musicPath) {
        try {
            // 使用ClassLoader加载资源文件，路径相对于resources目录
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream audioStream = classLoader.getResourceAsStream(musicPath);

            if (audioStream != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioStream);
                clip = AudioSystem.getClip();
                clip.open(audioIn);

                // 设置音量控制
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                }

                // 设置循环播放
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
                System.out.println("成功加载并播放音乐: " + musicPath);
            } else {
                System.err.println("无法找到音乐文件: " + musicPath);
                System.err.println("请检查资源路径是否正确，当前路径应该相对于resources目录");
            }

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("播放音乐时出错: " + e.getMessage());
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
