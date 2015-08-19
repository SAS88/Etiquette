package pk.edu.pucit.mian.loginapp;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.model.PlatformActionListener;
import com.liulishuo.share.weibo.WeiboLoginManager;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends android.support.v4.app.Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private ImageButton facebookButton;
    private ImageButton googleButton;
    private ImageButton wechatButton;
    private ImageButton weiboButton;
    private ImageButton loginButton;
    private ImageView gotoSignup;
    private CallbackManager callbackManager;
    private Activity activity;

    PlatformActionListener platformActionListener;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FacebookSdk.sdkInitialize(activity.getApplicationContext());
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        this.activity = activity;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onActivityCreated(Bundle bundle)
    {
        super.onActivityCreated(bundle);

        googleButton = (ImageButton)activity.findViewById(R.id.google_login);
        wechatButton = (ImageButton)activity.findViewById(R.id.wechat_login);
        weiboButton = (ImageButton)activity.findViewById(R.id.weibo_login);
        loginButton = (ImageButton)activity.findViewById(R.id.login_button);

        gotoSignup = (ImageView)activity.findViewById(R.id.goto_signup);

        googleButton.setOnClickListener(this);
        weiboButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);

        gotoSignup.setOnClickListener(this);

        facebookButton = (ImageButton)activity.findViewById(R.id.facebook_login);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().setLoginBehavior(LoginBehavior.SSO_WITH_FALLBACK);
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        try {
                            loginResult.getAccessToken();

                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            // Application code
                                            Log.v("LoginActivity", response.toString());

                                            try {
                                                String email = object.getString("email");
                                                String id = object.getString("id");

                                                new SigninAPI().execute(email, "123456789", "facebook");
                                            }
                                            catch (Exception ex){}
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,gender, birthday");
                            request.setParameters(parameters);
                            request.executeAsync();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException exception) {

                    }
                });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile, email, user_birthday"));
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.PLUS_ME))
                .build();

        activity.findViewById(R.id.google_login).setOnClickListener(this);

        ShareBlock.getInstance()
//                .initAppName("LoginApp")
//                .initQQ(OAuthConstant.QQ_APPID, OAuthConstant.QQ_SCOPE)
//                .initWechat(OAuthConstant.WECHAT_APPID, OAuthConstant.WECHAT_SECRET)
                .initWeibo("2535020927");

        platformActionListener = new PlatformActionListener() {
            @Override
            public void onComplete(HashMap<String, Object> hashMap) {
                String id = (String)hashMap.get("unionid");

                //we will use user id to login as each user has unique id
                SigninAPI api = new SigninAPI();
                api.execute(id, "123456789", "weibo");
            }

            @Override
            public void onError() {
                Toast.makeText(activity, "onError", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(activity, "onCancel", Toast.LENGTH_LONG).show();
            }
        };

        //WeiboLoginManager wm = new WeiboLoginManager(activity);
        //wm.login(platformActionListener);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != activity.RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
        else if(requestCode == 32973)
        {
            if(resultCode != activity.RESULT_OK)
            {
                Toast.makeText(getActivity(), "Weibo Error", Toast.LENGTH_SHORT).show();
            }
            else
            {
                WeiboLoginManager.getSsoHandler().authorizeCallBack(requestCode, resultCode, data);

            }
        }
        else
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        //Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();


            //info.setText(personName);
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

            SigninAPI api = new SigninAPI();
            api.execute(email, "123456789", "google");

        }

        // Show the signed-in UI
        //showSignedInUI();
    }

    @Override
    public void onConnectionSuspended(int arg0) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.google_login) {
            onSignInClicked();
        }
        else if(v.getId() == R.id.weibo_login){
            WeiboLoginManager wm = new WeiboLoginManager(activity);
            wm.login(platformActionListener);
        }
        else if(v.getId() == R.id.goto_signup){

            SignUp newFrag = new SignUp();

            android.support.v4.app.FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();

            trans.replace(R.id.fragment_container, newFrag, "loginFragment");
            //trans.addToBackStack(null);
            //getActivity().getSupportFragmentManager().popBackStack();
            trans.commit();
        }
        else if(v.getId() == R.id.login_button)
        {
            String email = ((EditText)getActivity().findViewById(R.id.login_email)).getText().toString();
            String passwrod = ((EditText)getActivity().findViewById(R.id.login_password)).getText().toString();

            if(email.length() == 0 || passwrod.length() == 0)
            {
                Toast.makeText(getActivity(), "Please fill all details", Toast.LENGTH_SHORT).show();
            }
            else {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                new SigninAPI().execute(email, passwrod);
            }
        }
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
        //info.setText("Google signed in");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d("error", "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(activity, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e("error", "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
            }
        } else {
            // Show the signed-out UI
            //showSignedOutUI();
        }
    }

    class SigninAPI extends AsyncTask<String, Void, String> {

        private Exception exception;
        ProgressDialog progressDialog;

        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(), "Loading", "Logging In", true);
        }

        protected String doInBackground(String... params) {
            String url = "http://etiquetteapp.azurewebsites.net/api/user/signin";

            String result = "";


            HttpClient httpclient = new DefaultHttpClient();

            HttpPost request = new HttpPost(url);

            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            if(params.length == 2) {
                parameters.add(new BasicNameValuePair("phone_email", params[0]));
                parameters.add(new BasicNameValuePair("password", params[1]));
            }
            else
            {
                parameters.add(new BasicNameValuePair("phone_email", params[0]));
                parameters.add(new BasicNameValuePair("password", params[1]));
                parameters.add(new BasicNameValuePair("social_media", params[2]));
            }
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


            if(message.equals("Information is incorrect"))
            {
                Toast.makeText(getActivity(), "Incorrect email/password", Toast.LENGTH_LONG).show();
            }
            else if(message.equals("Signed in successfully"))
            {
                Popular newFrag = new Popular();

                android.support.v4.app.FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                trans.replace(R.id.fragment_container, newFrag, "PopularFragment").commit();
            }
            else if(message.equals("Signed in successfully, after Sign up"))
            {
                ProfileFragment newFrag = new ProfileFragment();

                android.support.v4.app.FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();

                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                trans.replace(R.id.fragment_container, newFrag, "ProfileFragment").commit();
            }
        }
    }

    private String convertInputStreamToString(InputStream inputStream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        String result = "";

        try {
            while ((line = reader.readLine()) != null){
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


}
