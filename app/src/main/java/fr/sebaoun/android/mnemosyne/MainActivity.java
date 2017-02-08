package fr.sebaoun.android.mnemosyne;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import fr.sebaoun.android.mnemosyne.fragments.Tasks;
import fr.sebaoun.android.mnemosyne.fragments.Settings;
import io.realm.Realm;

/**
 * Created by sebaou_d on 16/01/17.
 */

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private FragmentManager fragmentManager = getFragmentManager();

    /**
     * Override of Activity onCreate method that initialises Realm, the AppBar, the left drawer
     * and puts a Tasks Fragment as main content of the Activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(onNavItemSelected);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, new Tasks())
                .commit();
    }

    /**
     * Override of Activity onBackPresses method that closes the left drawer if it is open
     * and calls the default onBackPresses method if not
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Override of Activity onCreateOptionsMenu method that sets the AppBar items as invisible
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_save).setVisible(false);
        menu.findItem(R.id.action_delete).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private NavigationView.OnNavigationItemSelectedListener onNavItemSelected =
            new NavigationView.OnNavigationItemSelectedListener() {
                /**
                 * NavigationView Listener that handles the selection of items in the left drawer
                 * ex: Tasks, Settings, Share
                 * @param item
                 * @return Fragment
                 */
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;
                    int id = item.getItemId();

                    if (id == R.id.nav_tasks) {
                        fragment = new Tasks();
                    } else if (id == R.id.nav_settings) {
                        fragment = new Settings();
                    } else if (id == R.id.nav_share) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg));
                        sendIntent.setType("text/plain");
                        Intent.createChooser(sendIntent,"Share via");
                        startActivity(sendIntent);
                    } else {
                        fragment = new Tasks();
                    }

                    if (fragment != null) {
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, fragment)
                                .commit();
                    }
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }
            };
}
