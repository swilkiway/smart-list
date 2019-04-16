package coolness.smartlist.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import coolness.smartlist.ListManager;
import coolness.smartlist.model.PreviousItem;
import coolness.smartlist.R;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsHolder> {
    private ArrayList<PreviousItem> items;
    private Activity activity;
    public ItemsAdapter(Activity a) {
        activity = a;
        items = new ArrayList<>();
    }
    public ItemsAdapter(Activity a, ArrayList<PreviousItem> g) {
        activity = a;
        items = g;
    }
    @Override
    @NonNull
    public ItemsAdapter.ItemsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        return new ItemsAdapter.ItemsHolder(inflater.inflate(R.layout.item_prev, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ItemsHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    private void removeItem(PreviousItem item, Activity activity) {
        ListManager.removeFromPreviousItems(item, activity);
        notifyDataSetChanged();
    }

    class ItemsHolder extends RecyclerView.ViewHolder {
        private Button removeButton;
        private PreviousItem grocery;
        private TextView nameView;
        private TextView listView;
        private TextView frequencyView;
        ItemsHolder(View view) {
            super(view);
            nameView = view.findViewById(R.id.itemName);
            listView = view.findViewById(R.id.itemList);
            frequencyView = view.findViewById(R.id.itemFrequency);
            removeButton = view.findViewById(R.id.deleteButton);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItemsAdapter.this.removeItem(grocery, ItemsAdapter.this.activity);
                    notifyDataSetChanged();
                }
            });
        }
        void bind(PreviousItem g) {
            grocery = g;
            nameView.setText(g.getName());
            listView.setText(g.getListName());
            int weeks = g.getFrequency(new Date().getTime());
            StringBuilder frequency = new StringBuilder();
            if (weeks > 1) {
                frequency.append(weeks);
                frequency.append(" weeks");
            } else {
                frequency.append("week");
            }
            frequencyView.setText(frequency.toString());
        }
    }
}
