package com.wuxia.jianghu;

/** 物品使用与装备的共用逻辑。 */
public final class Inventory {
    private Inventory() {
    }

    public static void applyConsumable(Player p, GameData.Item it) {
        switch (it.type) {
            case GameData.IT_HP:
                p.heal(it.value);
                break;
            case GameData.IT_MP:
                p.restoreMp(it.value);
                break;
            case GameData.IT_ALL:
                p.fullRestore();
                break;
            default:
                break;
        }
    }

    /** 装备一件兵器或护甲，原有装备放回行囊。 */
    public static void equip(Player p, int id) {
        GameData.Item it = GameData.ITEMS[id];
        if (p.items[id] <= 0) {
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
        }
    }
}
