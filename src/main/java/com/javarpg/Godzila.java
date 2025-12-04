package main.java.com.javarpg;

import java.util.Random;
public class Godzila extends Enemy {
    
    // 初始化怪物属性：(Name, MaxHP, MaxMP, Attack, Defense, Level, ExpReward)
    public Godzila() {
        super("哥斯拉", 150, 10, 25, 8, 3, 50);
    }
    
    @Override
    public void actInBattle(Character target, BattleEngine engine) {
        Random rand = new Random();
        if (this.getHP() < this.getMaxHP() * 0.3) {
            if (rand.nextBoolean()) {
                angry(target, engine);
                return;
            }
        }
        engine.basicAttack(this, target);
    }

    // 伤害翻倍
    public void angry(Character target, BattleEngine engine) {
        System.out.println("🔥 哥斯拉红温了！发动狂暴攻击！");
        int rawDamage = this.getAttack() * 2;
        target.takeDamage(rawDamage);
    }
}