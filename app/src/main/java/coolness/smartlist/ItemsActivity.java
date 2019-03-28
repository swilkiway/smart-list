package coolness.smartlist;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

public class ItemsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        RecyclerView itemsList = findViewById(R.id.itemsList);
        RecyclerView.LayoutManager itemsManager = new LinearLayoutManager(this);
        itemsList.setLayoutManager(itemsManager);
        final ItemsAdapter itemsAdapter;
        if (Settings.getPreviousItems() != null) {
            itemsAdapter = new ItemsAdapter(this, Settings.getPreviousItems());
        } else {
            itemsAdapter = new ItemsAdapter(this);
        }
        itemsList.setAdapter(itemsAdapter);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Settings.closeProgram(this);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}
