package com.i7xaphe.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 2016-08-23.
 */
public class DialogListApps_Adapter extends Dialog implements SearchView.OnQueryTextListener{

    RecyclerView recyclerView;
    private dialogListAPPCallbacks dialogListAPPCallbacks;
    LinearLayoutManager linearLayoutManager;
    int listTXTAdapterPosition;
    SearchView searchView;
    String filter;
    List<PackageInfo> myPackageInfo;
    TextView tv_title;

    public void setListTXTAdapterPosition(int listTXTAdapterPosition) {
        this.listTXTAdapterPosition = listTXTAdapterPosition;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                setNewAdapter(newText);
            }
        });
        return false;
    }


    interface dialogListAPPCallbacks {
        void dialogListAppCallbacks(String packedName,int position);
    }

    public void setCallbacks(dialogListAPPCallbacks callbacks) {
        dialogListAPPCallbacks = callbacks;
    }
    public DialogListApps_Adapter(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setContentView(R.layout.dialog_list_apps);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.8));

        recyclerView = (RecyclerView) findViewById(R.id.list);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        searchView = (SearchView) findViewById(R.id.action_search);
        searchView.setOnQueryTextListener(this);
        tv_title=(TextView)findViewById(R.id.title);
        searchView.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tv_title.setTextColor(Color.TRANSPARENT);

            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                tv_title.setTextColor(Color.BLACK);
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        myPackageInfo = new ArrayList<>(MainActivity.getPackageInfos());
        MyFileManager myFileManager = new MyFileManager(new File((MainActivity.fileFULL)));

        for(int i=0;i<myPackageInfo.size();i++){
            Log.i("all ", ""+myPackageInfo.get(i).packageName);
            for(int k=0;k<MainActivity.widgetIconsLimit;k++){
                if(myPackageInfo.get(i).packageName.equals(myFileManager.getElement(k))){
                    Log.i("remowe",""+myPackageInfo.remove(i)) ;
                    i--;
                    break;
                }
            }
        }



        CustomAdapter customAdapter = new CustomAdapter(getContext(), R.layout.item_list_dialog_app,myPackageInfo);
        recyclerView.setAdapter(customAdapter);

        recyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        });

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_card);
            img = (ImageView) itemView.findViewById(R.id.iv_card);
        }
    }
    public class CustomAdapter extends RecyclerView.Adapter<MyViewHolder> {
        List<PackageInfo> list;
        int resource;
        Context context;
        View v;
        PackageManager manager = getContext().getPackageManager();
        public CustomAdapter(Context context, int resource, final List list) {
            this.resource = resource;
            this.list = list;
            this.context = context;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            v = LayoutInflater.from(parent.getContext()).inflate(resource,parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.tv.setText((position + 1) + ". " + list.get(position).applicationInfo.loadLabel(manager).toString());
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("click","click");
                    MyFileManager myFileManager = new MyFileManager(new File((MainActivity.fileFULL)));
                    myFileManager.setElement(listTXTAdapterPosition,list.get(position).packageName);
                    myFileManager.confirmChanges();
                    cancel();
                    dialogListAPPCallbacks.dialogListAppCallbacks(list.get(position).packageName,listTXTAdapterPosition);
                    onCloseWindow();

                }
            });
            try {
                holder.img.setImageDrawable(manager.getActivityIcon(manager.getLaunchIntentForPackage(list.get(position).packageName)));
                holder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("click","click");
                        MyFileManager myFileManager = new MyFileManager(new File((MainActivity.fileFULL)));
                        myFileManager.setElement(listTXTAdapterPosition,list.get(position).packageName);
                        myFileManager.confirmChanges();
                        dialogListAPPCallbacks.dialogListAppCallbacks(list.get(position).packageName,listTXTAdapterPosition);
                        onCloseWindow();

                    }
                });
            } catch (PackageManager.NameNotFoundException e) {
                holder.img.setImageResource(R.drawable.folderandroid);

            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        onCloseWindow();
    }

    void setNewAdapter(String filter){
        this.filter=filter;
        List<PackageInfo> filteredList = new ArrayList<>();
//        if(this.filter.equals("")){
//            CustomAdapter Adapter = new CustomAdapter(getContext(), R.layout.item_list_dialog_app,myPackageInfo);
//            recyclerView.setAdapter(Adapter);
//            return;
//        }
        PackageManager manager = getContext().getPackageManager();
        for (PackageInfo packageInfo: MainActivity.packageInfos ) {
            if (packageInfo.applicationInfo.loadLabel(manager).toString().toLowerCase().contains(filter.toLowerCase())) {
                filteredList.add(packageInfo);
            }
        }
        if(filteredList.size()==0){
            recyclerView.setAdapter(null);
            return;
        }
        CustomAdapter Adapter = new CustomAdapter(getContext(), R.layout.item_list_dialog_app,filteredList);
        recyclerView.setAdapter(Adapter);
    }
    void onCloseWindow(){
        tv_title.setTextColor(Color.BLACK);
        if (!searchView.isIconified()) {
            searchView.onActionViewCollapsed();
            tv_title.setTextColor(Color.BLACK);
        }
        cancel();
    }
}
