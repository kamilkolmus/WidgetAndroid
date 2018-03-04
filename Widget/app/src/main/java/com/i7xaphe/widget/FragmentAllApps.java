package com.i7xaphe.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FragmentAllApps extends Fragment {

    RecyclerView recyclerview;


    Animation anim_button;
    int longClickPosition;
    PackageManager manager;
    MyFileManager listMemoryAdapter;
    String filter = "";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fargment_all_apps, container, false);


        File file = new File(MainActivity.fileFULL);
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listMemoryAdapter = new MyFileManager(file);
        if (listMemoryAdapter.getListSize() != MainActivity.widgetIconsLimit) {
            if (listMemoryAdapter.getListSize() < MainActivity.widgetIconsLimit) {
                listMemoryAdapter.fillList(MainActivity.EmptyLine, MainActivity.widgetIconsLimit);
            } else {
                while (listMemoryAdapter.getListSize() > MainActivity.widgetIconsLimit) {
                    listMemoryAdapter.removeLine(listMemoryAdapter.getListSize() - 1);
                }
            }

        }
        manager = getContext().getPackageManager();

        recyclerview = (RecyclerView) v.findViewById(R.id.recylerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false)); // zmieni!1

        anim_button = AnimationUtils.loadAnimation(getContext(), R.anim.button_press);
        Log.i("List Fragment", "on Create VIEW");
        setNewAdapter(filter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MobileAds.initialize(getContext(), "ca-app-pub-3940256099942544~3347511713");
                AdView mAdView = (AdView) v.findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        }, 100);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        setNewAdapter(filter);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CheckBox checkBox;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_card);
            checkBox = (CheckBox) itemView.findViewById(R.id.ch_card);
            imageView = (ImageView) itemView.findViewById(R.id.iv_card);
        }
    }
    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                Log.i("opening another app", "false " + packageName);
                return false;
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            Log.i("opening another app", "true");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    void setNewAdapter(String filter) {
        this.filter = filter;
        Log.i("filter", "/" + filter);
        List<PackageInfo> filteredList = new ArrayList<>();

        for (PackageInfo packageInfo: MainActivity.packageInfos ) {
            if (packageInfo.applicationInfo.loadLabel(manager).toString().toLowerCase().contains(filter.toLowerCase())) {
                filteredList.add(packageInfo);
            }
        }
        if (filteredList.size() == 0) {
            recyclerview.setAdapter(null);
            return;
        }
        MyAdapter Adapter = new MyAdapter(filteredList);
        recyclerview.setAdapter(Adapter);
    }

    //ADAPTER FOR RECYCLERVIEW
    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        List<PackageInfo> packageInfos;

        public MyAdapter(List<PackageInfo> packageInfos) {
            this.packageInfos = packageInfos;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_all, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final Drawable drawable= manager.getActivityIcon(manager.getLaunchIntentForPackage(packageInfos.get(position).packageName));
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.imageView.setImageDrawable(drawable);
                                }
                            });
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            } catch (NullPointerException e) {
                holder.imageView.setImageDrawable(getResources().getDrawable(R.drawable.android));
            }
            try {
                holder.textView.setText((position + 1) + ". " + packageInfos.get(position).applicationInfo.loadLabel(manager).toString());
            } catch (NullPointerException e) {

            }


            holder.checkBox.setChecked(listMemoryAdapter.checkIfExist(packageInfos.get(position).packageName));
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.checkBox.isChecked()) {
                        if (listMemoryAdapter.getListSize() >= MainActivity.widgetIconsLimit && !listMemoryAdapter.contains(MainActivity.EmptyLine)) {
                            holder.checkBox.setChecked(false);
                            Toast.makeText(getContext(), "You can choose max " + MainActivity.widgetIconsLimit + " Applications", Toast.LENGTH_SHORT).show();
                        } else {
                            listMemoryAdapter.swapElement(MainActivity.EmptyLine, packageInfos.get(position).packageName);
                            ((MainActivity) getActivity()).startWidget();
                        }
                    } else {

                        listMemoryAdapter.swapElement(packageInfos.get(position).packageName, MainActivity.EmptyLine);
                        ((MainActivity) getActivity()).startWidget();

                    }
                    listMemoryAdapter.confirmChanges();
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    try {
                        longClickPosition = position;
                        view.startAnimation(anim_button);
                        Thread thread1 = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    sleep(400);
                                    Message msg = myHandler.obtainMessage();
                                    myHandler.sendMessageAtFrontOfQueue(msg);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread1.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });


        }

        Handler myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                openApp(getContext(), packageInfos.get(longClickPosition).packageName);
                recyclerview.setAdapter(new MyAdapter(MainActivity.getPackageInfos()));
            }
        };//==============================================================================================

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return packageInfos.size();
        }

    }

}
