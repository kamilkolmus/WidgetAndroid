package com.i7xaphe.widget;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TableRow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Widget8 extends Service implements View.OnTouchListener {
    Animation animRotate;
    Animation anim1;
    Animation anim2;
    Animation anim3;
    Animation anim4;
    Animation animLongRotate;
    Animation anim_when_hide_icons, anim_when_show_icons;
    Animation hide, buttonPress;

    int randomint;
    boolean randomAnimationThread = true;

    WindowManager wm;
    LinearLayout linearLayout;
    double x;
    double y;
    double pressedX;
    double pressedY;
    LayoutParams tlparams;
    List<ImageView> imageViewList;
    ImageView imageViewWidget;
    PlayGifView gifViewWidget;
    boolean isFileGif = false;
    Space spaceTOP, spaceBOT, spaceLeft, spaceRight;
    private boolean runAnimation = true;
    SharedPreferences sheredpreferences;
    SharedPreferences.Editor editor;
    TableRow.LayoutParams layoutParams;

    int widgetSize;
    int iconSize;
    int radius;
    int space;
    float scaleY;

    int marginWidgetLeftRight;
    int marginWidgetTopBot;
    int marginIconLeftRight;

    enum switchAction {
        SHOW, HIDE
    }

    switchAction widgetClickAction = switchAction.HIDE;
    boolean unableRelease = true;

    PackageManager manager;
    MyFileManager listMemoryAdapter;
    public static String fileFULL = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "widget" + File.separator + "file.txt";

    private widgetServiceCallBacks serviceCallbacks;
    private final IBinder binder = new LocalBinder();


    interface widgetServiceCallBacks {
        void onWidgetRelease();

        void onIconClick(int i);
    }

    public void setCallbacks(widgetServiceCallBacks callbacks) {
        serviceCallbacks = callbacks;
    }

    public class LocalBinder extends Binder {
        Widget8 getService() {
            return Widget8.this;
        }
    }

    //==============================================================================================
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return binder;
    }


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        sheredpreferences = getSharedPreferences(MainActivity.sharePref, Context.MODE_PRIVATE);
        editor = sheredpreferences.edit();

        manager = getApplicationContext().getPackageManager();
        File file = new File(MainActivity.fileFULL);
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listMemoryAdapter = new MyFileManager(file);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        linearLayout = new LinearLayout(this);


        if (sheredpreferences.getBoolean("widgetBoundaries", MySettings.boundariesOn)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tlparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_APPLICATION_OVERLAY,
                        LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_HARDWARE_ACCELERATED, PixelFormat.TRANSPARENT);
            }else{
                tlparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_PHONE,
                        LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_HARDWARE_ACCELERATED, PixelFormat.TRANSPARENT);
            }
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tlparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_APPLICATION_OVERLAY,
                        LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_HARDWARE_ACCELERATED | LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSPARENT);
            }else{
                tlparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_PHONE,
                        LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_HARDWARE_ACCELERATED | LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSPARENT);
            }
        }

        tlparams.gravity = Gravity.CENTER;
        tlparams.windowAnimations = android.R.style.Animation_Activity;

        //ustawienie flagi braku animacji podczas zmiany wielkosci okna
        int currentFlags = 0;
        try {
            currentFlags = (Integer) tlparams.getClass().getField("privateFlags").get(tlparams);
            tlparams.getClass().getField("privateFlags").set(tlparams, currentFlags | 0x00000040 | 0x00000001);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


        tlparams.alpha = (float) 0.01 * sheredpreferences.getInt("alpha", MySettings.alpha);
        tlparams.x = sheredpreferences.getInt("widgetX", 0);
        tlparams.y = sheredpreferences.getInt("widgetY", 0);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.widget_8, null);


        imageViewList = new ArrayList<>();
        imageViewWidget = (ImageView) linearLayout.findViewById(R.id.iv_widget);
        //    imageViewWidget.setDrawingCacheEnabled(true);
        gifViewWidget = (PlayGifView) linearLayout.findViewById(R.id.iv_widget_gif);
        //    gifViewWidget.setDrawingCacheEnabled(true);
        imageViewList.add((ImageView) linearLayout.findViewById(R.id.im_1));
        imageViewList.add((ImageView) linearLayout.findViewById(R.id.im_2));
        imageViewList.add((ImageView) linearLayout.findViewById(R.id.im_3));
        imageViewList.add((ImageView) linearLayout.findViewById(R.id.im_4));
        imageViewList.add((ImageView) linearLayout.findViewById(R.id.im_5));
        imageViewList.add((ImageView) linearLayout.findViewById(R.id.im_6));
        imageViewList.add((ImageView) linearLayout.findViewById(R.id.im_7));
        imageViewList.add((ImageView) linearLayout.findViewById(R.id.im_8));

        spaceTOP = (Space) linearLayout.findViewById(R.id.space_top);
        spaceBOT = (Space) linearLayout.findViewById(R.id.space_bot);
        spaceRight = (Space) linearLayout.findViewById(R.id.space_right);
        spaceLeft = (Space) linearLayout.findViewById(R.id.space_left);


        String filePath = sheredpreferences.getString("pngFile", "");

        int color = sheredpreferences.getInt("widgetColor", MySettings.widgetColor);
        switch (color) {
            case 0:
                if (filePath.endsWith(".gif") || (filePath.endsWith(".GIF"))) {
                    isFileGif = true;
                } else {
                    isFileGif = false;
                }
                File imgFile = new File(sheredpreferences.getString("pngFile", ""));
                if (imgFile.exists()) {
                    if (!isFileGif) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        imageViewWidget.setImageBitmap(myBitmap);
                    }
                } else {
                    imageViewWidget.setImageResource(R.drawable.widget_blue);
                    editor.putInt("widgetColor", 1);
                    editor.apply();
                    // Toast.makeText(getBaseContext(), "Can't load file ", Toast.LENGTH_SHORT);
                }
                break;
            case 1:
                imageViewWidget.setImageResource(R.drawable.widget_blue);
                break;
            case 2:
                imageViewWidget.setImageResource(R.drawable.widget_yellow);
                break;
            case 3:
                imageViewWidget.setImageResource(R.drawable.widget_white);
                break;
            case 4:
                imageViewWidget.setImageResource(R.drawable.widget_dark);
                break;
            case 5:
                imageViewWidget.setImageResource(R.drawable.widget_purple);
                break;

            default:
        }

        widgetSize = dpToPx(sheredpreferences.getInt("widgetSize", MySettings.widgetSize));
        iconSize = dpToPx(sheredpreferences.getInt("iconSize", MySettings.iconSize));
        radius = dpToPx(sheredpreferences.getInt("radius", MySettings.radius));
        scaleY = sheredpreferences.getFloat("scaleY", (float) MySettings.scaleY);
        space = (int) (radius * 0.405 - iconSize / 2.0) * 2;
        //0.41421236
        marginWidgetLeftRight = (int) (radius - iconSize / 2.0 - widgetSize / 2.0);
        marginWidgetTopBot = (int) (radius * scaleY - iconSize / 2.0 - widgetSize / 2.0);
        marginIconLeftRight = radius - iconSize;

        //WIEKOSC ICON
        for (int i = 0; i < 8; i++) {
            imageViewList.get(i).getLayoutParams().height = iconSize;
            imageViewList.get(i).getLayoutParams().width = iconSize;
        }
        //WIEKOSC WIDGET
        if (isFileGif) {
            int realMovieX = gifViewWidget.checkMovieWidth(filePath);
            int realMovieY = gifViewWidget.checkMovieHeight(filePath);
            gifViewWidget.setVisibility(View.VISIBLE);
            imageViewWidget.setVisibility(View.GONE);
            int newMovieX;
            int newMovieY;
            if (realMovieX >= realMovieY) {
                newMovieX = (int) ((1 + (float) widgetSize / 400.0) * widgetSize);
                newMovieY = (int) ((1 + (float) widgetSize / 400.0) * widgetSize * realMovieY / realMovieX);
                gifViewWidget.setMYScaleX(newMovieX / (float) realMovieX);
                gifViewWidget.setMYScaleY(newMovieX / (float) realMovieX);
            } else {
                newMovieY = (int) ((1 + (float) widgetSize / 400.0) * widgetSize);
                newMovieX = (int) ((1 + (float) widgetSize / 400.0) * widgetSize * realMovieX / realMovieY);
                gifViewWidget.setMYScaleX(newMovieY / (float) realMovieY);
                gifViewWidget.setMYScaleY(newMovieY / (float) realMovieY);
            }
            gifViewWidget.setGif(sheredpreferences.getString("pngFile", ""), newMovieX, newMovieY);
            marginWidgetLeftRight = (int) (radius - iconSize / 2.0 - newMovieX / 2.0);
            marginWidgetTopBot = (int) (radius * scaleY - iconSize / 2 - newMovieY / 2.0);

        } else {
            imageViewWidget.getLayoutParams().height = widgetSize;
            imageViewWidget.getLayoutParams().width = widgetSize;
        }
        //MARGINESY
        TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) imageViewWidget.getLayoutParams();
        layoutParams.setMargins(marginWidgetLeftRight, marginWidgetTopBot, marginWidgetLeftRight, marginWidgetTopBot);
        if (isFileGif) {
            gifViewWidget.setLayoutParams(layoutParams);
        } else {
            imageViewWidget.setLayoutParams(layoutParams);
        }

        Log.i("space", "space" + space);
        spaceBOT.getLayoutParams().width = space;
        spaceTOP.getLayoutParams().width = space;
        spaceLeft.getLayoutParams().height = (int) (space);
        spaceRight.getLayoutParams().height = (int) (space);


        wm.addView(linearLayout, tlparams);

        anim_when_hide_icons = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_when_hide_icon_ratate);
        anim_when_show_icons = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_when_show_icon_rotate);
        animRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_rotare);

        if (sheredpreferences.getBoolean("randomAnim", MySettings.randomAnim)) {

            anim1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_anim_1);
            anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_anim_2);
            anim3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_anim_3);
            anim4 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_anim_4);
        } else {

            anim1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.empty_anim);
            anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.empty_anim);
            anim3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.empty_anim);
            anim4 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.empty_anim);

        }

        if (sheredpreferences.getBoolean("rotateAnim", MySettings.randomAnim)) {
            animLongRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_longrotate);
        } else {
            animLongRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.empty_anim);
            animRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.empty_anim);
            anim_when_hide_icons = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_when_hide_icon);
            anim_when_show_icons = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_when_show_icon);
        }

        hide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide);
        hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                for (int i = 0; i < 8; i++) {
                    imageViewList.get(i).setVisibility(View.INVISIBLE);
                }
                for (int i = 0; i < 8; i++) {
                    imageViewList.get(i).setVisibility(View.GONE);
                }
                TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) imageViewWidget.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 0);
                if (isFileGif) {
                    gifViewWidget.setLayoutParams(layoutParams);
                } else {
                    imageViewWidget.setLayoutParams(layoutParams);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(200);
                            setUnableRelease(true);
                            runAnimation = true;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        buttonPress = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_press);

        final int rotateFreq = sheredpreferences.getInt("rotateFreq", MySettings.randomAnimFreq);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (randomAnimationThread) {
                        Log.i("runAnimation", "runAnimation" + runAnimation);
                        if (runAnimation) {
                            Thread.sleep(1000 * rotateFreq);
                            if (runAnimation) {
                                randomint = random.nextInt(4);
                                handler.sendEmptyMessage(1);
                            }
                        } else {
                            Thread.sleep(2000);
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        gifViewWidget.setOnTouchListener(this);
        imageViewWidget.setOnTouchListener(this);


        imageViewList.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewList.get(0).startAnimation(buttonPress);
                if (openApp(getBaseContext(), listMemoryAdapter.getElement(0))) {
                    if (sheredpreferences.getBoolean("iconShowHide", MySettings.iconShowHide)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideIcon();

                            }
                        }, 300);
                    }
                }
            }
        });
        imageViewList.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewList.get(1).startAnimation(buttonPress);
                if (openApp(getBaseContext(), listMemoryAdapter.getElement(1))) {
                    if (sheredpreferences.getBoolean("iconShowHide", MySettings.iconShowHide)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideIcon();
                            }
                        }, 300);
                    }
                }
            }
        });
        imageViewList.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewList.get(2).startAnimation(buttonPress);
                if (openApp(getBaseContext(), listMemoryAdapter.getElement(2))) {
                    if (sheredpreferences.getBoolean("iconShowHide", MySettings.iconShowHide)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideIcon();
                            }
                        }, 300);
                    }
                }
            }
        });
        imageViewList.get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewList.get(3).startAnimation(buttonPress);
                if (openApp(getBaseContext(), listMemoryAdapter.getElement(3))) {
                    if (sheredpreferences.getBoolean("iconShowHide", MySettings.iconShowHide)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideIcon();
                            }
                        }, 300);
                    }
                }
            }
        });
        imageViewList.get(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewList.get(4).startAnimation(buttonPress);
                if (openApp(getBaseContext(), listMemoryAdapter.getElement(4))) {
                    if (sheredpreferences.getBoolean("iconShowHide", MySettings.iconShowHide)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideIcon();
                            }
                        }, 300);
                    }
                }
            }
        });
        imageViewList.get(5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewList.get(5).startAnimation(buttonPress);
                if (openApp(getBaseContext(), listMemoryAdapter.getElement(5))) {
                    if (sheredpreferences.getBoolean("iconShowHide", MySettings.iconShowHide)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideIcon();
                            }
                        }, 300);

                    }
                }
            }
        });
        imageViewList.get(6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewList.get(6).startAnimation(buttonPress);
                if (openApp(getBaseContext(), listMemoryAdapter.getElement(6))) {
                    if (sheredpreferences.getBoolean("iconShowHide", MySettings.iconShowHide)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideIcon();
                            }
                        }, 300);

                    }
                }
            }
        });
        imageViewList.get(7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewList.get(7).startAnimation(buttonPress);
                if (openApp(getBaseContext(), listMemoryAdapter.getElement(7))) {
                    if (sheredpreferences.getBoolean("iconShowHide", MySettings.iconShowHide)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hideIcon();
                            }
                        }, 300);

                    }
                }
            }
        });
        if (sheredpreferences.getBoolean("widgetForeground", MySettings.runForeground)) {
            startWidgetForeground("Click to open Widget");

        }
        for (int i = 0; i < 8; i++) {
            try {
                imageViewList.get(i).setImageDrawable(manager.getActivityIcon(manager.getLaunchIntentForPackage(listMemoryAdapter.getElement(i))));
            } catch (PackageManager.NameNotFoundException e) {
                imageViewList.get(i).setImageDrawable(getResources().getDrawable(R.drawable.mono_point));
            } catch (NullPointerException e) {
                imageViewList.get(i).setImageDrawable(getResources().getDrawable(R.drawable.mono_point));
            } catch (IndexOutOfBoundsException e) {
                imageViewList.get(i).setImageDrawable(getResources().getDrawable(R.drawable.mono_point));
            }
        }
        showIcon();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wm.removeView(linearLayout);
        randomAnimationThread = false;
        stopSelf();
    }

    void commitLastPosicion() {
        editor.putInt("widgetX", tlparams.x);
        editor.putInt("widgetY", tlparams.y);
        editor.commit();
    }


    public boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();


        try {
            Intent intent = manager.getLaunchIntentForPackage(packageName);
            if (intent == null) {
                Log.i("opening another app", "false " + packageName);
                return false;
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //             intent.setAction(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_LAUNCHER);
            getBaseContext().startActivity(intent);


            // context.startActivity(intent);
            Log.i("opening another app", "true");
            return true;
        } catch (Exception e) {
            Log.e("opening another app", "true");
            return false;
        }


    }

    private String getAppName(int pID) {
        String processName = "";
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    //Log.d("Process", "Id: "+ info.pid +" ProcessName: "+ info.processName +"  Label: "+c.toString());
                    //processName = c.toString();
                    processName = info.processName;
                }
            } catch (Exception e) {
                //Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    Handler handlerLongPress = new Handler();

    int showActivity = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isUnableRelease() || (switchAction.HIDE == widgetClickAction)) {
                    if(isFileGif){
                        gifViewWidget.startAnimation(animLongRotate);
                    }else{
                        imageViewWidget.startAnimation(animLongRotate);
                    }
                    runAnimation = false;
                }
                x = tlparams.x;
                y = tlparams.y;

                pressedX = event.getRawX();
                pressedY = event.getRawY();
                showActivity = 0;
                handlerLongPress.postDelayed(longPressRunnable, 500);

                break;

            case MotionEvent.ACTION_MOVE:

                showActivity++;
                tlparams.x = (int) (x + (event.getRawX() - pressedX));
                tlparams.y = (int) (y + (event.getRawY() - pressedY));
                wm.updateViewLayout(linearLayout, tlparams);
                if (showActivity >= 10) {
                    handlerLongPress.removeCallbacks(longPressRunnable);
                }
                break;
            case MotionEvent.ACTION_UP:
                //      Log.loaderListCaunter("Ivent", "ACTION_UP");
                handlerLongPress.removeCallbacks(longPressRunnable);

                if (unableRelease) {
                    if (showActivity < 10) {
                        switch (widgetClickAction) {
                            case SHOW:
                                showIcon();
                                break;
                            case HIDE:
                                hideIcon();
                                break;
                            default:
                        }
                    } else {
                        randomint = 4;
                        handler.sendEmptyMessage(1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                runAnimation = true;
                            }
                        }, 300);
                    }
                    commitLastPosicion();


                }
                break;
            default:
                break;
        }
        return true;

    }


    final Random random = new Random();
    final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (randomint) {
                case 0:
                    if (!isFileGif) {
                        imageViewWidget.startAnimation(anim1);
                    } else {
                        gifViewWidget.startAnimation(anim1);
                    }
                    break;
                case 1:
                    if (!isFileGif) {
                        imageViewWidget.startAnimation(anim2);
                    } else {
                        gifViewWidget.startAnimation(anim2);
                    }
                    break;
                case 2:
                    if (!isFileGif) {
                        imageViewWidget.startAnimation(anim3);
                    } else {
                        gifViewWidget.startAnimation(anim3);
                    }
                    break;
                case 3:
                    if (!isFileGif) {
                        imageViewWidget.startAnimation(anim4);
                    } else {
                        gifViewWidget.startAnimation(anim4);
                    }
                    break;
                case 4:
                    if (!isFileGif) {
                        imageViewWidget.startAnimation(animRotate);
                    } else {
                        gifViewWidget.startAnimation(animRotate);
                    }

                    break;
                case 5:
                       if(!isFileGif){
                           imageViewWidget.startAnimation(animLongRotate);
                        }else{
                           gifViewWidget.startAnimation(animLongRotate);
                       }
                        break;
                case 100:
                    if (isFileGif) {
                        gifViewWidget.startAnimation(anim_when_hide_icons);
                    } else {
                        imageViewWidget.startAnimation(anim_when_hide_icons);
                    }
                    break;
                case 101:
                    if (isFileGif) {
                        gifViewWidget.startAnimation(anim_when_show_icons);
                    } else {
                        imageViewWidget.startAnimation(anim_when_show_icons);
                    }
                    break;
                default:
            }
            return false;
        }
    });

    public boolean isUnableRelease() {
        return unableRelease;
    }

    public void setUnableRelease(boolean unableRelease) {
        this.unableRelease = unableRelease;
    }

    Runnable longPressRunnable = new Runnable() {
        @Override
        public void run() {
            showActivity = 200;
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();

            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getTaskInfo().id == sheredpreferences.getInt("ID", 0)) {
                    activityManager.moveTaskToFront(sheredpreferences.getInt("ID", 0), ActivityManager.MOVE_TASK_WITH_HOME);
                    break;
                }
                if (tasks.size()-1==i) {
                    openApp(getBaseContext(), "com.i7xaphe.widget");
                }

            }

        }
    };

    private void startWidgetForeground(String contentText) {
        int NOTIFICATION_ID = 1;
        Intent showTaskIntent = new Intent(getApplicationContext(), MainActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //       Icon icon = null;
//
//        icon=Icon.createWithFilePath(sheredpreferences.getString("pngFile", ""));
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(contentText)
                .setSmallIcon(R.drawable.avedesk4)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }




    void showIcon() {
        setUnableRelease(false);
        randomint = 101;
        handler.sendEmptyMessage(1);
        widgetClickAction = switchAction.HIDE;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 8; i++) {
                    imageViewList.get(i).setVisibility(View.INVISIBLE);
                }
                layoutParams = (TableRow.LayoutParams) imageViewWidget.getLayoutParams();
                layoutParams.setMargins(marginWidgetLeftRight, marginWidgetTopBot, marginWidgetLeftRight, marginWidgetTopBot);
                if (isFileGif) {
                    gifViewWidget.setLayoutParams(layoutParams);
                } else {
                    imageViewWidget.setLayoutParams(layoutParams);
                }
                spaceTOP.getLayoutParams().width = space;
                spaceBOT.getLayoutParams().width = space;
                spaceLeft.getLayoutParams().height = space;
                spaceRight.getLayoutParams().height = space;

                for (int i = 0; i < 8; i++) {
                    final int finalI = i;
                    imageViewList.get(i).postOnAnimationDelayed(new Runnable() {
                        @Override
                        public void run() {

                            imageViewList.get(finalI).setVisibility(View.VISIBLE);
                            imageViewList.get(finalI).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show));

                            if (finalI == 8 - 1) {
                                setUnableRelease(true);
                                runAnimation = true;
                            }

                        }

                    }, 60 * (1 + i));
                }
            }
        }, 200);

    }

    void hideIcon() {
        setUnableRelease(false);
        widgetClickAction = switchAction.SHOW;
        randomint = 100;
        handler.sendEmptyMessage(1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 8; i++) {
                    imageViewList.get(i).startAnimation(hide);
                }
            }
        }, 100);
    }
}