package main.java.com.javarpg;

import java.util.List;
import java.util.Random;

public class NPC {
    private String name;
    private List<String> dialogues;
    private Random random = new Random();

    public NPC(String name, List<String> dialogues) {
        this.name = name;
        this.dialogues = dialogues;
    }

    public String getName() {
        return name;
    }

    // 获取随机对话
    public String getRandomDialogue() {
        if (dialogues == null || dialogues.isEmpty()) {
            return name + ": (沉默不语)";
        }
        return name + ": " + dialogues.get(random.nextInt(dialogues.size()));
    }

    // 获取所有对话 (如果需要按顺序显示)
    public List<String> getAllDialogues() {
        return dialogues;
    }
}
