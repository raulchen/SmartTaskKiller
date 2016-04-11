package cn.ac.iscas.smarttaskmanager;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import cn.ac.iscas.smarttaskmanager.ui.KillingHistoryFragment;
import cn.ac.iscas.smarttaskmanager.ui.MemMonitorFragment;
import cn.ac.iscas.smarttaskmanager.ui.RunningAppsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private int selectedFragment;
    private Fragment[] fragments;
    private String[] tabTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabTitles = getResources().getStringArray(R.array.tab_titles);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragments = new Fragment[]{
                new RunningAppsFragment(),
                new KillingHistoryFragment(),
                new MemMonitorFragment()
        };
        selectedFragment = 2;
        updateFragment();

        Intent i = new Intent(this, MainService.class);
        startService(i);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        int index = 0;
        if (id == R.id.nav_running_apps) {
            index = 0;
        }
        else if (id == R.id.nav_killing_history) {
            index = 1;
        }
        else if (id == R.id.nav_mem_monitor) {
            index = 2;
        }

        if(index != selectedFragment) {
            selectedFragment = index;
            updateFragment();
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void updateFragment(){
        toolbar.setTitle(tabTitles[selectedFragment]);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragments[selectedFragment])
                .commit();
    }
}

