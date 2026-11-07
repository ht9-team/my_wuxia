package com.wuxia.jianghu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/** 全局游戏状态：当前场景、存档槽、设置、结局收集、声音与飘字提示。 */
public class Game {
    public static final int SLOTS = 3;
    private static final String PREFS = "jianghu";
    private static final String OLD_SAVE = "save_v1";
    private static final String KEY_LAST = "last_slot";
    private static final String KEY_ENDINGS = "endings_mask";
    private static final String KEY_MUSIC = "music_on";
    private static final String KEY_SFX = "sfx_on";

    public final Context ctx;
    public final Random rnd = new Random();
    public final Audio audio = new Audio();
    public final float W = GameView.VW;
    public float H = GameView.MIN_VH;
    public float time = 0f;
    public Player player;
    public int slot = 0;

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
        migrate();
        audio.setMusicOn(musicOn());
        audio.setSfxOn(sfxOn());
        audio.start();
    }

    // ---------------- 场景 ----------------

    public void setScene(Scene s) {
        scene = s;
        s.layout();
        int m = s.music();
        if (m != Audio.KEEP) {
            audio.music(m);
        }
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
            UI.text(c, t.msg, W / 2f, y + 43, 30, (alpha << 24) | 0xF2E6CC, Paint.Align.CENTER);
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

    // ---------------- 设置 ----------------

    private SharedPreferences prefs() {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public boolean musicOn() {
        return prefs().getBoolean(KEY_MUSIC, true);
    }

    public boolean sfxOn() {
        return prefs().getBoolean(KEY_SFX, true);
    }

    public void setMusicOn(boolean on) {
        prefs().edit().putBoolean(KEY_MUSIC, on).apply();
        audio.setMusicOn(on);
    }

    public void setSfxOn(boolean on) {
        prefs().edit().putBoolean(KEY_SFX, on).apply();
        audio.setSfxOn(on);
    }

    // ---------------- 多存档 ----------------

    private static String key(int s) {
        return "slot" + s;
    }

    /** 旧版只有一个存档，迁移到第一个存档槽。 */
    private void migrate() {
        SharedPreferences sp = prefs();
        if (sp.contains(OLD_SAVE) && !sp.contains(key(0))) {
            sp.edit().putString(key(0), sp.getString(OLD_SAVE, "")).remove(OLD_SAVE).putInt(KEY_LAST, 0).apply();
        }
    }

    public void save() {
        if (player == null) {
            return;
        }
        try {
            prefs().edit().putString(key(slot), player.toJson().toString()).putInt(KEY_LAST, slot).apply();
        } catch (Exception ignored) {
            // 存档失败不应打断游戏
        }
    }

    public boolean hasSave(int s) {
        return prefs().contains(key(s));
    }

    public boolean anySave() {
        for (int i = 0; i < SLOTS; i++) {
            if (hasSave(i)) {
                return true;
            }
        }
        return false;
    }

    public int lastSlot() {
        int s = prefs().getInt(KEY_LAST, 0);
        return s >= 0 && s < SLOTS ? s : 0;
    }

    /** 读取存档内容但不切换当前角色，用于存档列表展示。 */
    public Player peek(int s) {
        String js = prefs().getString(key(s), null);
        if (js == null) {
            return null;
        }
        try {
            return Player.fromJson(new JSONObject(js));
        } catch (Exception e) {
            return null;
        }
    }

    public boolean load(int s) {
        Player p = peek(s);
        if (p == null) {
            return false;
        }
        player = p;
        slot = s;
        prefs().edit().putInt(KEY_LAST, s).apply();
        return true;
    }

    public void deleteSave(int s) {
        prefs().edit().remove(key(s)).apply();
    }

    // ---------------- 结局收集 ----------------

    public void unlockEnding(int id) {
        int m = prefs().getInt(KEY_ENDINGS, 0) | (1 << id);
        prefs().edit().putInt(KEY_ENDINGS, m).apply();
    }

    public boolean endingUnlocked(int id) {
        return (prefs().getInt(KEY_ENDINGS, 0) & (1 << id)) != 0;
    }

    public int endingCount() {
        return Integer.bitCount(prefs().getInt(KEY_ENDINGS, 0));
    }

    // ---------------- 行动 ----------------

    /** 前往某个地点（城镇或野外），自动存档并检查剧情触发。 */
    public void goLocation(int loc) {
        player.location = loc;
        player.set("v" + loc);
        if (GameData.LOCATIONS[loc].town) {
            player.lastTown = loc;
        }
        save();
        Story.toLocation(this);
    }

    public void goMap() {
        save();
        setScene(new MapScene(this));
    }
}
