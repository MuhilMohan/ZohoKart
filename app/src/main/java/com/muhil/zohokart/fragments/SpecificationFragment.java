package com.muhil.zohokart.fragments;


import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muhil.zohokart.R;
import com.muhil.zohokart.models.Product;
import com.muhil.zohokart.models.specification.Specification;
import com.muhil.zohokart.utils.ZohokartDAO;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpecificationFragment extends android.support.v4.app.Fragment
{

    Gson gson;
    View rootView, specificationView;
    ZohokartDAO zohokartDAO;
    Product product;
    Map<String, List<Specification>> specificationGroup = null;
    TextView specificationGroupName;
    LayoutInflater layoutInflater;
    SpecCommunicator communicator;

    public static SpecificationFragment getInstance(Product product)
    {
        Gson gson = new Gson();
        SpecificationFragment specificationFragment = new SpecificationFragment();
        String productString = gson.toJson(product);
        Bundle bundle = new Bundle();
        bundle.putString("product", productString);
        specificationFragment.setArguments(bundle);
        return specificationFragment;
    }

    public void setCommunicator(SpecCommunicator communicator)
    {
        this.communicator = communicator;
    }

    public SpecificationFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        zohokartDAO = new ZohokartDAO(getActivity());
        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_specification, container, false);
        communicator.lockDrawer();

        product = gson.fromJson(getArguments().getString("product"), new TypeToken<Product>() {}.getType());
        new SpecificationAsyncTask().execute(product.getId());
        return rootView;
    }

    class SpecificationAsyncTask extends AsyncTask<Integer, View, Map<String, List<Specification>>>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            (rootView.findViewById(R.id.specs_progress)).setVisibility(View.VISIBLE);
        }

        @Override
        protected Map<String, List<Specification>> doInBackground(Integer... params)
        {
            return zohokartDAO.getSpecificationsByProductId(params[0]);
        }

        @Override
        protected void onProgressUpdate(View... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Map<String, List<Specification>> aVoid)
        {
            super.onPostExecute(aVoid);
            specificationGroup = aVoid;
            for (Map.Entry<String, List<Specification>> specificationEntry: specificationGroup.entrySet())
            {

                specificationGroupName = new TextView(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                specificationGroupName.setLayoutParams(params);
                specificationGroupName.setPadding(4, 4, 4, 4);
                specificationGroupName.setBackgroundColor(Color.parseColor("#BDBDBD"));
                specificationGroupName.setText(specificationEntry.getKey());
                specificationGroupName.setTextSize(18);
                ((ViewGroup) rootView.findViewById(R.id.specification_holder)).addView(specificationGroupName);

                for (Specification specification : specificationEntry.getValue())
                {
                    layoutInflater = LayoutInflater.from(getActivity());
                    specificationView = layoutInflater.inflate(R.layout.specification_item, ((ViewGroup) rootView.findViewById(R.id.specification_holder)), false);
                    ((TextView) specificationView.findViewById(R.id.specification_key)).setText(specification.getKey());
                    ((TextView) specificationView.findViewById(R.id.specification_value)).setText(specification.getValue());
                    ((ViewGroup) rootView.findViewById(R.id.specification_holder)).addView(specificationView);
                }

            }

            (rootView.findViewById(R.id.specification_holder)).setVisibility(View.VISIBLE);
            (rootView.findViewById(R.id.specs_progress)).setVisibility(View.GONE);

        }
    }

    public interface SpecCommunicator
    {
        void lockDrawer();
    }

}
