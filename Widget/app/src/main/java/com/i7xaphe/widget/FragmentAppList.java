package com.i7xaphe.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class FragmentAppList extends Fragment {

    CustomRecyclerView recyclerView;

    PackageManager manager;
    MyFileReader listMemoryAdapter;



    MyAdapter mAdapter;

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
        listMemoryAdapter = new MyFileReader(file);
        manager = getContext().getPackageManager();
        recyclerView = (CustomRecyclerView) v.findViewById(R.id.recylerview);




        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()) ;

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        CheckBox checkBox =view.findViewById(R.id.ch_card);
                        if (checkBox.isChecked()) {
                            checkBox.setChecked(false);
                            listMemoryAdapter.removeLine((String)checkBox.getTag());
                        } else {
                            checkBox.setChecked(true);
                            listMemoryAdapter.addToList((String)checkBox.getTag());

                        }
                        listMemoryAdapter.confirmChanges();
                        ((MainActivity) getActivity()).restartWidget();
                    }

                    @Override public void onItemLongClick(View view, final int position) {
                        Utils.openApp(getContext(),(String) view.findViewById(R.id.ch_card).getTag());
                    }
                })
        );

        mAdapter = new MyAdapter(getContext(), new Comparator<ItemAppListModel>() {
            @Override
            public int compare(ItemAppListModel a, ItemAppListModel b) {
                return a.getAppName().compareTo(b.getAppName());
            }
        });
        mAdapter.replaceAll(MainActivity.itemAppListModelList);
        recyclerView.setAdapter(mAdapter);
        Log.e("qdad","aaaaaaaaa");
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }



    private static List<ItemAppListModel> filter(List<ItemAppListModel> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<ItemAppListModel> filteredModelList = new ArrayList<>();
        for (ItemAppListModel model : models) {
            final String text = model.getAppName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    void setDataInRecyclerView(String query) {
        final List<ItemAppListModel> filteredModelList = filter(MainActivity.itemAppListModelList, query);
        mAdapter.replaceAll(filteredModelList);
        recyclerView.scrollToPosition(0);


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

        private final SortedList<ItemAppListModel> mSortedList = new SortedList<>(ItemAppListModel.class, new SortedList.Callback<ItemAppListModel>() {
            @Override
            public int compare(ItemAppListModel a, ItemAppListModel b) {
                return mComparator.compare(a, b);
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(ItemAppListModel oldItem, ItemAppListModel newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(ItemAppListModel item1, ItemAppListModel item2) {
                return item1 == item2;
            }
        });

        private final Comparator<ItemAppListModel> mComparator;
        private final LayoutInflater mInflater;

        public MyAdapter(Context context, Comparator<ItemAppListModel> comparator) {
            mInflater = LayoutInflater.from(context);
            mComparator = comparator;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_all, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;

        }

        @SuppressLint("StaticFieldLeak")
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final ItemAppListModel model = mSortedList.get(position);

            holder.imageView.setImageDrawable(model.getAppIcon());
            holder.textView.setText(model.getAppName());
            holder.checkBox.setChecked(listMemoryAdapter.checkIfExist(model.getAppPacked()));
            holder.checkBox.setTag(model.getAppPacked());

        }

        public void add(ItemAppListModel model) {
            mSortedList.add(model);
        }

        public void remove(ItemAppListModel model) {
            mSortedList.remove(model);
        }

        public void add(List<ItemAppListModel> models) {
            mSortedList.addAll(models);
        }

        public void remove(List<ItemAppListModel> models) {
            mSortedList.beginBatchedUpdates();
            for (ItemAppListModel model : models) {
                mSortedList.remove(model);
            }
            mSortedList.endBatchedUpdates();
        }

        public void replaceAll(List<ItemAppListModel> models) {
            mSortedList.beginBatchedUpdates();
            for (int i = mSortedList.size() - 1; i >= 0; i--) {
                final ItemAppListModel model = mSortedList.get(i);
                if (!models.contains(model)) {
                    mSortedList.remove(model);
                }
            }
            mSortedList.addAll(models);
            mSortedList.endBatchedUpdates();
        }

        @Override
        public int getItemCount() {
            return mSortedList.size();
        }

    }
}
