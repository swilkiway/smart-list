package coolness.smartlist.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import coolness.smartlist.ListManager;
import coolness.smartlist.model.ListItem;
import coolness.smartlist.R;
import coolness.smartlist.fragment.CurrListFragment;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.GroceryHolder> {
    private ArrayList<ListItem> groceries;
    private CurrListFragment fragment;
    public GroceryAdapter(CurrListFragment a) {
        fragment = a;
        groceries = new ArrayList<>();
    }
    public GroceryAdapter(CurrListFragment a, ArrayList<ListItem> g) {
        fragment = a;
        groceries = g;
    }
    @Override
    @NonNull
    public GroceryAdapter.GroceryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getActivity());
        return new GroceryHolder(inflater.inflate(R.layout.item_list, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull GroceryAdapter.GroceryHolder holder, int position) {
        holder.bind(groceries.get(position));
    }

    @Override
    public int getItemCount() {
        if (groceries != null) {
            return groceries.size();
        } else {
            return 0;
        }
    }

    private void removeItem(ListItem item) {
        ListManager.removeFromList(item, fragment.getActivity());
        notifyDataSetChanged();
    }

    public void addItem(ListItem item) {
        ListManager.addToList(item);
        notifyDataSetChanged();
    }

    public void clearCheckedItems() {
        ListManager.clearCheckedItems(fragment.getActivity());
        notifyDataSetChanged();
    }

    public boolean anyItemsChecked() {
        for (ListItem g : groceries) {
            if (g.isChecked()) {
                return true;
            }
        }
        return false;
    }

    private void setAddButtonVisibility(int visibility) {
        fragment.setAddButtonVisibility(visibility);
    }

    private void modifyItem(String oldName, String newName, Activity activity) {
        ListManager.modifyListItem(oldName, newName, activity);
        notifyDataSetChanged();
    }

    private void informActivityItemsChecked() {
        fragment.anyItemsChecked(anyItemsChecked());
    }

    class GroceryHolder extends RecyclerView.ViewHolder {
        private Button removeButton;
        private CheckBox boughtButton;
        private ListItem grocery;
        private TextView nameView;
        private EditText nameEdit;
        private Button editCheck;
        private Button editCancel;
        GroceryHolder(View view) {
            super(view);
            nameView = view.findViewById(R.id.itemName);
            nameEdit = view.findViewById(R.id.itemEdit);
            removeButton = view.findViewById(R.id.notButton);
            boughtButton = view.findViewById(R.id.gotButton);
            editCheck = view.findViewById(R.id.editCheck);
            editCancel = view.findViewById(R.id.editCancel);
            nameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editModeOn();
                }
            });
            nameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (view.getId() == R.id.itemEdit && !b) {
                        editModeOff();
                    }
                }
            });
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroceryAdapter.this.removeItem(grocery);
                    notifyDataSetChanged();
                }
            });
            boughtButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = grocery.switchChecked();
                    boughtButton.setChecked(isChecked);
                    ListManager.setChecked(grocery, isChecked);
                    GroceryAdapter.this.informActivityItemsChecked();
                }
            });
            editCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GroceryAdapter.this.modifyItem(grocery.getName(), nameEdit.getText().toString(), GroceryAdapter.this.fragment.getActivity());
                    grocery.setName(nameEdit.getText().toString());
                    editModeOff();
                    nameView.setText(grocery.getName());
                }
            });
            editCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editModeOff();
                }
            });
        }
        void bind(ListItem g) {
            grocery = g;
            boughtButton.setChecked(grocery.isChecked());
            nameView.setText(g.getName());
        }
        void editModeOn() {
            GroceryAdapter.this.setAddButtonVisibility(View.GONE);
            nameView.setVisibility(View.GONE);
            boughtButton.setVisibility(View.GONE);
            removeButton.setVisibility(View.GONE);
            nameEdit.setVisibility(View.VISIBLE);
            nameEdit.setText(grocery.getName());
            editCancel.setVisibility(View.VISIBLE);
            editCheck.setVisibility(View.VISIBLE);
        }
        void editModeOff() {
            nameEdit.setVisibility(View.GONE);
            editCheck.setVisibility(View.GONE);
            editCancel.setVisibility(View.GONE);
            nameView.setVisibility(View.VISIBLE);
            boughtButton.setVisibility(View.VISIBLE);
            removeButton.setVisibility(View.VISIBLE);
            GroceryAdapter.this.setAddButtonVisibility(View.VISIBLE);
        }
    }
}

