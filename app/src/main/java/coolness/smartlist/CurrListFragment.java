package coolness.smartlist;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CurrListFragment extends Fragment {

    private Button addItem;
    private EditText newItem;
    private String newItemName = "";
    private Button doneShopping;
    private TextView listNameView;
    private Button deleteList;
    private Button listsButton;
    private Spinner switchLists;
    private RecyclerView groceryList;
    private GroceryAdapter groceryAdapter;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        groceryList = view.findViewById(R.id.groceryList);
        RecyclerView.LayoutManager groceryManager = new LinearLayoutManager(getContext());
        groceryList.setLayoutManager(groceryManager);
        if (Settings.getCurrentList() != null) {
            groceryAdapter = new GroceryAdapter(this, Settings.getCurrentList());
        } else {
            groceryAdapter = new GroceryAdapter(this);
        }
        groceryList.setAdapter(groceryAdapter);
        newItem = view.findViewById(R.id.newItem);
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
        addItem = view.findViewById(R.id.addItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newItem.getVisibility() == View.VISIBLE) {
                    onItemAdded();
                } else {
                    newItem.setVisibility(View.VISIBLE);
                    newItem.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        });
        doneShopping = view.findViewById(R.id.doneShopping);
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
        switchLists = view.findViewById(R.id.switchLists);
        updateLists();
        switchLists.setSelection(0);
        switchLists.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String switchName = adapterView.getItemAtPosition(i).toString();
                if (!switchName.equals("add new list")) {
                    Settings.setCurrentList(switchName);
                    groceryAdapter.notifyDataSetChanged();
                } else {
                    NameListDialog cd = new NameListDialog();
                    //cd.setCancelable(false);
                    cd.show(getActivity().getSupportFragmentManager(), "example");
                }
                switchLists.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        listsButton = view.findViewById(R.id.listsButton);
        listsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchLists.performClick();
            }
        });
        Settings.initializeUpdateManager(this);
    }

    public void anyItemsChecked(boolean checked) {
        if (checked) {
            doneShopping.setEnabled(true);
        } else {
            doneShopping.setEnabled(false);
        }
    }

    public void setAddButtonVisibility(int visibility) {
        addItem.setVisibility(visibility);
    }

    public void updateLists() {
        spinnerAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, Settings.getCurrentListNames());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        switchLists.setAdapter(spinnerAdapter);
    }

    public void switchLists(String listName) {
        if (Settings.getCurrentList() != null) {
            groceryAdapter = new GroceryAdapter(this, Settings.getCurrentList());
        } else {
            groceryAdapter = new GroceryAdapter(this);
        }
        groceryList.setAdapter(groceryAdapter);
        int position = spinnerAdapter.getPosition(listName);
        switchLists.setSelection(position);
        doneShopping.setEnabled(groceryAdapter.anyItemsChecked());
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view == null) {
            view = new View(getContext());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean onItemAdded() {
        boolean handled = false;
        if (newItemName.equals("")) {
            Toast.makeText(CurrListFragment.this.getContext(), "Please enter an item name", Toast.LENGTH_SHORT).show();
        } else if (Settings.listContains(newItemName)) {
            Toast.makeText(CurrListFragment.this.getContext(), "That's already on your list", Toast.LENGTH_SHORT).show();
        } else {
            ListItem newGrocery = new ListItem(newItemName);
            groceryAdapter.addItem(newGrocery);
            newItem.setText("");
            handled = true;
            newItem.setVisibility(View.GONE);
            hideKeyboard();
            Settings.writeCurrentLists(CurrListFragment.this.getActivity());
        }
        return handled;
    }
}
