package com.i7xaphe.widget;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class FragmentSelectedApps2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match


    PackageManager packageManager;
    MyFileReader packedList;
    ArrayList<ImageView> imageButtonArrayList = new ArrayList<>();
    ConstraintLayout constraintLayout;
    View widget;
    SharedPreferences sheredpreferences;
    SharedPreferences.Editor editor;
    myDragEventListener mDragListen = new myDragEventListener();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_selected_apps_2,container,false);


        return v;
    }

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        sheredpreferences = getActivity().getSharedPreferences(MainActivity.sharePref, Context.MODE_PRIVATE);
        editor = sheredpreferences.edit();

        packageManager = getContext().getPackageManager();
        File file = new File(MainActivity.fileFULL);
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        packedList = new MyFileReader(file);

        constraintLayout = (ConstraintLayout) v.findViewById(R.id.constraint_layout_fragment);

        String widgetImagePath = sheredpreferences.getString("pngFile", "");
        int color = sheredpreferences.getInt("widgetColor", MySettings.widgetColor);
        boolean isWidgetGif;
        if (color == 0) {
            if ((widgetImagePath.endsWith(".gif") || (widgetImagePath.endsWith(".GIF")))) {
                widget = (PlayGifView) v.findViewById(R.id.iv_widget_gif_fragment);
                widget.setVisibility(View.VISIBLE);
                v.findViewById(R.id.iv_widget_fragment).setVisibility(View.GONE);
                isWidgetGif = true;
            } else {
                widget = (ImageView) v.findViewById(R.id.iv_widget_fragment);
                widget.setVisibility(View.VISIBLE);
                v.findViewById(R.id.iv_widget_gif_fragment).setVisibility(View.GONE);
                isWidgetGif = false;
            }
        } else {
            widget = (ImageView) v.findViewById(R.id.iv_widget_fragment);
            widget.setVisibility(View.VISIBLE);
            v.findViewById(R.id.iv_widget_gif_fragment).setVisibility(View.GONE);
            isWidgetGif = false;
        }
        switch (color) {
            case 0:
                File imgFile = new File(sheredpreferences.getString("pngFile", ""));
                if (imgFile.exists()) {
                    if (!isWidgetGif) {
                        try {
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            ((ImageView) widget).setImageBitmap(myBitmap);
                        }catch (OutOfMemoryError e){
                            e.printStackTrace();
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = false;
                            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                            options.inDither = true;
                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
                            ((ImageView) widget).setImageBitmap(myBitmap);
                        }
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


        int widgetSize = (int)(dpToPx(sheredpreferences.getInt("widgetSize", MySettings.widgetSize))*1.6);
        final int iconSize = (int)(dpToPx(sheredpreferences.getInt("iconSize", MySettings.iconSize))*1.6);

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

        mDragListen = new myDragEventListener();
        float angle;
        try {
            angle = 360 /(float) (packedList.getListSize());
        } catch (ArithmeticException e) {
            angle = 0;
        }
        int radius = (int)(dpToPx(sheredpreferences.getInt("radius", MySettings.radius))*1.6);

        for (int i = 0; i < packedList.getListSize(); i++) {
            // final ImageButton imageButton= new ImageButton(this);
            imageButtonArrayList.add(new ImageView(getActivity()));

            imageButtonArrayList.get(i).setBackgroundColor(Color.TRANSPARENT);
            imageButtonArrayList.get(i).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
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
            imageButtonArrayList.get(i).setId(121222120 + i);
            imageButtonArrayList.get(i).setVisibility(View.VISIBLE);
            imageButtonArrayList.get(i).setTag((CharSequence)packedList.getElement(i));
            final int finalI = i;
            imageButtonArrayList.get(i).setOnDragListener(mDragListen);
            imageButtonArrayList.get(i).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    imageButtonArrayList.get(finalI).startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.icon_click_anim));
                    ClipData.Item item = new ClipData.Item((CharSequence) imageButtonArrayList.get(finalI).getTag());
                    ClipData dragData = new ClipData((CharSequence) imageButtonArrayList.get(finalI).getTag(),new String[]{ ClipDescription.MIMETYPE_TEXT_PLAIN },item);

                    View.DragShadowBuilder myShadow = new MyDragShadowBuilder(imageButtonArrayList.get(finalI),packageManager,iconSize);

                    v.startDrag(dragData,  // the dataFormShadow to be dragged
                            myShadow,  // the drag shadow builder
                            null,      // no need to use local dataFormShadow
                            0          // flags (not currently used, set to 0)
                    );
                    return true;
                }
            });

            constraintLayout.addView(imageButtonArrayList.get(finalI));
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.constrainCircle(imageButtonArrayList.get(finalI).getId(), widget.getId(), radius, angle * i);
            constraintSet.applyTo(constraintLayout);
        }


    }
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private static class MyDragShadowBuilder extends View.DragShadowBuilder {

        // The drag shadow image, defined as a drawable thing
        private Bitmap  shadow;
        private static int size;
        // Defines the constructor for myDragShadowBuilder
        public MyDragShadowBuilder(View v,PackageManager packageManager,int size) {

            // Stores the View parameter passed to myDragShadowBuilder.
            super(v);

            BitmapDrawable bitmapDrawable = null;
            try {
                bitmapDrawable = (BitmapDrawable)packageManager.getActivityIcon(packageManager.getLaunchIntentForPackage((String)v.getTag()));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            shadow= bitmapDrawable.getBitmap();
            this.size=size;

        }

        // Defines a callback that sends the drag shadow dimensions and touch point back to the
        // system.
        @Override
        public void onProvideShadowMetrics (Point size, Point touch) {
            size.set((int)(MyDragShadowBuilder.size), (int)(MyDragShadowBuilder.size));
            touch.set(MyDragShadowBuilder.size/4 , MyDragShadowBuilder.size/4 );
        }

        // Defines a callback that draws the drag shadow in a Canvas that the system constructs
        // from the dimensions passed in onProvideShadowMetrics().
        @Override
        public void onDrawShadow(Canvas canvas) {

            canvas.drawBitmap(Bitmap.createScaledBitmap(shadow, size, size, false),0,0,null);

            // Draws the ColorDrawable in the Canvas passed in from the system.
        //    shadow.draw(canvas);
        }
    }



    protected class myDragEventListener implements View.OnDragListener {

        // This is the method that the system calls when it dispatches a drag event to the
        // listener.
        String dataFormShadow=null;
        String dataFormDropedViev=null;
        int action_drag_ended_counter =0;

        public boolean onDrag(final View v, DragEvent event) {

            // Defines a variable to store the action type for the incoming event
            final int action = event.getAction();

            // Handles each of the expected events
            switch(action) {

                case DragEvent.ACTION_DRAG_STARTED:
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                        ((ImageView)v).setColorFilter(new PorterDuffColorFilter(0xBBBBBBBB, PorterDuff.Mode.MULTIPLY) );
                        v.invalidate();
                        return true;

                    }
                    return false;

                case DragEvent.ACTION_DRAG_ENTERED:

                    ((ImageView)v).clearColorFilter();
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:

                    return true;

                case DragEvent.ACTION_DRAG_EXITED:

//                    try {
//                        ((ImageView)v).setImageDrawable(packageManager.getActivityIcon(packageManager.getLaunchIntentForPackage((String) v.getTag())));
//                    } catch (PackageManager.NameNotFoundException e) {
//                        e.printStackTrace();
//                    }

                    ((ImageView)v).setColorFilter(new PorterDuffColorFilter(0xBBBBBBBB, PorterDuff.Mode.MULTIPLY) );
                 //   ((ImageView)v).setColorFilter(Color.BLUE);
                    v.invalidate();

                    return true;

                case DragEvent.ACTION_DROP:

                    Log.e("ACTION_DROP",(String)v.getTag());
                    // Gets the item containing the dragged dataFormShadow
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    // Gets the text dataFormShadow from the item.

                    dataFormShadow =item.getText().toString();
                    dataFormDropedViev =(String)v.getTag();

                    if(dataFormShadow.equals(dataFormDropedViev)){
                        return false;
                    }

                    return true;

                case DragEvent.ACTION_DRAG_ENDED:

                    ((ImageView)v).clearColorFilter();
                    // Does a getResult(), and displays what happened.
                    if (event.getResult()) {
                        if(v.getTag().equals(dataFormShadow)){
                            try {
                                ((ImageView)v).setImageDrawable(packageManager.getActivityIcon(packageManager.getLaunchIntentForPackage((String) dataFormDropedViev)));
                                v.setTag(dataFormDropedViev);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else if(v.getTag().equals(dataFormDropedViev)){
                            try {
                                ((ImageView)v).setImageDrawable(packageManager.getActivityIcon(packageManager.getLaunchIntentForPackage((String) dataFormShadow)));
                                v.setTag(dataFormShadow);
//
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                        action_drag_ended_counter++;
                        if(action_drag_ended_counter ==packedList.getListSize()){
                            action_drag_ended_counter =0;
                            int listSize=packedList.getListSize();
                            packedList.clearList();
                            for(int i=0;i<listSize;i++){
                                packedList.addToList((String) imageButtonArrayList.get(i).getTag());
                            }
                            packedList.confirmChanges();
                            ((MainActivity) getActivity()).startWidget();
                        }
                        v.invalidate();
                    }


                    // returns true; the value is ignored.
                    return true;

                // An unknown action type was received.
                default:
                    Log.e("DragDrop Example","Unknown action type received by OnDragListener.");
                    break;
            }

            return false;
        }
    };
}
