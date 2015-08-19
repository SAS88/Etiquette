package pk.edu.pucit.mian.loginapp;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Introduction extends android.support.v4.app.Fragment implements View.OnClickListener {


    private ImageButton loginButton;
    private ImageButton signupButton;

    private ImageView dot1;
    private ImageView dot2;
    private ImageView dot3;

    public Introduction() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_introduction, container, false);
    }

    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);

        dot1 = (ImageView)getActivity().findViewById(R.id.dot1);
        dot2 = (ImageView)getActivity().findViewById(R.id.dot2);
        dot3 = (ImageView)getActivity().findViewById(R.id.dot3);

        loginButton = (ImageButton)getActivity().findViewById(R.id.firstLogin);
        signupButton = (ImageButton)getActivity().findViewById(R.id.firstSignup);

        loginButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);

        ViewPager defaultViewpager = (ViewPager) getActivity().findViewById(R.id.viewpager_default);

        IntroPagerAdapter defaultPagerAdapter = new IntroPagerAdapter(getChildFragmentManager());
        defaultViewpager.setAdapter(defaultPagerAdapter);
        defaultViewpager.setClipToPadding(false);
        defaultViewpager.setPadding(50, 0, 50, 0);



        defaultViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        dot1.setImageResource(R.drawable.scrollbarfilled);
                        dot2.setImageResource(R.drawable.scrollbar);
                        dot3.setImageResource(R.drawable.scrollbar);

                        break;

                    case 1:
                        dot1.setImageResource(R.drawable.scrollbar);
                        dot2.setImageResource(R.drawable.scrollbarfilled);
                        dot3.setImageResource(R.drawable.scrollbar);
                        break;

                    case 2:
                        dot1.setImageResource(R.drawable.scrollbar);
                        dot2.setImageResource(R.drawable.scrollbar);
                        dot3.setImageResource(R.drawable.scrollbarfilled);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.firstLogin){
            LoginFragment newFrag = new LoginFragment();

            android.support.v4.app.FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();

            trans.replace(R.id.fragment_container, newFrag, "loginFragment");
            //trans.addToBackStack(null);
            trans.commit();
        }
        else if(view.getId() == R.id.firstSignup){
            SignUp newFrag = new SignUp();

            android.support.v4.app.FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();

            trans.replace(R.id.fragment_container, newFrag, "signupFragment");
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //trans.addToBackStack(null);
            //getActivity().getSupportFragmentManager().popBackStack();



            trans.commit();
        }
    }

}
