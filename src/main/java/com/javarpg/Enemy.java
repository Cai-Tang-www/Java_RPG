package main.java.com.javarpg;

public class Enemy extends Character{
    private int expReward;
    boolean angry = false;
    public Enemy(String name, int maxHP, int maxMP, int attack, int defense, int level, int expReward) {
        super(name, maxHP, maxMP, attack, defense, level);
        this.expReward = expReward;
    }

    public void actInBattle(Character target, BattleEngine engine) {
        engine.basicAttack(this, target);
    }
    public int getExpReward() { return expReward; }

    @Override
    public void displayStatus() {
        System.out.println("敌人状态：" + getName() + " Lv." + getLevel() + 
                           " HP:" + getHP() + "/" + getMaxHP() + 
                           " MP:" + getMP() + "/" + getMaxMP());
    }
}
