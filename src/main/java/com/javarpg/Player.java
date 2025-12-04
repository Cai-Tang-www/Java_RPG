package main.java.com.javarpg;

public class Player extends Character{
    public Player(String name, int maxHP, int maxMP, int attack, int defense, int level, int exp, int expToNextLevel) {
        super(name, maxHP, maxMP, attack, defense, level);
        this.exp = exp;
        this.expToNextLevel = expToNextLevel;
    }
    private int exp;//经验  
    private int expToNextLevel; // 升到下一级所需经验
    public int getExp() { return exp; }
    public int getExpToNextLevel() { return expToNextLevel; }
    
    public void gainExp(int amount) {
        this.exp += amount;
        System.out.println(getName() + " 获得了 " + amount + " 点经验。");
        while (this.exp >= this.expToNextLevel) {
            levelUp();
        }
    }

    private void levelUp() {
        this.exp -= this.expToNextLevel;
        this.setLevel(getLevel() + 1);
        this.setMaxHP(getMaxHP() + 15);
        this.setMaxMP(getMaxMP() + 10);
        this.setAttack(getAttack() + 3);
        this.setDefense(getDefense() + 1);
        this.expToNextLevel = (int) (this.expToNextLevel * 1.5); // 经验升级曲线
        System.out.println("🎉 " + getName() + " 升级到 Level " + getLevel() + "!");
    }

    @Override
    public void displayStatus() {
        System.out.println("玩家状态：" + getName() + " Lv." + getLevel() + 
                           " HP:" + getHP() + "/" + getMaxHP() + 
                           " MP:" + getMP() + "/" + getMaxMP()+
                           " 经验: " + getExp() + "/" + getExpToNextLevel());
    }

}
