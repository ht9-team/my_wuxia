package com.wuxia.jianghu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.RectF;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/** 全局游戏状态：当前场景、玩家存档、飘字提示。 */
public class Game {
    private static final String PREFS = "jianghu";
    private static final String KEY_SAVE = "save_v1";

    public final Context ctx;
    public final Random rnd = new Random();
    public final float W = GameView.VW;
    public float H = GameView.MIN_VH;
    public float time = 0f;
    public Player player;

    private Scene scene;
    private final ArrayList<Toast> toasts = new ArrayList<>();

    private static final class Toast {
        final String msg;
        float t;

        Toast(String msg) {
            this.msg = msg;
            this.t = 2.4f;
        }
    }

    public Game(Context ctx) {
        this.ctx = ctx;
    }

    public void setScene(Scene s) {
        scene = s;
        s.layout();
    }

    public Scene scene() {
        return scene;
    }

    public void resize(float h) {
        H = Math.max(GameView.MIN_VH, h);
        if (scene != null) {
            scene.layout();
        }
    }

    public void update(float dt) {
        time += dt;
        if (scene != null) {
            scene.update(dt);
        }
        for (int i = toasts.size() - 1; i >= 0; i--) {
            Toast t = toasts.get(i);
            t.t -= dt;
            if (t.t <= 0) {
                toasts.remove(i);
            }
        }
    }

    public void draw(Canvas c) {
        if (scene != null) {
            scene.draw(c);
        }
        float y = H * 0.42f;
        for (int i = 0; i < toasts.size(); i++) {
            Toast t = toasts.get(i);
            float a = Math.min(1f, t.t / 0.4f);
            int alpha = (int) (a * 230);
            float w = Math.min(660f, UI.textWidth(t.msg, 30) + 60f);
            RectF r = new RectF((W - w) / 2f, y, (W + w) / 2f, y + 64);
            UI.panel(c, r, (alpha << 24) | 0x1E1A16, (alpha << 24) | (UI.GOLD & 0xFFFFFF));
            UI.text(c, t.msg, W / 2f, y + 43, 30, (alpha << 24) | 0xF2E6CC, android.graphics.Paint.Align.CENTER);
            y += 76;
        }
    }

    public void toast(String msg) {
        if (toasts.size() >= 4) {
            toasts.remove(0);
        }
        toasts.add(new Toast(msg));
    }

    public void touchDown(float x, float y) {
        if (scene != null) {
            scene.touchDown(x, y);
        }
    }

    public void touchMove(float x, float y) {
        if (scene != null) {
            scene.touchMove(x, y);
        }
    }

    public void touchUp(float x, float y) {
        if (scene != null) {
            scene.touchUp(x, y);
        }
    }

    public boolean onBack() {
        return scene != null && scene.onBack();
    }

    private SharedPreferences prefs() {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void save() {
        if (player == null) {
            return;
        }
        try {
            prefs().edit().putString(KEY_SAVE, player.toJson().toString()).apply();
        } catch (Exception ignored) {
            // 存档失败不应打断游戏
        }
    }

    public boolean hasSave() {
        return prefs().contains(KEY_SAVE);
    }

    public boolean load() {
        String s = prefs().getString(KEY_SAVE, null);
        if (s == null) {
            return false;
        }
        try {
            player = Player.fromJson(new JSONObject(s));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteSave() {
        prefs().edit().remove(KEY_SAVE).apply();
    }

    /** 前往某个地点（城镇或野外），并自动存档。 */
    public void goLocation(int loc) {
        player.location = loc;
        if (GameData.LOCATIONS[loc].town) {
            player.lastTown = loc;
        }
        save();
        setScene(new LocationScene(this));
    }

    public void goMap() {
        save();
        setScene(new MapScene(this));
    }
}
