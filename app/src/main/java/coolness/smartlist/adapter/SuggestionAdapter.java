package coolness.smartlist.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.ArrayList;
import coolness.smartlist.R;
import coolness.smartlist.fragment.SuggestionFragment;
import coolness.smartlist.model.SuggestionItem;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionHolder> {
    private ArrayList<SuggestionItem> suggestions;
    private SuggestionFragment fragment;

    public SuggestionAdapter(SuggestionFragment a) {
        fragment = a;
        suggestions = new ArrayList<>();
    }

    public SuggestionAdapter(SuggestionFragment a, ArrayList<SuggestionItem> g) {
        fragment = a;
        suggestions = g;
    }

    @Override
    @NonNull
    public SuggestionAdapter.SuggestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getActivity());
        return new SuggestionHolder(inflater.inflate(R.layout.item_suggestion, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionAdapter.SuggestionHolder holder, int position) {
        holder.bind(suggestions.get(position));
    }

    @Override
    public int getItemCount() {
        if (suggestions != null) {
            return suggestions.size();
        } else {
            return 0;
        }
    }

    public ArrayList<SuggestionItem> getCheckedItems() {
        ArrayList<SuggestionItem> tmp = new ArrayList<>();
        for (SuggestionItem s : suggestions) {
            if (s.isChecked()) {
                tmp.add(s);
            }
        }
        return tmp;
    }

    private boolean anyItemsChecked() {
        for (SuggestionItem g : suggestions) {
            if (g.isChecked()) {
                return true;
            }
        }
        return false;
    }

    private void informActivityItemsChecked() {
        fragment.anyItemsChecked(anyItemsChecked());
    }

    class SuggestionHolder extends RecyclerView.ViewHolder {
        private CheckBox wantButton;
        private SuggestionItem suggestion;
        private TextView nameView;
        private TextView listView;
        private TextView sinceBoughtView;

        SuggestionHolder(View view) {
            super(view);
            nameView = view.findViewById(R.id.itemName);
            listView = view.findViewById(R.id.itemList);
            sinceBoughtView = view.findViewById(R.id.sinceBought);
            wantButton = view.findViewById(R.id.wantButton);
            wantButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = suggestion.switchChecked();
                    wantButton.setChecked(isChecked);
                    SuggestionAdapter.this.informActivityItemsChecked();
                }
            });
        }

        void bind(SuggestionItem s) {
            suggestion = s;
            wantButton.setChecked(suggestion.isChecked());
            nameView.setText(s.getName());
            listView.setText(s.getListName());
            int weeks = s.getWeeksSinceBought();
            String sinceBought = (weeks == 1 ? "1 week " : weeks + " weeks ") + "ago";
            sinceBoughtView.setText(sinceBought);
        }
    }
}
