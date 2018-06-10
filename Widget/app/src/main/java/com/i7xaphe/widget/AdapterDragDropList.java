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


public class AdapterDragDropList extends DragItemAdapter<Pair<Long, String>, AdapterDragDropList.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    Context context;
    MyFileReader myFileReaderAdapter;
    PackageManager manager;
    ArrayList<Pair<Long, String>> mItemArray;

    public AdapterDragDropList(Context context, int layoutId, int grabHandleId, boolean dragOnLongPress,MyFileReader myFileReaderAdapter) {
        super(dragOnLongPress);


        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        setHasStableIds(true);
        this.myFileReaderAdapter = myFileReaderAdapter;
        this.context = context;
        manager = context.getPackageManager();
        mItemArray = new ArrayList<>();
        for (int i = 0; i < myFileReaderAdapter.getListSize(); i++) {
            mItemArray.add(new Pair<>(Long.valueOf(i), myFileReaderAdapter.getElement(i)));
        }

        setItemList(mItemArray);


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

        if (loaderListCaunter <myFileReaderAdapter.getListSize()) {
            loaderListCaunter++;
            try {
                holder.mText.setText(manager.getPackageInfo(myFileReaderAdapter.getElement(position), 0).applicationInfo.loadLabel(manager).toString());
            } catch (PackageManager.NameNotFoundException e) {
                holder.mText.setText("EMPTY");
            } catch (NullPointerException e) {
                holder.mText.setText("EMPTY");
            }
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        holder.mImageView.setImageDrawable(manager.getActivityIcon(manager.getLaunchIntentForPackage(myFileReaderAdapter.getElement(position))));
                    } catch (PackageManager.NameNotFoundException e) {
                        holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.mono_point));
                    } catch (NullPointerException e) {
                        holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.mono_point));
                    }
                }
            });
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

        public ViewHolder(final View itemView) {

            super(itemView, mGrabHandleId);
            mText = (TextView) itemView.findViewById(R.id.text);
            mImageView = (ImageView) itemView.findViewById(R.id.image);

        }
    }
}
