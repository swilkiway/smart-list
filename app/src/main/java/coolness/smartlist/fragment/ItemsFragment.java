package coolness.smartlist.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import coolness.smartlist.R;
import coolness.smartlist.ListManager;
import coolness.smartlist.adapter.ItemsAdapter;

public class ItemsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        RecyclerView itemsList = view.findViewById(R.id.itemsList);
        RecyclerView.LayoutManager itemsManager = new LinearLayoutManager(getContext());
        itemsList.setLayoutManager(itemsManager);
        ItemsAdapter itemsAdapter;
        if (ListManager.getPreviousItems() != null) {
            itemsAdapter = new ItemsAdapter(getActivity(), ListManager.getPreviousItems());
        } else {
            itemsAdapter = new ItemsAdapter(getActivity());
        }
        itemsList.setAdapter(itemsAdapter);
    }
}
