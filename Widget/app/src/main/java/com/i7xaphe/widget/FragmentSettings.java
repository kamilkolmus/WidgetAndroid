package com.i7xaphe.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 2016-08-13.
 */
public class FragmentSettings extends Fragment implements View.OnTouchListener{
    private SeekBar sb_size, sb_radius,sb_icon,sb_alpha,sb_rotate_freq;
    private TextView tv_size, tv_radius,tv_icon,tv_alpha,tv_rotate_freq;
    SharedPreferences sheredpreferences;
    SharedPreferences.Editor editor;
    Spinner spinnerColor;
    boolean spinnerFirstSelectio = false;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings,container,false);

        MobileAds.initialize(getContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        sheredpreferences = getActivity().getSharedPreferences(MainActivity.sharePref, Context.MODE_PRIVATE);
        editor = sheredpreferences.edit();

        final int stepWidgetSize = 1;
        int maxWidgetSize = 100;
        final int minWidgetSize = 20;

        final int stepIconSize = 1;
        int maxIconSize = 60;
        final int minIconSize = 20;

        final int stepRadius = 1;
        int maxRadius = 100;
        final int minRadius = 20;

        final double stepScaleY = 0.01;
        double maxScaleY = 2.0;
        final double minScaleY = 0.5;

        final int stepAlpha = 1;
        double maxAlpha = 100;
        final int minAlpha = 0;

        final int stepRotateFreq = 1;
        double maxRotateFreq = 60;
        final int minRotateFreq = 1;

        sb_size = (SeekBar) v.findViewById(R.id.sb_widget_size);
        sb_radius = (SeekBar) v.findViewById(R.id.sb_radius);
        sb_icon = (SeekBar) v.findViewById(R.id.sb_icon_size);
        sb_alpha=(SeekBar)v.findViewById(R.id.sb_alpha);
        sb_rotate_freq=(SeekBar)v.findViewById(R.id.sb_rotate_freq);

        tv_size = (TextView) v.findViewById(R.id.tv_widget_size);
        tv_radius = (TextView) v.findViewById(R.id.tv_radius);
        tv_icon= (TextView) v.findViewById(R.id.tv_icon_size);
        tv_alpha=(TextView) v.findViewById(R.id.tv_alpha);
        tv_rotate_freq=(TextView)v.findViewById(R.id.tv_rotate_freq);

        sb_size.setMax( (maxWidgetSize - minWidgetSize) / stepWidgetSize );
        sb_radius.setMax((maxRadius - minRadius) / stepRadius);
        sb_icon.setMax((maxIconSize - minIconSize) / stepIconSize);
        sb_alpha.setMax((int)((maxAlpha - minAlpha) / stepAlpha));
        sb_rotate_freq.setMax((int)((maxRotateFreq - minRotateFreq) / stepRotateFreq));

        sb_size.setProgress(sheredpreferences.getInt("widgetSize", MySettings.widgetSize)-minWidgetSize);
        sb_icon.setProgress(sheredpreferences.getInt("iconSize", MySettings.iconSize)-minIconSize);
        sb_radius.setProgress(sheredpreferences.getInt("radius", MySettings.radius)-minRadius);
        sb_alpha.setProgress(100-sheredpreferences.getInt("alpha", MySettings.alpha));
        sb_rotate_freq.setProgress(sheredpreferences.getInt("rotateFreq", MySettings.randomAnimFreq));

        tv_size.setText(""+sheredpreferences.getInt("widgetSize", MySettings.widgetSize));
        tv_icon.setText(""+sheredpreferences.getInt("iconSize", MySettings.iconSize));
        tv_radius.setText(""+sheredpreferences.getInt("radius", MySettings.radius));
        tv_alpha.setText(""+(100-sheredpreferences.getInt("alpha", MySettings.alpha))+"%");
        tv_rotate_freq.setText(""+sheredpreferences.getInt("rotateFreq", MySettings.randomAnimFreq)+"s");
      //  tv_scale_y.setOnTouchListener(this);

        sb_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = minWidgetSize + (progressValue * stepWidgetSize);
                tv_size.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tv_size.setText(""+progress);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                tv_size.setText(""+progress);
                editor.putInt("widgetSize",progress);
                editor.apply();
                widgetRestrart();
            }
        });
        sb_icon.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = minIconSize + (progressValue * stepIconSize);
                tv_icon.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tv_icon.setText(""+progress);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                tv_icon.setText(""+progress);
                editor.putInt("iconSize",progress);
                editor.apply();
                widgetRestrart();
            }
        });
        sb_radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = minRadius + (progressValue * stepRadius);
                tv_radius.setText(""+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tv_radius.setText(""+progress);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv_radius.setText(""+progress);
                editor.putInt("radius",progress);
                editor.apply();
                widgetRestrart();
            }
        });


        sb_alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = minAlpha + (progressValue * stepAlpha);
                tv_alpha.setText(""+(int)(progress)+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tv_alpha.setText(""+(int)(progress)+"%");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                tv_alpha.setText(""+(int)(progress)+"%");
                editor.putInt("alpha", 100-progress);
                editor.apply();
                widgetRestrart();
            }
        });

        final Switch switchIconShowHide = (Switch) v.findViewById(R.id.sw_hide_icons);
        final Switch switchBoundaries = (Switch) v.findViewById(R.id.sw_boundaries);
        final Switch switchWidgetForeground = (Switch) v.findViewById(R.id.sw_foreground);
        final Switch switchRotateAnim= (Switch) v.findViewById(R.id.sw_rotate_animation);
        final Switch switchRandomAnim= (Switch) v.findViewById(R.id.sw_random_animation);
        final Switch switchStartUp= (Switch) v.findViewById(R.id.sw_add_startApp);


        switchIconShowHide.setChecked(sheredpreferences.getBoolean("iconShowHide", MySettings.iconShowHide));
        switchBoundaries.setChecked(sheredpreferences.getBoolean("widgetBoundaries", MySettings.boundariesOn));
        switchWidgetForeground.setChecked(sheredpreferences.getBoolean("widgetForeground", MySettings.runForeground));
        switchRotateAnim.setChecked(sheredpreferences.getBoolean("rotateAnim", MySettings.randomAnim));
        switchRandomAnim.setChecked(sheredpreferences.getBoolean("randomAnim", MySettings.randomAnim));
        switchStartUp.setChecked(sheredpreferences.getBoolean("startUp", MySettings.startUp));


        final LinearLayout ll_ratate_ferq= (LinearLayout) v.findViewById(R.id.ll_rotate_container);
        if(switchRandomAnim.isChecked()){
            ll_ratate_ferq.setVisibility(View.VISIBLE);
        }else{
            ll_ratate_ferq.setVisibility(View.GONE);
        }

        switchIconShowHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("iconShowHide",isChecked);
                if(isChecked){
                    switchIconShowHide.setText(R.string.hide_icons_when_you_select_applications);
                }else{
                    switchIconShowHide.setText(R.string.show_icons_when_you_select_applications);
                }
                editor.apply();
                widgetRestrart();
            }
        });

        switchBoundaries.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("widgetBoundaries",isChecked);
                if(isChecked){
                    switchBoundaries.setText(R.string.widget_boundaries_on);
                }else{
                    switchBoundaries.setText(R.string.widget_boundaries_off);
                }
                editor.apply();
                widgetRestrart();
            }
        });

        switchWidgetForeground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("widgetForeground",isChecked);
                if(isChecked){
                    switchWidgetForeground.setText("RUN FOREGROUND ");
                }else{
                    switchWidgetForeground.setText("RUN BACKGROUND ");
                }
                editor.apply();
               widgetRestrart();
            }
        });

        switchRotateAnim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                editor.putBoolean("rotateAnim",isChecked);
                if(isChecked){
                    switchRotateAnim.setText(R.string.rotate_animation_on);

                }else{
                    switchRotateAnim.setText(R.string.rotate_animation_off);

                }
                editor.apply();
                widgetRestrart();
            }
        });

        switchRandomAnim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                editor.putBoolean("randomAnim",isChecked);
                if(isChecked){
                    switchRandomAnim.setText(R.string.rotate_random_anim_on);
                    ll_ratate_ferq.setVisibility(View.VISIBLE);
//                    ll_ratate_ferq.setAlpha(0.0f);
//                    ll_ratate_ferq.animate().translationY(0).alpha(1.0f).setDuration(800).setListener(null);
                }else{
                    switchRandomAnim.setText(R.string.rotate_random_anim_off);
                    ll_ratate_ferq.setVisibility(View.GONE);
//                    ll_ratate_ferq.animate().translationY(0).alpha(0.0f).setDuration(800)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    super.onAnimationEnd(animation);
//                                   //     ll_ratate_ferq.setVisibility(View.GONE);
//
//                                }
//                            });

                }
                editor.apply();
                widgetRestrart();
            }
        });

        switchStartUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("startUp",isChecked);
                if(isChecked){
                    switchStartUp.setText(R.string.add_widget_to_startup_on);
                }else{
                    switchStartUp.setText(R.string.add_widget_to_startup_off);
                }
                editor.apply();
            }
        });

        if(sheredpreferences.getBoolean("iconShowHide", MySettings.iconShowHide)){
            switchIconShowHide.setText(R.string.hide_icons_when_you_select_applications);
        }else{
            switchIconShowHide.setText(R.string.show_icons_when_you_select_applications);
        }

        if(sheredpreferences.getBoolean("widgetBoundaries", MySettings.boundariesOn)){
            switchBoundaries.setText(R.string.widget_boundaries_on);
        }else{
            switchBoundaries.setText(R.string.widget_boundaries_off);
        }

        if(sheredpreferences.getBoolean("widgetForeground", MySettings.runForeground)){
            switchWidgetForeground.setText("RUN FOREGROUND ");
        }else{
            switchWidgetForeground.setText("RUN BACKGROUND ");
        }

        if(sheredpreferences.getBoolean("rotateAnim", MySettings.rotateAnim)){
            switchRotateAnim.setText(R.string.rotate_animation_on);
        }else{
            switchRotateAnim.setText(R.string.rotate_animation_off);
        }

        if(sheredpreferences.getBoolean("randomAnim", MySettings.randomAnim)){
            switchRandomAnim.setText(R.string.rotate_random_anim_on);
        }else{
            switchRandomAnim.setText(R.string.rotate_random_anim_off);
        }

        if(sheredpreferences.getBoolean("startUp", MySettings.startUp)){
            switchStartUp.setText(R.string.add_widget_to_startup_on);
        }else{
            switchStartUp.setText(R.string.add_widget_to_startup_off);
        }

        sb_rotate_freq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = minRotateFreq + (progressValue * stepRotateFreq);
                tv_rotate_freq.setText(""+(int)(progress)+"s");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                tv_rotate_freq.setText(""+(int)(progress)+"s");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                tv_rotate_freq.setText(""+(int)(progress)+"s");
                editor.putInt("rotateFreq", progress);
                editor.apply();
                widgetRestrart();
            }
        });





        spinnerColor = (Spinner) v.findViewById(R.id.spinner_color);

        List<String> categories = new ArrayList<String>();
        categories.add("CUSTOM");
        categories.add("BLUE");
        categories.add("YELLOW");
        categories.add("WHITE");
        categories.add("DARK");
        categories.add("PURPLE");
        categories.add("RED");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinnerColor.setAdapter(dataAdapter);
        spinnerFirstSelectio=false;
        spinnerColor.setSelection(sheredpreferences.getInt("widgetColor", MySettings.widgetColor));
        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinnerFirstSelectio){
                    editor.putInt("widgetColor",position);
                    editor.apply();
                    widgetRestrart();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button bCastomWidget = (Button) v.findViewById(R.id.b_custom_widget);
        bCastomWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openBrowserDialogRecycleView();
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                spinnerFirstSelectio =true;
            }
        }, 500);

    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    float dX, dY;
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:

                view.animate()
                        .x(event.getRawX() + dX)
                        .y(event.getRawY() + dY)
                        .setDuration(0)
                        .start();
                break;
            default:
                return false;
        }
        return true;
    }


    void widgetRestrart() {
        ((MainActivity) getActivity()).startWidget();

    }

    public void restartSpinnerWidget() {
        spinnerColor.setSelection(sheredpreferences.getInt("widgetColor", MySettings.widgetColor));
    }
}