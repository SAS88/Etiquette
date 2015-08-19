package pk.edu.pucit.mian.loginapp;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUp extends android.support.v4.app.Fragment implements
                View.OnClickListener{

    ImageButton signupButton;
    ImageView gotoLogin;

    public SignUp() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);

        signupButton = (ImageButton)getActivity().findViewById(R.id.signup_button);
        gotoLogin = (ImageView)getActivity().findViewById(R.id.goto_login);

        signupButton.setOnClickListener(this);
        gotoLogin.setOnClickListener(this);

    }


    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.signup_button)
        {
            String email = ((EditText)getActivity().findViewById(R.id.signup_email)).getText().toString();
            String passwrod = ((EditText)getActivity().findViewById(R.id.signup_password)).getText().toString();

            if(email.length() == 0 || passwrod.length() == 0)
            {
                Toast.makeText(getActivity(), "Please fill all details", Toast.LENGTH_SHORT).show();
            }
            else {
                View v = getActivity().getCurrentFocus();
                if (v != null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                new SignupAPI().execute(email, passwrod);
            }

        }
        else if(view.getId() == R.id.goto_login)
        {
            LoginFragment newFrag = new LoginFragment();

            android.support.v4.app.FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();

            trans.replace(R.id.fragment_container, newFrag, "signupFragment");
            //trans.addToBackStack(null);
            //getActivity().getSupportFragmentManager().popBackStack();
            trans.commit();
        }
    }

    class SignupAPI extends AsyncTask<String, Void, String> {

        private Exception exception;
        ProgressDialog progressDialog;

        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(), "Loading", "Signing Up", true);
        }

        protected String doInBackground(String... params) {
            String url = "http://etiquetteapp.azurewebsites.net/api/user/signup";

            String result = "";


            HttpClient httpclient = new DefaultHttpClient();

            HttpPost request = new HttpPost(url);

            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("phone_email", params[0]));
            parameters.add(new BasicNameValuePair("password", params[1]));

            try{
                request.setEntity(new UrlEncodedFormEntity(parameters));
            }
            catch (Exception ex){}

            HttpResponse response;

            try
            {
                response = httpclient.execute(request);

                result = EntityUtils.toString(response.getEntity());
            }
            catch(SocketTimeoutException ex)
            {
                //Toast.makeText(getActivity(), "Timeout Exception", Toast.LENGTH_LONG).show();
            }
            catch (ClientProtocolException e)
            {
                //Toast.makeText(getActivity(), "Client Protocol Ex", Toast.LENGTH_LONG).show();
            }
            catch (IOException e)
            {
                //Toast.makeText(getActivity(), "IO Exception", Toast.LENGTH_LONG).show();
            }
            catch(Exception ex)
            {
                //Toast.makeText(getActivity(), "Some Exception", Toast.LENGTH_LONG).show();
            }

            httpclient.getConnectionManager().shutdown();
            return result;
        }

        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            progressDialog = null;

            String message = "", status = "";
            JSONObject object;


            if(result.equals(""))
            {
                Toast.makeText(getActivity(), "Check Internet", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                object = new JSONObject(result);
                message = object.getString("message");
            }
            catch (Exception ex){}


            if(message.equals("Already Exist"))
            {
                Toast.makeText(getActivity(), "Account Already Exist", Toast.LENGTH_LONG).show();
            }
            else if(message.equals("Signed up successfully"))
            {
                ProfileFragment newFrag = new ProfileFragment();

                android.support.v4.app.FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();

                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                trans.replace(R.id.fragment_container, newFrag, "ProfileFragment").commit();
            }
        }
    }
}
