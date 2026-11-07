package com.wuxia.jianghu;

/** 物品使用、装备、出售与强化的共用逻辑。 */
public final class Inventory {
    public static final int MAX_PLUS = 10;
    public static final int IRON = 35;
    private static final int[] RATES = {100, 100, 100, 85, 75, 65, 55, 45, 40, 35};

    private Inventory() {
    }

    /** 使用一件消耗品，返回效果描述。 */
    public static String applyConsumable(Player p, GameData.Item it) {
        switch (it.type) {
            case GameData.IT_HP:
                p.heal(it.value);
                return "气血 +" + it.value;
            case GameData.IT_MP:
                p.restoreMp(it.value);
                return "内力 +" + it.value;
            case GameData.IT_ALL:
                p.fullRestore();
                return "气血内力全满";
            case GameData.IT_CURE:
                p.heal(it.value);
                return "解除中毒，气血 +" + it.value;
            default:
                return "";
        }
    }

    /** 装备一件兵器 / 护甲 / 饰品，原有装备放回行囊。 */
    public static void equip(Player p, int id) {
        GameData.Item it = GameData.ITEMS[id];
        if (p.items[id] <= 0 || !it.equipable()) {
            return;
        }
        p.items[id]--;
        if (it.type == GameData.IT_WEAPON) {
            if (p.weapon >= 0) {
                p.items[p.weapon]++;
            }
            p.weapon = id;
        } else if (it.type == GameData.IT_ARMOR) {
            if (p.armor >= 0) {
                p.items[p.armor]++;
            }
            p.armor = id;
        } else {
            if (p.accessory >= 0) {
                p.items[p.accessory]++;
            }
            p.accessory = id;
        }
        p.clampVitals();
    }

    /** slot：0 兵器，1 护甲，2 饰品。 */
    public static void unequip(Player p, int slot) {
        int id = slot == 0 ? p.weapon : (slot == 1 ? p.armor : p.accessory);
        if (id < 0) {
            return;
        }
        p.items[id]++;
        if (slot == 0) {
            p.weapon = -1;
        } else if (slot == 1) {
            p.armor = -1;
        } else {
            p.accessory = -1;
        }
        p.clampVitals();
    }

    public static boolean sellable(GameData.Item it) {
        return !(it.price == 0 && it.type == GameData.IT_ACC);
    }

    public static int sellPrice(GameData.Item it) {
        if (it.price > 0) {
            return Math.max(1, it.price * 2 / 5);
        }
        return 50 + it.value * 4;
    }

    public static int enhanceCost(Player p, int id) {
        GameData.Item it = GameData.ITEMS[id];
        return (p.enhance[id] + 1) * (40 + Math.max(it.value, 4) * 5);
    }

    public static int enhanceIron(Player p, int id) {
        int lv = p.enhance[id];
        return lv < 3 ? 0 : (lv - 1) / 2;
    }

    public static int enhanceRate(Player p, int id) {
        int lv = p.enhance[id];
        return lv >= MAX_PLUS ? 0 : RATES[lv];
    }
}
