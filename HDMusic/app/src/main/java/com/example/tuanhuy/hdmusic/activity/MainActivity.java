package com.example.tuanhuy.hdmusic.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tuanhuy.hdmusic.callback.OnCloseNavigation;
import com.example.tuanhuy.hdmusic.R;
import com.example.tuanhuy.hdmusic.frament.BottomSheetFragment;
import com.example.tuanhuy.hdmusic.frament.LibraryFragment;
import com.example.tuanhuy.hdmusic.frament.SettingFragment;
import com.example.tuanhuy.hdmusic.utils.Constants;

public class MainActivity extends AppCompatActivity implements OnCloseNavigation {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToogle;
    private Toolbar mToolbar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissionApp();
        addFragment();
        addBottomSheet();
        configureNavigationDrawer();
        configureToolBar();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void addFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().add(R.id.Frame_Content, new LibraryFragment());
        transaction.commit();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissionApp() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);

            return;
        }
    }

    private void configureToolBar() {
        mToolbar = findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_navigation);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    private void configureNavigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawer);
        mToogle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToogle);
        mToogle.syncState();
        NavigationView nvDrawer = findViewById(R.id.navigation);
        setUpDrawerContent(nvDrawer);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToogle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectItemDrawer(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.library:
                fragmentClass = LibraryFragment.class;
                break;
            case R.id.settings:
                fragmentClass = SettingFragment.class;
                break;
            default:
                fragmentClass = LibraryFragment.class;
                break;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.Frame_Content, fragment).commit();
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    private void setUpDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectItemDrawer(menuItem);
                return false;
            }
        });
    }

    private void addBottomSheet() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.Frame_Bottom_Sheet, new BottomSheetFragment()).commit();
    }

    @Override
    public void OnClick(int type) {
        if (type == Constants.PAGER_DRAGGING) {
            mDrawerLayout.requestDisallowInterceptTouchEvent(true);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else if (type == Constants.PAGER_COLLAPSED) {
            mDrawerLayout.requestDisallowInterceptTouchEvent(false);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
}
