package pk.edu.pucit.mian.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.login.LoginManager;


public class MainActivity extends ActionBarActivity implements
        View.OnClickListener {

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        Introduction newFrag = new Introduction();

        android.support.v4.app.FragmentTransaction trans = getSupportFragmentManager().beginTransaction();

        trans.add(R.id.fragment_container, newFrag).commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LoginFragment frag = (LoginFragment)getSupportFragmentManager().findFragmentByTag("loginFragment");
        if(frag != null && frag.isVisible())
        {
            frag.onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    public void onClick(View v) {

    }

    public void onBackPressed() {
        Fragment popularFragment = (Fragment) getSupportFragmentManager().findFragmentByTag("PopularFragment");
        Fragment profileFragment = (Fragment) getSupportFragmentManager().findFragmentByTag("ProfileFragment");
        Fragment loginFragment = (Fragment) getSupportFragmentManager().findFragmentByTag("loginFragment");
        Fragment signupFragment = (Fragment) getSupportFragmentManager().findFragmentByTag("signupFragment");

        if ((loginFragment != null && loginFragment.isVisible())
                || (signupFragment != null && signupFragment.isVisible())) {
            Introduction newFrag = new Introduction();

            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            android.support.v4.app.FragmentTransaction trans = getSupportFragmentManager().beginTransaction();

            trans.replace(R.id.fragment_container, newFrag, "introFragment");

            trans.commit();

        }
        else if ((popularFragment != null && popularFragment.isVisible())
                || (profileFragment != null && profileFragment.isVisible())) {
            if (doubleBackToExitPressedOnce) {
                try {
                    LoginManager.getInstance().logOut();
                }
                catch (Exception e){}

                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
