package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;

/** 存档位选择（共 20 个，分页显示）：新游戏 / 读取存档 / 查看节点 / 删除。 */
public class SlotScene extends Scene {
    private static final int PER = 5;
    private final boolean newGame;
    private int page;
    private int confirm = -1;
    private int confirmDelete = -1;

    public SlotScene(Game game, boolean newGame) {
        super(game);
        this.newGame = newGame;
        this.page = game.lastSlot() / PER;
    }

    @Override
    public int music() {
        return Audio.BGM_TITLE;
    }

    @Override
    public void layout() {
        buttons.clear();
        final int pageCount = pages(Game.SLOTS, PER);
        if (page >= pageCount) {
            page = 0;
        }
        float y = 240;
        float small = 104;
        for (int i = page * PER; i < Math.min(Game.SLOTS, page * PER + PER); i++) {
            final int slot = i;
            boolean has = game.hasSave(i);
            Player p = has ? game.peek(i) : null;
            String label;
            String sub;
            if (p != null) {
                label = "存档" + (i + 1) + " · " + p.name + "  Lv." + p.level;
                String chapter = p.finished ? "已通关" : Story.current(p).title;
                sub = GameData.SECTS[p.sect].name + " · " + GameData.LOCATIONS[p.location].name + " · " + chapter;
            } else if (has) {
                label = "存档" + (i + 1) + " · 已损坏";
                sub = "无法读取";
            } else {
                label = "存档" + (i + 1) + " · 空";
                sub = newGame ? "点击在此开始新的江湖" : "暂无存档";
            }
            if (newGame && has && confirm == i) {
                label = "再点一次覆盖存档" + (i + 1);
            }
            float w = has ? W() - 72 - 2 * (small + 10) : W() - 72;
            Button b = addButton(36, y, w, 130, label, () -> pick(slot));
            b.sub = sub;
            b.textSize = 30;
            b.enabled = newGame || p != null;
            b.color = newGame ? UI.RED : UI.JADE;
            if (has) {
                Button n = addButton(36 + w + 10, y, small, 130, "节点", () -> game.setScene(new NodeScene(game, slot, true)));
                n.enabled = game.hasNodes(slot);
                n.color = UI.GOLD;
                n.textSize = 28;
                Button d = addButton(36 + w + small + 20, y, small, 130, confirmDelete == i ? "确认" : "删除", () -> delete(slot));
                d.color = UI.INK_LIGHT;
                d.textSize = 28;
            }
            y += 146;
        }
        float bw = (W() - 72 - 14) / 2f;
        addButton(36, H() - 250, bw, 84, "上一页", () -> {
            page = (page + pageCount - 1) % pageCount;
            confirm = -1;
            confirmDelete = -1;
            layout();
        }).color = UI.INK_LIGHT;
        addButton(36 + bw + 14, H() - 250, bw, 84, "下一页 " + (page + 1) + "/" + pageCount, () -> {
            page = (page + 1) % pageCount;
            confirm = -1;
            confirmDelete = -1;
            layout();
        }).color = UI.INK_LIGHT;
        addButton(36, H() - 150, W() - 72, 104, "返回", () -> game.setScene(new TitleScene(game))).color = UI.INK_LIGHT;
    }

    private void pick(int slot) {
        if (newGame) {
            if (game.hasSave(slot) && confirm != slot) {
                confirm = slot;
                confirmDelete = -1;
                layout();
                return;
            }
            game.setScene(new CreateScene(game, slot));
            return;
        }
        if (game.load(slot)) {
            Story.toLocation(game);
        } else {
            game.toast("存档损坏，无法读取");
        }
    }

    private void delete(int slot) {
        if (confirmDelete != slot) {
            confirmDelete = slot;
            confirm = -1;
            layout();
            return;
        }
        game.deleteSave(slot);
        confirmDelete = -1;
        game.toast("存档" + (slot + 1) + " 已删除");
        if (!newGame && !game.anySave()) {
            game.setScene(new TitleScene(game));
            return;
        }
        layout();
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 1, game.time);
        UI.title(c, newGame ? "选择存档位" : "读取存档", W() / 2, 150, 56);
        drawButtons(c);
        UI.text(c, "共 " + Game.SLOTS + " 个存档位 · 「节点」可读取关键抉择前的自动存档", W() / 2, H() - 270, 22, UI.INK_LIGHT,
                Paint.Align.CENTER);
    }

    @Override
    public boolean onBack() {
        game.setScene(new TitleScene(game));
        return true;
    }
}
