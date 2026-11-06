package com.wuxia.jianghu;

import android.content.Context;
import android.content.pm.PackageInfo;

public final class BuildInfo {
    private BuildInfo() {
    }

    public static String versionName(Context ctx) {
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return pi.versionName == null ? "?" : pi.versionName;
        } catch (Exception e) {
            return "?";
        }
    }
}
