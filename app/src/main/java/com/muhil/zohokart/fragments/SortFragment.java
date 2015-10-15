package com.muhil.zohokart.fragments;


import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.muhil.zohokart.R;
import com.muhil.zohokart.comparators.PriceHighToLowComparator;
import com.muhil.zohokart.comparators.PriceLowToHighComparator;
import com.muhil.zohokart.comparators.StarsHighToLowComparator;
import com.muhil.zohokart.comparators.StarsLowToHighComparator;

import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class SortFragment extends DialogFragment {

    View sortOptionsLayout;
    String[] sortOptions;
    TextView textView;
    SortOptionsCommunicator communicator;

    public SortFragment() {
        // Required empty public constructor
    }

    public static SortFragment getInstance(String[] sortOptions)
    {

        SortFragment sortFragment = new SortFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray("sort_options", sortOptions);
        sortFragment.setArguments(bundle);
        return sortFragment;

    }

    public void getComm(SortOptionsCommunicator communicator)
    {
        this.communicator = communicator;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sortOptionsLayout = inflater.inflate(R.layout.fragment_sort, container, false);
        sortOptions = getArguments().getStringArray("sort_options");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 8, 0, 0);
        textView = new TextView(getActivity());
        textView.setText("Sort By");
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18);
        textView.setLayoutParams(params);
        ((LinearLayout) sortOptionsLayout.findViewById(R.id.sort_options)).addView(textView);

        for (int i = 0; i<sortOptions.length; i++) {

            textView = new TextView(getActivity());
            textView.setText(sortOptions[i]);
            textView.setTextColor(Color.parseColor("#3a3535"));
            textView.setTag(i);
            textView.setTextSize(14);
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(16, 16, 0, 0);
            textView.setLayoutParams(params);
            ((LinearLayout) sortOptionsLayout.findViewById(R.id.sort_options)).addView(textView);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();

                    communicator.sendIndex(index);
                    dismiss();

                }
            });

        }

        return sortOptionsLayout;

    }

    public interface SortOptionsCommunicator
    {
        public void sendIndex(int index);
    }

}
