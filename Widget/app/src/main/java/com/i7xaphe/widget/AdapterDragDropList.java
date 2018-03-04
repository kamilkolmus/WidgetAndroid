package com.i7xaphe.widget; /**
 * Copyright 2014 Magnus Woxblom
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Handler;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItemAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;


public class AdapterDragDropList extends DragItemAdapter<Pair<Long, String>, AdapterDragDropList.ViewHolder> implements DialogListApps_Adapter.dialogListAPPCallbacks{

    private int mLayoutId;
    private int mGrabHandleId;
    Context context;
    MyFileManager myFileManagerAdapter;
    PackageManager manager;
    ArrayList<Pair<Long, String>> mItemArray;


    DialogListApps_Adapter dialogListAppsAdapter;

    @Override
    public void dialogListAppCallbacks(String packedName, int position) {
        myFileManagerAdapter.setElement(position,packedName);
        mItemArray.set(position,new Pair<>(Long.valueOf(new Random().nextInt(99999999) + 100), packedName));
        Log.i("kk",packedName);
        loaderListCaunter=0;
        setItemList(mItemArray);
        notifyDataSetChanged();
        adapterCallbacks.restartWidget();
    }

    private itemAdapterCallBacks adapterCallbacks;

    interface itemAdapterCallBacks {
        void restartWidget();
    }

    public void setCallbacks(itemAdapterCallBacks callbacks) {
        adapterCallbacks = callbacks;
    }

    public AdapterDragDropList(Context context, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        super(dragOnLongPress);


        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        setHasStableIds(true);
        myFileManagerAdapter = new MyFileManager(new File(MainActivity.fileFULL));
        myFileManagerAdapter =new MyFileManager(new File(MainActivity.fileFULL));
        if(myFileManagerAdapter.getListSize()!=MainActivity.widgetIconsLimit){
            if(myFileManagerAdapter.getListSize()<MainActivity.widgetIconsLimit){
                myFileManagerAdapter.fillList(MainActivity.EmptyLine,MainActivity.widgetIconsLimit);
            }
            else{
                while(myFileManagerAdapter.getListSize()>MainActivity.widgetIconsLimit){
                    myFileManagerAdapter.removeLine(myFileManagerAdapter.getListSize()-1);
                }
            }

        }
        this.context = context;
        manager = context.getPackageManager();
        mItemArray = new ArrayList<>();
        for (int i = 0; i < myFileManagerAdapter.getListSize(); i++) {
            mItemArray.add(new Pair<>(Long.valueOf(i), myFileManagerAdapter.getElement(i)));
        }

        setItemList(mItemArray);

        dialogListAppsAdapter = new DialogListApps_Adapter(context);
        dialogListAppsAdapter.setCallbacks(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        Log.i("onCreateViewHolder", "onCreateViewHolder" + viewType);
        return new ViewHolder(view);
    }

    int loaderListCaunter = 0;

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        if (loaderListCaunter < MainActivity.widgetIconsLimit) {
            loaderListCaunter++;
            try {
                holder.mText.setText(manager.getPackageInfo(myFileManagerAdapter.getElement(position), 0).applicationInfo.loadLabel(manager).toString());
                holder.mButton.setText("REMOVE");
            } catch (PackageManager.NameNotFoundException e) {
                holder.mText.setText("EMPTY");
                holder.mButton.setText("ADD");
            } catch (NullPointerException e) {
                holder.mText.setText("EMPTY");
                holder.mButton.setText("ADD");
            }
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        holder.mImageView.setImageDrawable(manager.getActivityIcon(manager.getLaunchIntentForPackage(myFileManagerAdapter.getElement(position))));
                    } catch (PackageManager.NameNotFoundException e) {
                        holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.mono_point));
                    } catch (NullPointerException e) {
                        holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.mono_point));
                    }
                }
            });
        }
        holder.mButton.setOnClickListener(new View.OnClickListener()
                                          {
                                              @Override
                                              public void onClick(View v) {

                                                  if(myFileManagerAdapter.getElement(position).equals(MainActivity.EmptyLine)){
                                                      Log.i("add","addd");
                                                      dialogListAppsAdapter.setListTXTAdapterPosition(position);
                                                      dialogListAppsAdapter.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationUpDown;
                                                      dialogListAppsAdapter.show();
                                                  }else {
                                                      myFileManagerAdapter.swapElement(mItemArray.get(position).second, "");
                                                      mItemArray.set(position, new Pair<>(Long.valueOf(new Random().nextInt(99999999) + 100), MainActivity.EmptyLine));
                                                         setItemList(mItemArray);
                                                      myFileManagerAdapter.confirmChanges();
                                                      adapterCallbacks.restartWidget();
                                                  //    notifyDataSetChanged();
                                                      loaderListCaunter = 0;
                                                  }
                                              }
                                          }
        );
        if(position==MainActivity.widgetIconsLimit-1){
            myFileManagerAdapter.clearList();
        for (int i = 0; i <MainActivity.widgetIconsLimit; i++)
        {
            myFileManagerAdapter.addToList(mItemList.get(i).second);
        }
            myFileManagerAdapter.confirmChanges();
            Log.i("confirmChanges", "confirmChanges" + position);
        }

        Log.i("onBindViewHolder", "onBindViewHolder" + position);
    }

    @Override
    public long getItemId(int position) {

        return mItemList.get(position).first;
    }

    public class ViewHolder extends DragItemAdapter<Pair<Long, String>, AdapterDragDropList.ViewHolder>.ViewHolder {
        public TextView mText;
        public ImageView mImageView;
        public Button mButton;

        public ViewHolder(final View itemView) {

            super(itemView, mGrabHandleId);
            mText = (TextView) itemView.findViewById(R.id.text);
            mImageView = (ImageView) itemView.findViewById(R.id.image);
            mButton = (Button) itemView.findViewById(R.id.button);

        }
    }
}
