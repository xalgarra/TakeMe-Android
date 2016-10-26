package com.pes.takemelegends.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pes.takemelegends.R;

public class LoginActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Button buttonFacebook = (Button)findViewById(R.id.button_facebook);
        buttonFacebook.setOnClickListener(this);

        Button buttonGoogle = (Button)findViewById(R.id.button_google);
        buttonGoogle.setOnClickListener(this);

        Button buttonTwitter = (Button)findViewById(R.id.button_twitter);
        buttonTwitter.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case  R.id.button_facebook:
            {
                Intent intent = new Intent(LoginActivity.this, PreferencesActivity.class);
                startActivity(intent);
                finish();
                break;
            }

            case R.id.button_google: {
                break;
            }

            case R.id.button_twitter: {
                break;
            }
        }

        /*Context context = this;
        new AlertDialog.Builder(context)
                .setTitle("Login")
                .setMessage("Are you sure you want to login?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)

        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        LoginActivity.this.startActivity(myIntent);
        
                .show();*/

    }
}
