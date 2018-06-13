package com.i7xaphe.widget;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DialogBrowserWithIconRecycleview.dialogBroswerCallbacks, SearchView.OnQueryTextListener {

    SharedPreferences sheredpreferences;
    SharedPreferences.Editor editor;
    public static String sharePref = "com.i7xaphe.widget";
    public static String fileFULL = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "widget" + File.separator + "file.txt";
    static List<PackageInfo> packageInfos;
    static List<Drawable> packageIcon;
    ProgressDialog dialog;
    FragmentAppList fragmentAppList;
    FragmentSettings fragmentSettings;
    FragmentDragAndDrop fragmentDragAndDrop;
    SearchView searchView;
    int globalId = 0;
    public final static int REQUEST_CODE = 10101;
    DrawerLayout drawer;
    private Menu menu;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //check nessesery permision

        sheredpreferences = getSharedPreferences(sharePref, Context.MODE_PRIVATE);
        editor = sheredpreferences.edit();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {

                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1);
                return;

            }

        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setTag(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
                globalId = (int) drawer.getTag();
                if (globalId == R.id.applist) {
                    if (!fragmentAppList.isResumed()) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.show_fragment, R.anim.hide_fragment)
                                .replace(R.id.root_frame, fragmentAppList)
                                .commit();
                        searchView.setVisibility(View.VISIBLE);
                    }

                } else if (globalId == R.id.selected_apps) {
                    if (!fragmentDragAndDrop.isResumed()) {
                        fragmentDragAndDrop = new FragmentDragAndDrop();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.show_fragment, R.anim.hide_fragment)
                                .replace(R.id.root_frame, fragmentDragAndDrop)
                                .commit();
                        searchView.setVisibility(View.GONE);
                        View view = getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                    }
                } else if (globalId == R.id.settings) {
                    if (!fragmentSettings.isResumed()) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.show_fragment, R.anim.hide_fragment)
                                .replace(R.id.root_frame, fragmentSettings)
                                .commit();
                        searchView.setVisibility(View.GONE);

                        View view = getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                    }
                } else if (globalId == R.id.widget) {
                    MenuItem menuItem = ((NavigationView) findViewById(R.id.nav_view)).getMenu().findItem(R.id.widget);
                    if (!stopService(new Intent(getBaseContext(), Widget.class))) {
                        startService(new Intent(getBaseContext(), Widget.class));
                        menuItem.setTitle(R.string.close_widget);
                    } else {
                        menuItem.setTitle(R.string.open_widget);
                    }

                } else if (globalId == R.id.close_app) {

                    finish();
                } else if (globalId == R.id.test_activity) {

                    openBrowserDialogRecycleView();

                } else if (globalId == R.id.about) {
                    Snackbar.make(findViewById(android.R.id.content).getRootView(), R.string.version_info, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }


            }
        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();


        fragmentAppList = new FragmentAppList();
        fragmentSettings = new FragmentSettings();
        fragmentDragAndDrop = new FragmentDragAndDrop();

        editor.putInt("ID", getTaskId());
        editor.apply();

        openProgressDialog();
        //check for necessary permission
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            closeProgressDialog();
        }


    }

    public static List<PackageInfo> getPackageInfos() {
        return packageInfos;
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
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        this.menu = menu;
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
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void startWidget() {

        stopService(new Intent(getBaseContext(), Widget.class));
        startService(new Intent(getBaseContext(), Widget.class));

    }

    @Override
    public void onFileSelect(String fileDir) {

        if ((fileDir).endsWith(".png") || (fileDir).endsWith(".gif") || (fileDir).endsWith(".jpg") || (fileDir).endsWith(".bmp") ||
                (fileDir).endsWith(".PNG") || (fileDir).endsWith(".GIF") || (fileDir).endsWith(".JPG") || (fileDir).endsWith(".BMP")) {
            editor.putString("pngFile", fileDir);
            editor.putInt("widgetColor", 0);
            editor.commit();
            //     fragmentSettings.restartSpinnerWidget();
            startWidget();
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
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                fragmentAppList.setDataInRecyclerView(newText);
                Log.i("onQueryTextChange", "onQueryTextChange");
            }
        });

        return false;
    }

    void closeProgressDialog() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PackageManager manager = getApplicationContext().getPackageManager();
                packageInfos = manager.getInstalledPackages(PackageManager.GET_ACTIVITIES);
                packageIcon = new ArrayList<>();
                for (int i = 0; i < packageInfos.size(); i++) {
                    try {
                        Drawable drawable;
                        if (manager.getActivityIcon(manager.getLaunchIntentForPackage(packageInfos.get(i).packageName)) == null) {
                        } else {
                            //         packageIcon.add(packageManager.getActivityIcon(packageManager.getLaunchIntentForPackage(packageInfos.get(i).packageName)));
                        }
                    } catch (NullPointerException e) {
                        packageInfos.remove(i);
                        i--;
                    } catch (PackageManager.NameNotFoundException e) {
                        packageInfos.remove(i);
                        i--;
                    }

                }
                Collections.sort(packageInfos, new Comparator<PackageInfo>() {
                    @Override
                    public int compare(PackageInfo lhs, PackageInfo rhs) {
                        PackageManager manager = getApplicationContext().getPackageManager();
                        return lhs.applicationInfo.loadLabel(manager).toString().compareTo(rhs.applicationInfo.loadLabel(manager).toString());
                    }
                });

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hide();
                        dialog.cancel();
                        try {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.show_fragment, R.anim.hide_fragment)
                                    .replace(R.id.root_frame, fragmentAppList)
                                    .addToBackStack("applist")
                                    .commit();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        startWidget();
                    }
                });
                Log.i("List Fragment", "on Create");
            }
        }).start();


    }

    void openProgressDialog() {
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.load_apps));
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            //WRITE_EXTERNAL_STORAGE
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //enable WRITE_EXTERNAL_STORAGE
                    closeProgressDialog();
                } else {
                    //disable WRITE_EXTERNAL_STORAGE
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(i);
            } else {
                finish();
                System.exit(0);
            }
        }

    }

}
