package com.i7xaphe.widget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DialogBrowserWithIconRecycleview.dialogBroswerCallbacks, SearchView.OnQueryTextListener {

    SharedPreferences sheredpreferences;
    SharedPreferences.Editor editor;
    public static String sharePref = "com.i7xaphe.widget";
    public static String fileFULL = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "widget" + File.separator + "file.txt";
    //static List<ResolveInfo> appInfos;
    static List<ItemAppListModel> itemAppListModelList;
    ProgressDialog dialog;
    FragmentAppList fragmentAppList;
    FragmentSettings fragmentSettings;
    FragmentDragAndDrop fragmentDragAndDrop;
    SearchView searchView;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }else{
            showProgressDialog();
        }

        sheredpreferences = getSharedPreferences(sharePref, Context.MODE_PRIVATE);
        editor = sheredpreferences.edit();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setTag(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer ,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
                onDrawerClick((int)drawer.getTag());
            }
        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fragmentAppList = new FragmentAppList();
        fragmentSettings = new FragmentSettings();
        fragmentDragAndDrop = new FragmentDragAndDrop();

        editor.putInt("ID", getTaskId());
        editor.apply();



    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        editor.putInt("ID", 0);
        editor.commit();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {

        drawer.setTag(item.getItemId());
        drawer.closeDrawers();


        return true;
    }

    void restartWidget() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {

                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1);
                return;

            }else{
                stopService(new Intent(getBaseContext(), Widget.class));
                startService(new Intent(getBaseContext(), Widget.class));
            }
        }
    }

    @Override
    public void onFileSelect(String fileDir) {

        if ((fileDir).endsWith(".png") || (fileDir).endsWith(".gif") || (fileDir).endsWith(".jpg") || (fileDir).endsWith(".bmp") ||
                (fileDir).endsWith(".PNG") || (fileDir).endsWith(".GIF") || (fileDir).endsWith(".JPG") || (fileDir).endsWith(".BMP")) {
            editor.putString("pngFile", fileDir);
            editor.putInt("widgetColor", 0);
            editor.commit();
            //     fragmentSettings.restartSpinnerWidget();
            restartWidget();
        } else {
            Toast.makeText(getBaseContext(), "it's not png, jpg, bmp or gif file", Toast.LENGTH_LONG).show();
        }


    }

    void openBrowserDialogRecycleView() {
        try {
            DialogBrowserWithIconRecycleview dialogBrowser = new DialogBrowserWithIconRecycleview(this);
            dialogBrowser.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationUpDown;
            dialogBrowser.setCallbacks(this);
            dialogBrowser.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {

        fragmentAppList.setDataInRecyclerView(newText);
        Log.i("onQueryTextChange", "onQueryTextChange");

        return false;
    }



    @SuppressLint("StaticFieldLeak")
    void showProgressDialog() {

        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.load_apps));
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    loadAppListFragment();
                    dialog.hide();
                    dialog.cancel();
                    restartWidget();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }

            @Override
            protected Void doInBackground(Void... voids) {
                Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                final PackageManager manager = getApplicationContext().getPackageManager();


                List<ResolveInfo> appInfos = getApplicationContext().getPackageManager().queryIntentActivities( mainIntent, 0);

//                Collections.sort(appInfos, new Comparator<ResolveInfo>() {
//                    @Override
//                    public int compare(ResolveInfo a, ResolveInfo b) {
//                        return a.loadLabel(manager).toString().toUpperCase().compareTo(b.loadLabel(manager).toString().toUpperCase());
//                    }
//                });

                itemAppListModelList=new ArrayList<>();
                for(int i = 0; i<appInfos.size(); i++){
                    itemAppListModelList.add(new ItemAppListModel(i,getApplicationContext(),appInfos.get(i),manager));
                }

                return null;
            }
        }.execute();

    }




    void onDrawerClick(int code){
        if (code== R.id.applist) {
            loadAppListFragment();
        } else if (code == R.id.selected_apps) {
           loadDragAndDropFragment();
        } else if (code== R.id.settings) {
            loadSettingsFragment();
        } else if (code == R.id.widget) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1);
                } else {
                    MenuItem menuItem = ((NavigationView) findViewById(R.id.nav_view)).getMenu().findItem(R.id.widget);
                    if (!stopService(new Intent(getBaseContext(), Widget.class))) {
                        startService(new Intent(getBaseContext(), Widget.class));
                        menuItem.setTitle(R.string.close_widget);
                    } else {
                        menuItem.setTitle(R.string.open_widget);
                    }
                }
            }

        } else if (code== R.id.close_app) {

            finish();
        } else if (code == R.id.test_activity) {

            openBrowserDialogRecycleView();

        } else if (code == R.id.about) {
            Snackbar.make(findViewById(android.R.id.content).getRootView(), R.string.version_info, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }


    }
    void loadAppListFragment(){

        if (!fragmentAppList.isResumed()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                     .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                 //   .setCustomAnimations(R.anim.show_fragment, R.anim.hide_fragment)
                    .replace(R.id.root_frame, fragmentAppList)
                    .addToBackStack("fragmentAppList")
                    .commit();
            searchView.setVisibility(View.VISIBLE);
        }

    }
    void loadDragAndDropFragment(){

        if (!fragmentDragAndDrop.isResumed()) {
            fragmentDragAndDrop = new FragmentDragAndDrop();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                  //  .setCustomAnimations(R.anim.show_fragment, R.anim.hide_fragment)
                    .replace(R.id.root_frame, fragmentDragAndDrop)
                    .addToBackStack("fragmentDragAndDrop")
                    .commit();
            searchView.setVisibility(View.GONE);
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }
    }

    void loadSettingsFragment(){

        if (!fragmentSettings.isResumed()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                //    .setCustomAnimations(R.anim.show_fragment, R.anim.hide_fragment)
                    .replace(R.id.root_frame, fragmentSettings)
                    .addToBackStack("fragmentSettings")
                    .commit();
            searchView.setVisibility(View.GONE);

            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            //WRITE_EXTERNAL_STORAGE
            case 1: {
                Log.e("onRequestPermissions","onRequestPermissionsResult");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showProgressDialog();
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to write your storage", Toast.LENGTH_SHORT).show();
                    this.finish();
                    System.exit(0);
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("onActivityResult","onActivityResult");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                restartWidget();
                MenuItem menuItem = ((NavigationView) findViewById(R.id.nav_view)).getMenu().findItem(R.id.widget);
                menuItem.setTitle(R.string.close_widget);
            } else {
                MenuItem menuItem = ((NavigationView) findViewById(R.id.nav_view)).getMenu().findItem(R.id.widget);
                menuItem.setTitle(R.string.open_widget);
            }
        }
    }

}
