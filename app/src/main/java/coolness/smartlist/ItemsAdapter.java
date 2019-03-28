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
import java.util.Date;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsHolder> {
    private ArrayList<PreviousItem> items;
    private Context context;
    public ItemsAdapter(Context c) {
        context = c;
        items = new ArrayList<>();
    }
    public ItemsAdapter(Context c, ArrayList<PreviousItem> g) {
        context = c;
        items = g;
    }
    @Override
    @NonNull
    public ItemsAdapter.ItemsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ItemsAdapter.ItemsHolder(inflater.inflate(R.layout.item_prev, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ItemsHolder holder, int position) {
        holder.bind(items.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        } else {
            return 0;
        }
    }

    public void removeItem(PreviousItem item) {
        Settings.removeFromPreviousItems(item);
        notifyDataSetChanged();
    }

    class ItemsHolder extends RecyclerView.ViewHolder {
        private Button removeButton;
        private PreviousItem grocery;
        private TextView nameView;
        private TextView frequencyView;
        ItemsHolder(View view) {
            super(view);
            nameView = view.findViewById(R.id.itemName);
            frequencyView = view.findViewById(R.id.itemFrequency);
            removeButton = view.findViewById(R.id.deleteButton);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItemsAdapter.this.removeItem(grocery);
                    notifyDataSetChanged();
                }
            });
        }
        void bind(PreviousItem g, int position) {
            grocery = g;
            nameView.setText(g.getName());
            int weeks = g.getFrequency(new Date().getTime());
            StringBuilder frequency = new StringBuilder("Every ");
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
