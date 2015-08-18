package pk.edu.pucit.mian.loginapp;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Popular extends android.support.v4.app.Fragment {


    public Popular() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_popular, container, false);
    }

    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);

        ArrayList<String> texts = new ArrayList<String>();

        texts.add("This is sample text");
        texts.add("This is sample text");
        texts.add("This is sample text");
        texts.add("This is sample text");
        texts.add("This is sample text");

        ListView list = (ListView) getActivity().findViewById(R.id.popularList);
        ListAdapter viewadapter = new ListAdapter(getActivity(), texts , 5);
        list.setAdapter(viewadapter);
    }


}
