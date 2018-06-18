package com.i7xaphe.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Kamil on 2016-08-23.
 */
public class DialogBrowserWithIconRecycleview extends Dialog  {

    private String pathFull;
    List<String> historyPath;
    List<Integer> historyPosition;
    RecyclerView recyclerView;
    private dialogBroswerCallbacks dialogBroswerCallbacks;
    ImageLoader imageLoader;

    TextView tvPath;

    LinearLayoutManager linearLayoutManager;

    interface dialogBroswerCallbacks {
        void onFileSelect(String fileDir);
    }

    public void setCallbacks(dialogBroswerCallbacks callbacks) {
        dialogBroswerCallbacks = callbacks;
    }


    public DialogBrowserWithIconRecycleview(Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setContentView(R.layout.dialog_browser_recyclewiew);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.8));

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));

        historyPath = new ArrayList<>();
        historyPosition = new ArrayList<>();
        pathFull = Environment.getExternalStorageDirectory().getPath();
        Log.i("pathFull", "path length" + pathFull.length());
        tvPath = (TextView) findViewById(R.id.tv_path);
        tvPath.setText("Root" + pathFull.substring(19));

        recyclerView = (RecyclerView) findViewById(R.id.list);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        setMyAdapter(pathFull, 0);
        recyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        Button bClose = (Button) findViewById(R.id.b_close);
        Button bPrevious = (Button) findViewById(R.id.b_previous);

        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        bPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //        hide();
                if (!historyPath.isEmpty()) {
                    pathFull = historyPath.get(historyPath.size() - 1);
                    tvPath.setText("Root" + pathFull.substring(19));
                    setMyAdapter(historyPath.get(historyPath.size() - 1), historyPosition.get(historyPosition.size() - 1));
                    historyPath.remove(historyPath.size() - 1);
                    historyPosition.remove(historyPosition.size() - 1);
                }
            }
        });


    }



    void setMyAdapter(String filePath, int position) {
        List<String> values = new ArrayList();
        File dir = new File(filePath);
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    values.add(file);
                }
            }
        }
        Collections.sort(values);
        CustomAdapter customAdapter = new CustomAdapter(getContext(), R.layout.item_dialog_broswer_with_icon, values);
        linearLayoutManager.scrollToPosition(position);
        recyclerView.setAdapter(customAdapter);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView img;
        Button button;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.textView1);
            button = (Button) itemView.findViewById(R.id.image_button);
            img = (ImageView) itemView.findViewById(R.id.imageView1);
        }
    }

    public class CustomAdapter extends RecyclerView.Adapter<MyViewHolder> {
        List<String> list;
        int resource;
        Context context;
        View lastView;
        View v;

        public CustomAdapter(Context context, int resource, final List list) {
            this.resource = resource;
            this.list = list;
            this.context = context;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            v = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int itemPosition = recyclerView.getChildPosition(v);
                    if (new File(pathFull + File.separator + list.get(itemPosition)).isDirectory()) {
                        historyPath.add(pathFull);
                        historyPosition.add(itemPosition);
                        pathFull = pathFull + File.separator + list.get(itemPosition);
                        tvPath.setText("Root" + pathFull.substring(19));
                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.button_press_fast_fast);
                        v.startAnimation(animation);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                setMyAdapter(pathFull, 0);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    } else {
                        if (lastView != null) {
                            lastView.setBackgroundColor(Color.TRANSPARENT);
                        }
                        v.setBackgroundColor(new Color().argb(0x73, 0xcf, 0xcf, 0xcf));
                        lastView = v;
                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.button_press_fast_fast);
                        v.startAnimation(animation);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                dialogBroswerCallbacks.onFileSelect(pathFull + File.separator + list.get(itemPosition));
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }
            });
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.tv.setText(list.get(position));

            if (new File(pathFull + File.separator + list.get(position)).isDirectory()) {
                holder.img.setImageResource(R.drawable.folderandroid);
            } else if ((list.get(position)).endsWith(".bmp") || (list.get(position)).endsWith(".BMP") ||
                    (list.get(position)).endsWith(".png") || (list.get(position)).endsWith(".PNG") ||
                    (list.get(position)).endsWith(".gif") || (list.get(position)).endsWith(".GIF") ||
                    (list.get(position)).endsWith(".jpg") || (list.get(position)).endsWith(".JPG")) {
                Log.e("imageLoader", "file://" + pathFull + File.separator + list.get(position));
                ImageSize targetSize = new ImageSize(64, 64);
                imageLoader.displayImage("file://" + pathFull + File.separator + list.get(position), holder.img,targetSize);

            } else {
              //  final Drawable icon = match.loadIcon(getPackageManager());
                holder.img.setImageResource(R.drawable.otherfile);
            }

            holder.button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Delete " + list.get(position) + "?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    File file = new File(pathFull + File.separator + list.get(position));
                                    if (file.delete()) {
                                        Animation animation = AnimationUtils.loadAnimation(context, R.anim.delete_anim);
                                        holder.button.startAnimation(animation);
                                        holder.img.startAnimation(animation);
                                        holder.tv.startAnimation(animation);
                                        Toast.makeText(getContext(), list.get(position) + " was deleted", Toast.LENGTH_SHORT).show();
                                        list.remove(position);
                                        new android.os.Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                notifyDataSetChanged();
                                            }
                                        }, 300);
                                    } else {
                                        Toast.makeText(getContext(), "Cannot delete " + list.get(position), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("No", new OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                }
            });


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
}
