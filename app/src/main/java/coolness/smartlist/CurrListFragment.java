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

public class CurrListFragment extends Fragment {

    private Button addItem;
    private EditText newItem;
    private String newItemName = "";
    private Button doneShopping;
    private TextView listNameView;
    private EditText listNameEdit;
    private Button listNameCheck;
    private Button listNameCancel;
    private Button deleteList;
    private Button listsButton;
    private Button newItemCheck;
    private Button newItemCancel;
    private Spinner switchLists;
    private RecyclerView groceryList;
    private GroceryAdapter groceryAdapter;
    private ArrayAdapter<String> spinnerAdapter;
    private View lineView;
    private boolean justEditedName = false;

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
        newItemCheck = view.findViewById(R.id.newItemCheck);
        newItemCancel = view.findViewById(R.id.newItemCancel);
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
                    addItem.setVisibility(View.GONE);
                    newItem.setVisibility(View.VISIBLE);
                    newItemCancel.setVisibility(View.VISIBLE);
                    newItemCheck.setVisibility(View.VISIBLE);
                    newItem.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        });
        newItemCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemAdded();
            }
        });
        newItemCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newItem.setVisibility(View.GONE);
                newItemCheck.setVisibility(View.GONE);
                newItemCancel.setVisibility(View.GONE);
                addItem.setVisibility(View.VISIBLE);
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
        lineView = view.findViewById(R.id.lineView);
        switchLists = view.findViewById(R.id.switchLists);
        updateLists();
        switchLists.setSelection(0);
        switchLists.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!justEditedName) {
                    String switchName = adapterView.getItemAtPosition(i).toString();
                    if (!switchName.equals("add new list")) {
                        Settings.setCurrentList(switchName);
                        groceryAdapter.notifyDataSetChanged();
                    } else {
                        justEditedName = true;
                        NameListDialog cd = new NameListDialog();
                        //cd.setCancelable(false);
                        cd.show(getActivity().getSupportFragmentManager(), "example");
                    }
                } else { justEditedName = false; }
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
        listNameCancel = view.findViewById(R.id.listNameCancel);
        listNameCheck = view.findViewById(R.id.listNameCheck);
        listNameEdit = view.findViewById(R.id.listNameEdit);
        listNameView = view.findViewById(R.id.listNameView);
        deleteList = view.findViewById(R.id.listDelete);
        listNameView.setText(Settings.getCurrentListName());
        listNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listNameView.setVisibility(View.INVISIBLE);
                deleteList.setVisibility(View.INVISIBLE);
                addItem.setVisibility(View.INVISIBLE);
                lineView.setVisibility(View.INVISIBLE);
                listNameEdit.setVisibility(View.VISIBLE);
                listNameEdit.setText(Settings.getCurrentListName());
                listNameCancel.setVisibility(View.VISIBLE);
                listNameCheck.setVisibility(View.VISIBLE);
            }
        });
        listNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.getId() == R.id.itemEdit && !b) {
                    listNameEdit.setVisibility(View.GONE);
                    listNameCheck.setVisibility(View.GONE);
                    listNameCancel.setVisibility(View.GONE);
                    lineView.setVisibility(View.VISIBLE);
                    listNameView.setVisibility(View.VISIBLE);
                    deleteList.setVisibility(View.VISIBLE);
                    addItem.setVisibility(View.VISIBLE);
                }
            }
        });
        listNameCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListNameChanged();
            }
        });
        listNameCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listNameEdit.setVisibility(View.GONE);
                listNameCheck.setVisibility(View.GONE);
                listNameCancel.setVisibility(View.GONE);
                listNameView.setVisibility(View.VISIBLE);
                deleteList.setVisibility(View.VISIBLE);
                addItem.setVisibility(View.VISIBLE);
                lineView.setVisibility(View.VISIBLE);
            }
        });
        deleteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Settings.deleteCurrentList(getActivity());
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
        listNameView.setText(listName);
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
            newItemCheck.setVisibility(View.GONE);
            newItemCancel.setVisibility(View.GONE);
            addItem.setVisibility(View.VISIBLE);
            hideKeyboard();
            Settings.writeCurrentLists(CurrListFragment.this.getActivity());
        }
        return handled;
    }

    public void onListNameChanged() {
        String name = listNameEdit.getText().toString();
        if (name.equals("")) {
            Toast.makeText(CurrListFragment.this.getContext(), "Please enter a list name", Toast.LENGTH_SHORT).show();
        } else if (Settings.alreadyUsedListName(name)) {
            Toast.makeText(CurrListFragment.this.getContext(), "You already have a list named that", Toast.LENGTH_SHORT).show();
        } else {
            justEditedName = true;
            String newName = listNameEdit.getText().toString();
            Settings.setCurrentListName(newName, getActivity());
            listNameEdit.setVisibility(View.GONE);
            listNameCheck.setVisibility(View.GONE);
            listNameCancel.setVisibility(View.GONE);
            listNameView.setVisibility(View.VISIBLE);
            listNameView.setText(newName);
            deleteList.setVisibility(View.VISIBLE);
            addItem.setVisibility(View.VISIBLE);
            lineView.setVisibility(View.VISIBLE);
        }
    }
}
