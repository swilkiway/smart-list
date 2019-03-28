package coolness.smartlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.GroceryHolder> {
    private ArrayList<ListItem> groceries;
    private ListActivity activity;
    public GroceryAdapter(ListActivity a) {
        activity = a;
        groceries = new ArrayList<>();
    }
    public GroceryAdapter(ListActivity a, ArrayList<ListItem> g) {
        activity = a;
        groceries = g;
    }
    @Override
    @NonNull
    public GroceryAdapter.GroceryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        return new GroceryHolder(inflater.inflate(R.layout.item_list, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull GroceryAdapter.GroceryHolder holder, int position) {
        holder.bind(groceries.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (groceries != null) {
            return groceries.size();
        } else {
            return 0;
        }
    }

    public void removeItem(ListItem item) {
        Settings.removeFromList(item);
        notifyDataSetChanged();
    }

    public void addItem(ListItem item) {
        Settings.addToList(item);
        notifyDataSetChanged();
    }

    public void clearCheckedItems() {
        Settings.clearCheckedItems(activity);
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

    public void informActivityItemsChecked() {
        activity.anyItemsChecked(anyItemsChecked());
    }

    class GroceryHolder extends RecyclerView.ViewHolder {
        private Button removeButton;
        private CheckBox boughtButton;
        private ListItem grocery;
        private TextView nameView;
        GroceryHolder(View view) {
            super(view);
            nameView = view.findViewById(R.id.itemName);
            removeButton = view.findViewById(R.id.notButton);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroceryAdapter.this.removeItem(grocery);
                    notifyDataSetChanged();
                }
            });
            boughtButton = view.findViewById(R.id.gotButton);
            boughtButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = grocery.switchChecked();
                    boughtButton.setChecked(isChecked);
                    Settings.setChecked(grocery, isChecked);
                    GroceryAdapter.this.informActivityItemsChecked();
                }
            });
        }
        void bind(ListItem g, int position) {
            grocery = g;
            boughtButton.setChecked(grocery.isChecked());
            nameView.setText(g.getName());
        }
    }
}

