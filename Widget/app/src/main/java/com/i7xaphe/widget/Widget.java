package com.i7xaphe.widget;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
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

import java.io.File;
import java.io.IOException;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Random;

public class Widget extends Service implements View.OnTouchListener, AnimationCallBacks {

    WindowManager wm;
    RandomAnimAsyncTask randomAnimAsyncTask;
    //fields that hold widget position
    double x;
    double y;
    double pressedX;
    double pressedY;

    //
    ConstraintLayout constraintLayout;
    LayoutParams layoutParams;
    View widget;
    ArrayList<ImageView> imageButtonArrayList = new ArrayList<>();

    //
    SharedPreferences sheredpreferences;
    SharedPreferences.Editor editor;

    PackageManager packageManager;
    MyFileReader packedList;

    AnimationCallBacks animationCallBacks = this;
    private boolean clickWidgetPossibility;

    final String ACTION_SHOW = "ACTION_SHOW";
    final String ACTION_HIDE = "ACTION_HIDE";

    //==============================================================================================
    private final IBinder binder = new LocalBinder();


    public class LocalBinder extends Binder {
        Widget getService() {
            return Widget.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return binder;
    }
    //==============================================================================================

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        sheredpreferences = getSharedPreferences(MainActivity.sharePref, Context.MODE_PRIVATE);
        editor = sheredpreferences.edit();

        packageManager = getApplicationContext().getPackageManager();
        File file = new File(MainActivity.fileFULL);
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        packedList = new MyFileReader(file);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        //set layout params
        if (sheredpreferences.getBoolean("widgetBoundaries", MySettings.boundariesOn)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_APPLICATION_OVERLAY,
                        LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_HARDWARE_ACCELERATED, PixelFormat.TRANSPARENT);
            } else {
                layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_PHONE,
                        LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_HARDWARE_ACCELERATED, PixelFormat.TRANSPARENT);
            }
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_APPLICATION_OVERLAY,
                        LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_HARDWARE_ACCELERATED | LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSPARENT);
            } else {
                layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_PHONE,
                        LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_HARDWARE_ACCELERATED | LayoutParams.FLAG_LAYOUT_NO_LIMITS, PixelFormat.TRANSPARENT);
            }
        }

        layoutParams.gravity = Gravity.CENTER;
        layoutParams.windowAnimations = android.R.style.Animation_Activity;

        //ustawienie flagi braku animacji podczas zmiany wielkosci okna
        int currentFlags = 0;
        try {
            currentFlags = (Integer) layoutParams.getClass().getField("privateFlags").get(layoutParams);
            layoutParams.getClass().getField("privateFlags").set(layoutParams, currentFlags | 0x00000040 | 0x00000001);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


        layoutParams.alpha = (float) 0.01 * sheredpreferences.getInt("alpha", MySettings.alpha);
        layoutParams.x = sheredpreferences.getInt("widgetX", 0);
        layoutParams.y = sheredpreferences.getInt("widgetY", 0);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        constraintLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.widget, null);

        String widgetImagePath = sheredpreferences.getString("pngFile", "");


        int color = sheredpreferences.getInt("widgetColor", MySettings.widgetColor);
        boolean isWidgetGif;
        if (color == 0) {
            if ((widgetImagePath.endsWith(".gif") || (widgetImagePath.endsWith(".GIF")))) {
                widget = (PlayGifView) constraintLayout.findViewById(R.id.iv_widget_gif);
                widget.setVisibility(View.VISIBLE);
                constraintLayout.findViewById(R.id.iv_widget).setVisibility(View.GONE);
                isWidgetGif = true;
            } else {
                widget = (ImageView) constraintLayout.findViewById(R.id.iv_widget);
                widget.setVisibility(View.VISIBLE);
                constraintLayout.findViewById(R.id.iv_widget_gif).setVisibility(View.GONE);
                isWidgetGif = false;
            }
        } else {
            widget = (ImageView) constraintLayout.findViewById(R.id.iv_widget);
            widget.setVisibility(View.VISIBLE);
            constraintLayout.findViewById(R.id.iv_widget_gif).setVisibility(View.GONE);
            isWidgetGif = false;
        }

        switch (color) {
            case 0:
                File imgFile = new File(sheredpreferences.getString("pngFile", ""));
                if (imgFile.exists()) {
                    if (!isWidgetGif) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        ((ImageView) widget).setImageBitmap(myBitmap);
                    }
                } else {
                    ((ImageView) widget).setImageResource(R.drawable.widget_blue);
                    editor.putInt("widgetColor", 1);
                    editor.apply();
                }
                break;
            case 1:
                ((ImageView) widget).setImageResource(R.drawable.widget_blue);
                break;
            case 2:
                ((ImageView) widget).setImageResource(R.drawable.widget_yellow);
                break;
            case 3:
                ((ImageView) widget).setImageResource(R.drawable.widget_white);
                break;
            case 4:
                ((ImageView) widget).setImageResource(R.drawable.widget_dark);
                break;
            case 5:
                ((ImageView) widget).setImageResource(R.drawable.widget_purple);
                break;
            case 6:
                ((ImageView) widget).setImageResource(R.drawable.widget_red);
                break;

            default:
        }


        int widgetSize = dpToPx(sheredpreferences.getInt("widgetSize", MySettings.widgetSize));
        int iconSize = dpToPx(sheredpreferences.getInt("iconSize", MySettings.iconSize));
        int radius = dpToPx(sheredpreferences.getInt("radius", MySettings.radius));

        //WIEKOSC WIDGET
        if (isWidgetGif) {
            int realMovieX = ((PlayGifView) widget).checkMovieWidth(widgetImagePath);
            int realMovieY = ((PlayGifView) widget).checkMovieHeight(widgetImagePath);
            int newMovieX;
            int newMovieY;
            if (realMovieX >= realMovieY) {
                newMovieX = (int) ((1 + (float) widgetSize / 400.0) * widgetSize);
                newMovieY = (int) ((1 + (float) widgetSize / 400.0) * widgetSize * realMovieY / realMovieX);
                ((PlayGifView) widget).setMYScaleX(newMovieX / (float) realMovieX);
                ((PlayGifView) widget).setMYScaleY(newMovieX / (float) realMovieX);
            } else {
                newMovieY = (int) ((1 + (float) widgetSize / 400.0) * widgetSize);
                newMovieX = (int) ((1 + (float) widgetSize / 400.0) * widgetSize * realMovieX / realMovieY);
                ((PlayGifView) widget).setMYScaleX(newMovieY / (float) realMovieY);
                ((PlayGifView) widget).setMYScaleY(newMovieY / (float) realMovieY);
            }
            ((PlayGifView) widget).setGif(sheredpreferences.getString("pngFile", ""), newMovieX, newMovieY);
        } else {
            (widget).getLayoutParams().height = widgetSize;
            (widget).getLayoutParams().width = widgetSize;
        }


        float nbPackedes = packedList.getListSize();
        float angle;
        try {
            angle = 360 / (nbPackedes);
        } catch (ArithmeticException e) {
            angle = 0;
        }

        for (int i = 0; i < nbPackedes; i++) {
            // final ImageButton imageButton= new ImageButton(this);
            imageButtonArrayList.add(new ImageView(this));
            imageButtonArrayList.get(i).setBackgroundColor(Color.TRANSPARENT);
            imageButtonArrayList.get(i).setLayoutParams(new LayoutParams());
            imageButtonArrayList.get(i).getLayoutParams().height = iconSize;
            imageButtonArrayList.get(i).getLayoutParams().width = iconSize;
            try {
                imageButtonArrayList.get(i).setImageDrawable(packageManager.getActivityIcon(packageManager.getLaunchIntentForPackage(packedList.getElement(i))));

            } catch (PackageManager.NameNotFoundException e) {
                imageButtonArrayList.get(i).setImageDrawable(getResources().getDrawable(R.drawable.mono_point));
            } catch (NullPointerException e) {
                imageButtonArrayList.get(i).setImageDrawable(getResources().getDrawable(R.drawable.mono_point));
            } catch (IndexOutOfBoundsException e) {
                imageButtonArrayList.get(i).setImageDrawable(getResources().getDrawable(R.drawable.mono_point));
            }
            imageButtonArrayList.get(i).setId(121202120 + i);
            imageButtonArrayList.get(i).setVisibility(View.GONE);
            final int finalI = i;
            imageButtonArrayList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation iconClickAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.icon_click_anim);
                    iconClickAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            animationCallBacks.onClickIconAnimationStart();
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            animationCallBacks.onClickIconAnimationEnd();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    imageButtonArrayList.get(finalI).startAnimation(iconClickAnimation);

                    openApp(getBaseContext(), packedList.getElement(finalI));


                }
            });


            constraintLayout.addView(imageButtonArrayList.get(finalI));


            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.constrainCircle(imageButtonArrayList.get(finalI).getId(), widget.getId(), radius, angle * i);


            constraintSet.applyTo(constraintLayout);

        }

        wm.addView(constraintLayout, layoutParams);


        widget.setOnTouchListener(this);

        if (sheredpreferences.getBoolean("widgetForeground", MySettings.runForeground)) {
            startWidgetForeground("Click to open Widget");

        }
        unLockClickWidgetPossibility();
        showIcon();

        widget.setTag(ACTION_HIDE);

        if (sheredpreferences.getBoolean("randomAnim", MySettings.randomAnim)) {
            randomAnimAsyncTask = new RandomAnimAsyncTask();
            randomAnimAsyncTask.execute();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wm.removeView(constraintLayout);
        if (randomAnimAsyncTask != null) {
            randomAnimAsyncTask.cancel(false);
        }
        stopSelf();
    }

    void commitLastPosicion() {
        editor.putInt("widgetX", layoutParams.x);
        editor.putInt("widgetY", layoutParams.y);
        editor.commit();
    }


    public boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();


        try {
            Intent intent = manager.getLaunchIntentForPackage(packageName);
            if (intent == null) {
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


    //==================================================================================================
    Handler handlerLongPress = new Handler();
    Runnable longPressRunnable = new Runnable() {
        @Override
        public void run() {
            //ustawieniewartości wiekszej niż 10 żeby
            showActivity = 200;
            openApp(getBaseContext(), "com.i7xaphe.widget");
        }
    };
    int showActivity = 0;


    @Override
    public boolean onTouch(View v, MotionEvent event) {


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //    if (((String) imageViewWidget.getTag()).equals(ACTION_SHOW)) {
                //  if(checkClickWidgetPossibility()){
                if (sheredpreferences.getBoolean("rotateAnim", MySettings.rotateAnim)) {
                    Animation animation = widget.getAnimation();
                    if (animation == null) {

                        widget.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_longrotate));

                    } else {
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                widget.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_longrotate));
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }

                showActivity = 0;
                handlerLongPress.postDelayed(longPressRunnable, 500);
                //    }
                //       runAnimation = false;
                //   }
                x = layoutParams.x;
                y = layoutParams.y;

                pressedX = event.getRawX();
                pressedY = event.getRawY();


                break;

            case MotionEvent.ACTION_MOVE:

                showActivity++;
                layoutParams.x = (int) (x + (event.getRawX() - pressedX));
                layoutParams.y = (int) (y + (event.getRawY() - pressedY));
                wm.updateViewLayout(constraintLayout, layoutParams);
                if (showActivity >= 10) {
                    handlerLongPress.removeCallbacks(longPressRunnable);
                }
                break;
            case MotionEvent.ACTION_UP:
                //      Log.loaderListCaunter("Ivent", "ACTION_UP");
                handlerLongPress.removeCallbacks(longPressRunnable);

                if (checkClickWidgetPossibility()) {
                    if (showActivity < 10) {
                        switch (((String) widget.getTag())) {
                            case ACTION_SHOW:
                                Log.i("imageViewWidget.getTag", "ACTION_SHOW");
                                showIcon();
                                break;
                            case ACTION_HIDE:
                                Log.i("imageViewWidget.getTag", "ACTION_HIDE");
                                hideIcon();
                                break;
                            default:
                        }
                    } else {
                        if (sheredpreferences.getBoolean("rotateAnim", MySettings.rotateAnim)) {
                            final Animation releaseWisgetAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.release_widget_anim);
                            releaseWisgetAnim.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    animationCallBacks.onReleaseWidgetAnimationStart();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    animationCallBacks.onReleaseWidgetAnimationStart();

                                }

                            });
                            widget.startAnimation(releaseWisgetAnim);
                        }
                    }
                    commitLastPosicion();
                }
                break;
            default:
                break;
        }
        return true;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////

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

    private void runOnUiThread(Runnable runnable) {
        new Handler().postAtFrontOfQueue(runnable);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////


    void showIcon() {
        final Animation widgetAnimWhenIconShow;
        if (sheredpreferences.getBoolean("rotateAnim", MySettings.rotateAnim)) {
            widgetAnimWhenIconShow= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_when_show_icon_rotate);
        }else{
            widgetAnimWhenIconShow= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_when_show_icon);
        }


        widgetAnimWhenIconShow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animationCallBacks.onWidgetAnimWhenIconShowStart();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animationCallBacks.onWidgetAnimWhenIconShowHalf();
                    }
                }, widgetAnimWhenIconShow.getDuration() / 2);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationCallBacks.onWidgetAnimWhenIconShowEnd();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        widget.startAnimation(widgetAnimWhenIconShow);

    }

    void hideIcon() {
        Animation hideIcon = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_icon);


        Animation widgetAnimWhenIconHide ;
        if (sheredpreferences.getBoolean("rotateAnim", MySettings.rotateAnim)) {
            widgetAnimWhenIconHide= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_when_hide_icon_ratate);
        }else{
            widgetAnimWhenIconHide= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_when_hide_icon);
        }


        widgetAnimWhenIconHide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animationCallBacks.onWidgetAnimWhenIconHideStart();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationCallBacks.onWidgetAnimWhenIconHideEnd();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        for (int i = 0; i < packedList.getListSize(); i++) {
            if (i == packedList.getListSize() - 1) {
                hideIcon.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        animationCallBacks.onHideIconAnimationStart();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        animationCallBacks.onHideIconAnimationEnd();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
            imageButtonArrayList.get(i).startAnimation(hideIcon);
        }


        widget.startAnimation(widgetAnimWhenIconHide);


    }

    private void lockClickWidgetPossibility() {
        clickWidgetPossibility = false;
    }

    private void unLockClickWidgetPossibility() {
        clickWidgetPossibility = true;
    }

    private boolean checkClickWidgetPossibility() {
        return clickWidgetPossibility;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //wywoływana podczas klikniecia animacji
    @Override
    public void onClickIconAnimationStart() {

    }

    @Override
    public void onClickIconAnimationEnd() {
        if (sheredpreferences.getBoolean("iconShowHide", MySettings.iconShowHide)) {
            hideIcon();
        }
    }

    //animacja icon gdy są chowane
    @Override
    public void onHideIconAnimationStart() {
        Log.i("ANIMCALLBACKS", "onHideIconAnimationStart");
        lockClickWidgetPossibility();
    }

    @Override
    public void onHideIconAnimationEnd() {
        Log.i("ANIMCALLBACKS", "onHideIconAnimationEnd");


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < packedList.getListSize(); i++) {
                    imageButtonArrayList.get(i).setVisibility(View.GONE);
                }
            }
        });


        widget.setTag(ACTION_SHOW);


        unLockClickWidgetPossibility();
    }
    //animacja icon gdy są pokazywane

    @Override
    public void onShowIconAnimationStart() {

        Log.i("ANIMCALLBACKS", "onShowIconAnimationStart");
    }

    @Override
    public void onShowIconAnimationEnd() {
        widget.setTag(ACTION_HIDE);
        unLockClickWidgetPossibility();
        Log.i("ANIMCALLBACKS", "onShowIconAnimationEnd");
    }


    //wywoływane animacja widgetu gdy chowane są icony
    @Override
    public void onWidgetAnimWhenIconHideStart() {
        lockClickWidgetPossibility();
        Log.i("ANIMCALLBACKS", "onWidgetAnimWhenIconHideStart");
    }

    @Override
    public void onWidgetAnimWhenIconHideEnd() {

        Log.i("ANIMCALLBACKS", "onWidgetAnimWhenIconHideEnd");
        widget.setTag(ACTION_SHOW);
        unLockClickWidgetPossibility();

    }

    //wywoływane animacja widgetu gdy pokazywane są icony
    @Override
    public void onWidgetAnimWhenIconShowStart() {
        lockClickWidgetPossibility();
        Log.i("ANIMCALLBACKS", "onWidgetAnimWhenIconShowStart");
    }

    @Override
    public void onWidgetAnimWhenIconShowHalf() {
        Log.i("ANIMCALLBACKS", "onWidgetAnimWhenIconShowHalf");
        for (int i = 0; i < packedList.getListSize(); i++) {
            imageButtonArrayList.get(i).setImageDrawable(null);
            imageButtonArrayList.get(i).setVisibility(View.INVISIBLE);
        }

        for (int i = 0; i < packedList.getListSize(); i++) {
            final int finalI = i;
            imageButtonArrayList.get(i).postOnAnimationDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation showIcon = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_icon);
                    imageButtonArrayList.get(finalI).setVisibility(View.VISIBLE);
                    try {
                        imageButtonArrayList.get(finalI).setImageDrawable(packageManager.getActivityIcon(packageManager.getLaunchIntentForPackage(packedList.getElement(finalI))));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (finalI == 0) {
                        animationCallBacks.onShowIconAnimationStart();
                    }
                    if (finalI == packedList.getListSize() - 1) {
                        showIcon.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                animationCallBacks.onShowIconAnimationEnd();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                    }
                    imageButtonArrayList.get(finalI).startAnimation(showIcon);
                }
            }, (int) (200 / packedList.getListSize()) * (1 + i));
        }
    }

    @Override
    public void onWidgetAnimWhenIconShowEnd() {
        Log.i("ANIMCALLBACKS", "onWidgetAnimWhenIconShowEnd");
        //  unLockClickWidgetPossibility();
        widget.setTag(ACTION_HIDE);
    }

    //wywoływana w momencie puszczenia widgetu bez pokazywania/chowania  icon
    @Override
    public void onReleaseWidgetAnimationStart() {
        Log.i("ANIMCALLBACKS", "onReleaseWidgetAnimationStart");
    }

    @Override
    public void onReleaseWidgetAnimationEnd() {
        Log.i("ANIMCALLBACKS", "onReleaseWidgetAnimationEnd");

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    class RandomAnimAsyncTask extends AsyncTask<Void, Void, Void> {

        Random random;

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //parseing all details
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            random = new Random();
            random.setSeed(System.currentTimeMillis());
            // set up values for required params
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (widget.getAnimation() == null) {
                switch (random.nextInt(4)) {
                    case 0:
                        widget.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_anim_1));
                        break;
                    case 1:
                        widget.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_anim_2));
                        break;
                    case 2:
                        widget.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_anim_3));
                        break;
                    case 3:
                        widget.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.widget_anim_4));
                        break;
                }
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //make the web service call here
            int rotateFreq = sheredpreferences.getInt("rotateFreq", MySettings.randomAnimFreq);
            do {
                try {
              //      Log.e("dsds", "sdddddddddddddd");
                    Thread.sleep(rotateFreq * 1000);
                    publishProgress();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } while (widget.isAttachedToWindow());
            return null;
        }
    }

}


