package coolness.smartlist;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.Date;

import coolness.smartlist.fragment.CurrListFragment;
import coolness.smartlist.fragment.HelpFragment;
import coolness.smartlist.fragment.ItemsFragment;
import coolness.smartlist.fragment.SettingsFragment;
import coolness.smartlist.fragment.SuggestionFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!ListManager.isInitialized()) {
            ListManager.initialize(this);
        }
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        Fragment fragment;
                        switch (menuItem.getTitle().toString()) {
                            case "Previous Items":
                                fragment = new ItemsFragment(); break;
                            case "ListManager":
                                fragment = new SettingsFragment(); break;
                            case "Help":
                                fragment = new HelpFragment(); break;
                            default:
                                fragment = new CurrListFragment(); break;
                        }
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                        ft.commit();
                        return true;
                    }
                });
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        Fragment fragment;
        if (ListManager.getSuggestions(new Date().getTime()) != null) {
            fragment = new SuggestionFragment();
        } else {
            fragment = new CurrListFragment();
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.content_frame, fragment);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
