package com.pes.takemelegends.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pes.takemelegends.Controller.ControllerFactory;
import com.pes.takemelegends.Controller.UserController;
import com.pes.takemelegends.R;
import com.pes.takemelegends.Utils.SharedPreferencesManager;
import com.pes.takemelegends.Utils.URLResources;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import io.fabric.sdk.android.Fabric;

import static com.twitter.sdk.android.core.TwitterCore.TAG;

public class LoginActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "3BHl3HDnSPuOEz4h8udLjZ1an";
    private static final String TWITTER_SECRET = "gCwOn4hM5xiC07KJXpONWZUw0moyz5OhP92GYFZRxveCImCtBK";
    private static final int RC_SIGN_IN = 1;
    private Boolean isNewUser;
    private TwitterLoginButton loginButton;
    private LoginButton buttonFacebook;
    private GoogleApiClient mGoogleApiClient;
    private final UserController uc = ControllerFactory.getInstance().getUserController();
    private SharedPreferencesManager sharedPreferences;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = new SharedPreferencesManager(this);

        if (sharedPreferences.getUserId().equals("")) {

            //Facebook config
            FacebookSdk.sdkInitialize(getApplicationContext());
            callbackManager = CallbackManager.Factory.create();

            //Twitter config
            TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
            Fabric.with(this, new Twitter(authConfig));

            setContentView(R.layout.activity_login);

            //Twitter button
            loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
            loginButton.setText(getString(R.string.login_twitter));
            loginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    TwitterSucces(result);
                }

                private void TwitterSucces(Result<TwitterSession> result) {
                    final TwitterSession session = result.data;
                    Toast.makeText(getApplicationContext(), getString(R.string.login_success), Toast.LENGTH_LONG).show();
                    CreateUser(String.valueOf(session.getId()),"Twitter",session.getUserName());
                }

                @Override
                public void failure(TwitterException exception) {
                    Toast.makeText(getApplicationContext(), getString(R.string.login_fail), Toast.LENGTH_LONG).show();
                }
            });

            //Google
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            SignInButton signInButton = (SignInButton) findViewById(R.id.google_login_button);
            signInButton.setSize(SignInButton.SIZE_WIDE);
            setGooglePlusButtonText(signInButton);

            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInGoogle();

                }
            });


            //facebook button
            buttonFacebook = (LoginButton) findViewById(R.id.button_facebook);
            buttonFacebook.setReadPermissions("public_profile");

            // Callback registration
            buttonFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {


                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Log.v("LoginActivity", response.toString());

                                    // Application code
                                    String name = "";
                                    try {
                                        name = object.getString("name");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String msg = "@" + name + " logged in! (#" + loginResult.getAccessToken().getUserId() + ")";
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                    CreateUser(loginResult.getAccessToken().getUserId(), "Facebook", name);
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "name");
                    request.setParameters(parameters);
                    request.executeAsync();


                }

                @Override
                public void onCancel() {
                    Toast.makeText(getApplicationContext(), "facebook cancel", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(getApplicationContext(), "facebook error", Toast.LENGTH_LONG).show();
                    Log.e("error de facebook", exception.toString());
                }
            });



        }
        else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            Log.v("token", sharedPreferences.getUserToken());
            startActivity(intent);
            finish();
        }
    }
    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(getApplicationContext(), getString(R.string.login_success), Toast.LENGTH_LONG).show();
            CreateUser(acct != null ? acct.getId() : null, "Google", acct.getDisplayName());
        } else {
            // Signed out, show unauthenticated UI.

            Toast.makeText(getApplicationContext(), getString(R.string.login_fail), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void CreateUser(String uid, String provider, final String name) {
        JSONObject cli = new JSONObject();
        StringEntity entity;
        try {
            cli.put("appkey", URLResources.APP_KEY);
            cli.put("uid", uid);
            cli.put("provider", provider);
            cli.put("name", name);
            entity = new StringEntity(cli.toString());
            uc.postUser(entity, getApplicationContext(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    //Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                    try {

                        JSONObject user = response.getJSONObject("user");
                        sharedPreferences.setUserId(user.getString("uid"));
                        sharedPreferences.setUserName(name);
                        sharedPreferences.setUserProvider(user.getString("provider"));
                        sharedPreferences.setUserToken(user.getString("token"));
                        sharedPreferences.setFirstTime(!user.getBoolean("has_preferences"));
                        sharedPreferences.setTotalTakes(user.getInt("takes"));
                        sharedPreferences.setCurrentLevel(user.getInt("level"));
                        sharedPreferences.setCurrentExperience(user.getInt("experience"));
                        sharedPreferences.setNumberOfCheckins(user.getInt("number_checkins"));
                        sharedPreferences.setExperienceToNextLevel((float)user.getDouble("experience_of_next_level"));
                        sharedPreferences.setAttendanceUpdate(true);
                        sharedPreferences.setTodosUpdate(true);
                        sharedPreferences.setRecomendadosUpdate(true);
                        sharedPreferences.setDistance(500);
                        isNewUser = user.getBoolean("new_user");
                        Log.v("token", sharedPreferences.getUserToken());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent;
                    if (isNewUser) {
                        intent = new Intent(LoginActivity.this, PreferencesActivity.class);
                        intent.putExtra("skip", true);
                        sharedPreferences.setFirstTime(true);
                    }
                    else intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Toast.makeText(getApplicationContext(), "fail" + errorResponse.toString(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setGooglePlusButtonText(SignInButton signInButton) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(getString(R.string.login_google));
                return;
            }
        }
    }
}