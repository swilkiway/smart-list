package coolness.smartlist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ListActivity extends AppCompatActivity {

    private Button addItem;
    private EditText newItem;
    private String newItemName = "";
    private Button doneShopping;
    private GroceryAdapter groceryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        RecyclerView groceryList = findViewById(R.id.groceryList);
        RecyclerView.LayoutManager groceryManager = new LinearLayoutManager(this);
        groceryList.setLayoutManager(groceryManager);
        if (Settings.getCurrentList() != null) {
            groceryAdapter = new GroceryAdapter(this, Settings.getCurrentList());
        } else {
            groceryAdapter = new GroceryAdapter(this);
        }
        groceryList.setAdapter(groceryAdapter);
        newItem = findViewById(R.id.newItem);
        newItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newItemName = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        newItem.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handled = onItemAdded();
                }
                return handled;
            }
        });
        addItem = findViewById(R.id.addItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newItem.getVisibility() == View.VISIBLE) {
                    onItemAdded();
                } else {
                    newItem.setVisibility(View.VISIBLE);
                    newItem.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        });
        doneShopping = findViewById(R.id.doneShopping);
        doneShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groceryAdapter.clearCheckedItems();
                doneShopping.setEnabled(false);
            }
        });
        if (groceryAdapter.anyItemsChecked()) {
            doneShopping.setEnabled(true);
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void anyItemsChecked(boolean checked) {
        if (checked) {
            doneShopping.setEnabled(true);
        } else {
            doneShopping.setEnabled(false);
        }
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

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean onItemAdded() {
        boolean handled = false;
        if (newItemName.equals("")) {
            Toast.makeText(ListActivity.this, "Please enter an item name", Toast.LENGTH_SHORT).show();
        } else if (Settings.listContains(newItemName)) {
            Toast.makeText(ListActivity.this, "That's already on your list", Toast.LENGTH_SHORT).show();
        } else {
            ListItem newGrocery = new ListItem(newItemName);
            groceryAdapter.addItem(newGrocery);
            newItem.setText("");
            handled = true;
            newItem.setVisibility(View.GONE);
            hideKeyboard();
            Settings.writeCurrentList(ListActivity.this);
        }
        return handled;
    }
}
