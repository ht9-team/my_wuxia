package com.wuxia.jianghu;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;

/** 客栈：住店、悬赏榜（可重复的支线任务）、打听消息。 */
public class InnScene extends Scene {
    private static final class Offer {
        final int enemy;
        final int loc;
        final int count;
        final int silver;
        final int exp;

        Offer(int enemy, int loc, int count, int silver, int exp) {
            this.enemy = enemy;
            this.loc = loc;
            this.count = count;
            this.silver = silver;
            this.exp = exp;
        }
    }

    private final ArrayList<Offer> offers = new ArrayList<>();
    private boolean confirmReplace = false;
    private int pendingOffer = -1;

    public InnScene(Game game) {
        super(game);
        makeOffers();
    }

    private int price() {
        return 5 + game.player.level * 3;
    }

    /** 根据角色等级生成三张悬赏：目标为附近可去的野外敌人，非首领。 */
    private void makeOffers() {
        Player p = game.player;
        ArrayList<Integer> locs = new ArrayList<>();
        for (GameData.Location l : GameData.LOCATIONS) {
            if (!l.town && !l.storyBoss && l.minLv <= p.level + 1 && l.enemies.length > 0) {
                locs.add(l.id);
            }
        }
        if (locs.isEmpty()) {
            locs.add(1);
        }
        for (int i = 0; i < 3; i++) {
            int locId = locs.get(game.rnd.nextInt(locs.size()));
            GameData.Location l = GameData.LOCATIONS[locId];
            int enemy = l.enemies[game.rnd.nextInt(l.enemies.length)];
            GameData.Enemy e = GameData.ENEMIES[enemy];
            int count = 3 + game.rnd.nextInt(3);
            int silver = e.silver * count * 2 + 30 + e.lv * 10;
            int exp = e.exp * count / 2 + 10;
            offers.add(new Offer(enemy, locId, count, silver, exp));
        }
    }

    @Override
    public void layout() {
        buttons.clear();
        Player p = game.player;
        Button rest = addButton(36, 196, W() - 72, 100, "住店歇息  " + price() + " 两", this::rest);
        rest.sub = "气血内力尽数恢复，耗时一日";
        rest.enabled = p.silver >= price();
        float y = 560;
        for (int i = 0; i < offers.size(); i++) {
            final int idx = i;
            Offer o = offers.get(i);
            String label = (confirmReplace && pendingOffer == i ? "再点一次替换当前悬赏" : "击败 " + GameData.ENEMIES[o.enemy].name
                    + " ×" + o.count);
            Button b = addButton(36, y, W() - 72, 100, label, () -> accept(idx));
            b.sub = GameData.LOCATIONS[o.loc].name + " · 赏银 " + o.silver + " · 修为 " + o.exp;
            b.color = UI.RED;
            b.textSize = 30;
            y += 114;
        }
        Button news = addButton(36, y + 10, W() - 72, 100, "打听消息  5 两", this::news);
        news.sub = "店小二消息灵通，或许知道些什么";
        news.color = UI.JADE;
        news.enabled = p.silver >= 5;
        addButton(36, H() - 150, W() - 72, 104, "离开客栈", () -> Story.toLocation(game)).color = UI.INK_LIGHT;
    }

    private void rest() {
        Player p = game.player;
        if (p.silver < price()) {
            game.toast("银两不足，掌柜把你赶了出来");
            return;
        }
        p.silver -= price();
        p.fullRestore();
        p.day++;
        game.audio.sfx(Audio.SFX_HEAL);
        game.toast("美美睡了一觉，气血内力尽复");
        game.save();
        layout();
    }

    private void accept(int idx) {
        Player p = game.player;
        if (p.hasBounty() && !(confirmReplace && pendingOffer == idx)) {
            confirmReplace = true;
            pendingOffer = idx;
            layout();
            return;
        }
        Offer o = offers.remove(idx);
        p.bountyEnemy = o.enemy;
        p.bountyLoc = o.loc;
        p.bountyCount = o.count;
        p.bountyProgress = 0;
        p.bountySilver = o.silver;
        p.bountyExp = o.exp;
        confirmReplace = false;
        pendingOffer = -1;
        game.audio.sfx(Audio.SFX_EQUIP);
        game.toast("揭下悬赏：" + p.bountyText());
        game.save();
        layout();
    }

    private void news() {
        Player p = game.player;
        if (p.silver < 5) {
            return;
        }
        p.silver -= 5;
        game.audio.sfx(Audio.SFX_COIN);
        game.setScene(new DialogScene(game, Story.tavern(game)));
    }

    @Override
    public void draw(Canvas c) {
        UI.paperBackground(c, W(), H(), 0, game.time);
        UI.hud(c, game);
        Player p = game.player;
        RectF r = new RectF(36, 316, W() - 36, 500);
        UI.panel(c, r);
        UI.seal(c, "悬赏", r.right - 62, r.top + 62, 72);
        UI.text(c, "当前悬赏", 62, r.top + 46, 26, UI.INK_LIGHT);
        if (p.hasBounty()) {
            UI.text(c, p.bountyText(), 62, r.top + 98, 32, UI.RED);
            UI.text(c, "赏银 " + p.bountySilver + " · 修为 " + p.bountyExp, 62, r.top + 146, 24, UI.INK);
        } else {
            UI.text(c, "暂无，可从下方悬赏榜揭榜", 62, r.top + 98, 28, UI.INK_LIGHT);
            UI.text(c, "已完成悬赏 " + p.bountiesDone + " 件", 62, r.top + 146, 24, UI.INK);
        }
        UI.text(c, "—— 悬赏榜 ——", W() / 2, 540, 26, UI.INK, Paint.Align.CENTER);
        drawButtons(c);
    }

    @Override
    public boolean onBack() {
        Story.toLocation(game);
        return true;
    }
}
