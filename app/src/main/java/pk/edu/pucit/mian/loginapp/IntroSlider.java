package pk.edu.pucit.mian.loginapp;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class IntroSlider extends android.support.v4.app.Fragment {


    public IntroSlider() {
        // Required empty public constructor
    }

    public static android.support.v4.app.Fragment getInstance(int position)
    {
        return new IntroSlider();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_intro_slider, container, false);
    }


}
