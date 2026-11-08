package com.wuxia.jianghu;

/** 门派专属数据：师门、叛徒、影阁来敌、出师试炼、镇派神兵与最终首领各不相同。 */
final class Sects {
    private Sects() {
    }

    static final String[] MASTER = {"了尘方丈", "冲和真人", "静慈师太", "石帮主"};
    static final int[] MINION = {38, 42, 44, 47};
    static final int[] TRAITOR = {40, 43, 46, 49};
    static final String[] TRAITOR_NAME = {"慧空", "玄冥子", "血手人屠", "彭长老"};
    static final int[] LIEUTENANT = {50, 51, 52, 53};
    static final int[] GUARDIAN = {72, 73, 74, 75};
    static final String[] GUARDIAN_NAME = {"十八铜人阵", "真武七星剑阵", "金顶守山师太", "打狗阵"};
    static final int[] WEAPON = {38, 39, 40, 41};
    static final int[] FINAL_BOSS = {63, 64, 65, 66};
    static final String[] SHADOW_NAME = {"慧明", "清虚", "静玄", "钱不易"};
    static final String[] SHADOW_TITLE = {"少林达摩院首座慧明大师", "武当掌门大弟子清虚道长", "峨眉戒律院首座静玄师太", "丐帮执法长老钱不易"};
    static final String[] HEIR_TITLE = {"少林方丈", "武当掌门", "峨眉掌门", "丐帮帮主"};

    static int home(Player p) {
        return 14 + p.sect;
    }

    static String homeName(Player p) {
        return GameData.LOCATIONS[home(p)].name;
    }

    static boolean isFinalBoss(int enemyId) {
        return enemyId >= 63 && enemyId <= 66;
    }
}
