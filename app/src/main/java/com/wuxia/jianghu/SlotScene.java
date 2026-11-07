package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;

/** 存档位选择：newGame=true 时选择位置开始新游戏（已有存档需二次确认），否则读取存档。 */
public class SlotScene extends Scene {
    private final boolean newGame;
    private int confirm = -1;
    private int confirmDelete = -1;

    public SlotScene(Game game, boolean newGame) {
        super(game);
        this.newGame = newGame;
    }

    @Override
    public int music() {
        return Audio.BGM_TITLE;
    }

    @Override
    public void layout() {
        buttons.clear();
        float y = 280;
        float delW = 140;
        for (int i = 0; i < Game.SLOTS; i++) {
            final int slot = i;
            Player p = game.peek(i);
            boolean has = game.hasSave(i);
            String label;
            String sub;
            if (p != null) {
                label = "存档" + (i + 1) + " · " + p.name + "  Lv." + p.level;
                String chapter = p.finished ? "已通关" : Story.current(p).title;
                sub = GameData.SECTS[p.sect].name + " · " + GameData.LOCATIONS[p.location].name + " · 第" + p.day + "日 · " + chapter;
            } else if (has) {
                label = "存档" + (i + 1) + " · 已损坏";
                sub = "无法读取";
            } else {
                label = "存档" + (i + 1) + " · 空";
                sub = newGame ? "点击在此开始新的江湖" : "暂无存档";
            }
            float w = has ? W() - 72 - delW - 14 : W() - 72;
            if (newGame && has && confirm == i) {
                label = "再点一次覆盖存档" + (i + 1);
            }
            Button b = addButton(36, y, w, 150, label, () -> pick(slot));
            b.sub = sub;
            b.textSize = 32;
            b.enabled = newGame || p != null;
            b.color = newGame ? UI.RED : UI.JADE;
            if (has) {
                Button d = addButton(36 + w + 14, y, delW, 150, confirmDelete == i ? "确认" : "删除", () -> delete(slot));
                d.color = UI.INK_LIGHT;
                d.textSize = 30;
            }
            y += 172;
        }
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
        UI.title(c, newGame ? "选择存档位" : "读取存档", W() / 2, 190, 60);
        drawButtons(c);
        UI.text(c, "每个存档位都是一段独立的江湖", W() / 2, H() - 180, 24, UI.INK_LIGHT, Paint.Align.CENTER);
    }

    @Override
    public boolean onBack() {
        game.setScene(new TitleScene(game));
        return true;
    }
}
