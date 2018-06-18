package com.i7xaphe.widget;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;

public class ItemAppListModel {

    private final long mId;
    private final String appName;
    private final String appPacked;
    private Drawable icon;
    private final ResolveInfo applicationInfo;
    private final PackageManager manager;

    public ItemAppListModel(long id, Context context, ResolveInfo applicationInfo, PackageManager manager) {
        mId = id;
        this.applicationInfo=applicationInfo;
        this.manager=manager;
        this.appName = applicationInfo.loadLabel(manager).toString();
        this.appPacked = applicationInfo.activityInfo.packageName;
        try {
            this.icon=manager.getActivityIcon(manager.getLaunchIntentForPackage(getAppPacked()));
        } catch (PackageManager.NameNotFoundException|NullPointerException e) {
            this.icon=context.getResources().getDrawable(R.drawable.android);
            e.printStackTrace();
        }
    }

    public long getId() {
        return mId;
    }

    public String getAppName() {
        return appName;
    }
    public String getAppPacked() {
        return appPacked;
    }
    public Drawable getAppIcon() {
        return icon;
    }
}