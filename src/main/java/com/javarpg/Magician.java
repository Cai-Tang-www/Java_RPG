package main.java.com.javarpg;

public class Magician extends Player {
    // 初始化属性
    public Magician() {
        super("cyxxx", 100, 50, 20, 5, 1, 0, 100);
    }

    // 玩家由 UI 驱动，此方法在玩家类中留空
    public void actInBattle(Character target, BattleEngine engine) {
        
    }
    public boolean useBasicAttack(Character target, BattleEngine engine) {
        engine.basicAttack(this, target);
        return true;
    }

    public boolean useSmallSkill(Character target, BattleEngine engine) {
        return engine.smallSkill(this, target); 
    }

    // 大招方法 (委托给 BattleEngine)
    public boolean useBigSkill(Character target, BattleEngine engine) {
        // 使用 BigSkill 方法
        return engine.bigSkill(this, target);
    }
}