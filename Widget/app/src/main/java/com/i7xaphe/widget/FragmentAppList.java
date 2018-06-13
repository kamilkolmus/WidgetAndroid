package com.i7xaphe.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FragmentAppList extends Fragment {

    RecyclerView recyclerView;
    MyAdapter adapter;

    PackageManager manager;
    MyFileReader listMemoryAdapter;
    String filter = "";

    List<PackageInfo> filteredPackageInfoList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fargment_app_list, container, false);


        MobileAds.initialize(getContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        File file = new File(MainActivity.fileFULL);
        file.getParentFile().mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        filteredPackageInfoList = new ArrayList<>();
        listMemoryAdapter = new MyFileReader(file);
        manager = getContext().getPackageManager();
        recyclerView = (RecyclerView) v.findViewById(R.id.recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false)); // zmieni!1
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        CheckBox checkBox =view.findViewById(R.id.ch_card);
                        if (checkBox.isChecked()) {
                            listMemoryAdapter.removeLine(filteredPackageInfoList.get(position).packageName);
                            checkBox.setChecked(false);
                        } else {
                            listMemoryAdapter.addToList(filteredPackageInfoList.get(position).packageName);
                            checkBox.setChecked(true);

                        }
                        listMemoryAdapter.confirmChanges();
                        ((MainActivity) getActivity()).startWidget();
                    }

                    @Override public void onLongItemClick(View view, int position) {

                    }
                })
        );

        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        changeDataInRecyclerView(filter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

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

    void changeDataInRecyclerView(String filter) {
        this.filter = filter;
        new MyAsyncTask().execute();
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

    //ADAPTER FOR RECYCLERVIEW
    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_all, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @SuppressLint("StaticFieldLeak")
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            new AsyncTask<Void, Void, Drawable>() {
                @Override
                protected void onPostExecute(Drawable drawable) {
                    super.onPostExecute(drawable);
                    if(drawable!=null){
                        holder.imageView.setImageDrawable(drawable);
                  //      holder.imageView.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.show_icon_in_recycler_view));
                    }else{
                        holder.imageView.setImageDrawable(getResources().getDrawable(R.drawable.android));
                    }

                }
                @Override
                protected Drawable doInBackground(Void... voids) {
                    try {
                        return manager.getActivityIcon(manager.getLaunchIntentForPackage(filteredPackageInfoList.get(position).packageName));
                    } catch (PackageManager.NameNotFoundException | IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            try {
                holder.textView.setText((position + 1) + ". " + filteredPackageInfoList.get(position).applicationInfo.loadLabel(manager).toString());
                holder.checkBox.setChecked(listMemoryAdapter.checkIfExist(filteredPackageInfoList.get(position).packageName));
        //        holder.checkBox.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.show_item_in_recycler_view));
        //        holder.textView.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.show_item_in_recycler_view));
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return filteredPackageInfoList.size();
        }

    }

    private class MyAsyncTask extends AsyncTask<Void,Void,Void>{

        List<PackageInfo> newList;
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }


        @Override
        protected Void doInBackground(Void... voids) {
            newList= new ArrayList<>();
            for (PackageInfo packageInfo: MainActivity.packageInfos ) {
                if (packageInfo.applicationInfo.loadLabel(manager).toString().toLowerCase().contains(filter.toLowerCase())) {
                    newList.add(packageInfo);
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute( Void aVoid) {
            super.onPostExecute(aVoid);
            synchronized (filteredPackageInfoList){
                filteredPackageInfoList=newList;
            }
            adapter.notifyDataSetChanged();

        }

    }

}
