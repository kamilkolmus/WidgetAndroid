package com.i7xaphe.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

public class PlayGifView extends View {

    private static final int DEFAULT_MOVIEW_DURATION = 1000;

    private Movie mMovie;
    String file;
    private long mMovieStart = 0;
    private int mCurrentAnimationTime = 0;

    int scaleWindowX = 1, scaleWindowY = 1;
    private float scaleX = 1;
    private float scaleY = 1;

    @SuppressLint("NewApi")
    public PlayGifView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setMYScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public void setMYScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public void setGif(String file) {
        this.file = file;
        mMovie = Movie.decodeFile(file);
        this.scaleWindowX = mMovie.width();
        this.scaleWindowX = mMovie.height();
        Log.i("setGif", "setGif");
    }

    int checkMovieWidth(String file) {
        try {
            return Movie.decodeFile(file).width();
        } catch (Exception e) {
            return 1;
        }
    }

    int checkMovieHeight(String file) {
        try {
            return Movie.decodeFile(file).height();
        } catch (Exception e) {
            return 1;
        }
    }

    public void setGif(String file, int scaleWindowX, int scaleWindowY) {
        this.file = file;
        this.scaleWindowX = scaleWindowX;
        this.scaleWindowY = scaleWindowY;
        mMovie = Movie.decodeFile(file);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMovie != null) {
            setMeasuredDimension(scaleWindowX, scaleWindowY);
        } else {
            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
        }
    }


    @Override
    public void startAnimation(Animation animation) {
        super.startAnimation(animation);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie != null) {
            updateAnimtionTime();
            drawGif(canvas);
            invalidate();
        } else {
            drawGif(canvas);
        }
    }

    private void updateAnimtionTime() {
        long now = android.os.SystemClock.uptimeMillis();

        if (mMovieStart == 0) {
            mMovieStart = now;
        }
        int dur = mMovie.duration();
        if (dur == 0) {
            dur = DEFAULT_MOVIEW_DURATION;
        }
        mCurrentAnimationTime = (int) ((now - mMovieStart) % dur);
        //  Log.i("updateAnimtionTime","cuttent time = "+mCurrentAnimationTime );
    }

    private void drawGif(Canvas canvas) {
        try {
            mMovie.setTime(mCurrentAnimationTime);
            canvas.scale(scaleX, scaleY);
            mMovie.draw(canvas, 0, 0);
            canvas.restore();
            //       Log.i("drawGif","cuttent time = "+mCurrentAnimationTime );

        } catch (Exception e) {

        }

    }

}