package com.i7xaphe.widget;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.google.android.gms.ads.MobileAds;
import com.woxthebox.draglistview.DragListView;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSelectedApps extends Fragment implements AdapterView.OnItemClickListener,AdapterDragDropList.itemAdapterCallBacks{
    DragListView mDragListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_drag_drop_list, container, false);
        mDragListView = (DragListView) v.findViewById(R.id.drag_list_view);

        MobileAds.initialize(getContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        return v;
    }

    @Override
    public void onStart() {

        mDragListView.setLayoutManager(new LinearLayoutManager(getContext()));
        final AdapterDragDropList listAdapter = new AdapterDragDropList(getContext(),R.layout.item_list_selected, R.id.layout, false);
        listAdapter.setCallbacks(this);
        mDragListView.setAdapter(listAdapter, true);
        mDragListView.setCanDragHorizontally(false);
        mDragListView.setDragListListener(new DragListView.DragListListener() {
            @Override
            public void onItemDragStarted(int position) {

            }
            @Override
            public void onItemDragging(int itemPosition, float x, float y) {

            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                if (fromPosition != toPosition) {
                    listAdapter.notifyDataSetChanged();
                    widgetRestrart();
                }
            }
        });


        super.onStart();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("onClick","onClick");
        Toast.makeText(getContext(),""+position,Toast.LENGTH_SHORT).show();
    }
     void  widgetRestrart() {
        ((MainActivity) getActivity()).startWidget();

    }

    @Override
    public void restartWidget() {
        widgetRestrart();
    }


}
