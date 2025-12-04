package main.java.com.javarpg;

public class BattleEngine {
    public void basicAttack(Character attacker, Character target) {
        int rawDamage = attacker.getAttack();
        target.takeDamage(rawDamage); 
    }

    public boolean smallSkill(Player player, Character target) {
        if (player.getMP() < 2) {
            System.out.println(player.getName() + " 蓝量不足！");
            return false;
        }
        player.setMP(player.getMP() - 2);
        int rawDamage = (int)(player.getAttack() * 1.5);
        target.takeDamage(rawDamage);
        System.out.println(player.getName() + " 使用小技能攻击 " + target.getName() + "。");
        return true;
    }

    public boolean bigSkill(Player player, Character target) {
        if (player.getMP() < 4) {
            System.out.println(player.getName() + " 蓝量不足！");
            return false;
        }
        player.setMP(player.getMP() - 4);
        int rawDamage = (int)(player.getAttack() * 2.0);
        target.takeDamage(rawDamage);
        
        System.out.println(player.getName() + " 使用大招攻击 " + target.getName() + "。");
        return true;
    }

    /*
      @param winner 战斗胜利者
      @param loser 战斗失败者
      @return 奖励信息字符串
     */

    public String processBattleWin(Player winner, Enemy loser) {
        String result = "恭喜你，战斗胜利！\n";
        
        // 经验
        int expGained = loser.getExpReward();
        winner.gainExp(expGained);
        result += "获得 " + expGained + " 点经验。\n";
        
        //战斗胜利回复10滴血
        winner.setHP(Math.min(winner.getMaxHP(), winner.getHP() + 10)); // 少量回复HP

        return result;
    }
}