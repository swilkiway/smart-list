package coolness.smartlist.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Date;

import coolness.smartlist.ListManager;
import coolness.smartlist.R;
import coolness.smartlist.model.SuggestionItem;
import coolness.smartlist.adapter.SuggestionAdapter;

public class SuggestionFragment extends Fragment {
    private Button keepItems;
    private Button rejectAll;
    private SuggestionAdapter suggestionAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggestion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        RecyclerView suggestionList = view.findViewById(R.id.suggestionList);
        RecyclerView.LayoutManager suggestionManager = new LinearLayoutManager(getContext());
        suggestionList.setLayoutManager(suggestionManager);
        long today = new Date().getTime();
        ArrayList<SuggestionItem> suggestions = ListManager.getSuggestions(today);
        if (suggestions != null) {
            suggestionAdapter = new SuggestionAdapter(this, suggestions);
        } else {
            suggestionAdapter = new SuggestionAdapter(this);
        }
        suggestionList.setAdapter(suggestionAdapter);
        keepItems = view.findViewById(R.id.keepChecked);
        keepItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListManager.addToLists(suggestionAdapter.getCheckedItems(), getActivity());
                Fragment fragment = new CurrListFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                ft.commit();
            }
        });
        rejectAll = view.findViewById(R.id.rejectAll);
        rejectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new CurrListFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.content_frame, fragment).addToBackStack(null);
                ft.commit();
            }
        });
    }

    public void anyItemsChecked(boolean checked) {
        if (checked) {
            keepItems.setEnabled(true);
        } else {
            keepItems.setEnabled(false);
        }
    }
}
