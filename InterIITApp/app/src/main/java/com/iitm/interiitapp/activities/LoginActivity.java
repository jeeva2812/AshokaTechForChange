package com.iitm.interiitapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iitm.interiitapp.R;
import com.iitm.interiitapp.activities.scrap_collector_activities.ScrapCollectorFillDetailsActivity;
import com.iitm.interiitapp.activities.scrap_collector_activities.ScrapCollectorMainActivity;
import com.iitm.interiitapp.helpers.SessionManager;
import com.iitm.interiitapp.networking.LoginRequest;
import com.iitm.interiitapp.networking.VolleySingleton;

public class LoginActivity extends AppCompatActivity {

    EditText mPhone, mPassword;
    Button mLogin, mSignUp;
    TextView textView;
    int state;

    SessionManager sessionManager;
    TextView tv_scrap_collector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn())
            openMainActivity();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        textView = findViewById(R.id.tv_user);

        mPhone = findViewById(R.id.phone);
        mPassword = findViewById(R.id.password);
        mLogin = findViewById(R.id.login);
        mSignUp = findViewById(R.id.signup);

        state = LoginRequest.USER;

        mLogin.setOnClickListener(view -> {
            String phone = mPhone.getText().toString();
            String password = mPassword.getText().toString();
            Log.d("Vallabh", "phone: "+phone);
            Log.d("Vallabh", "pass: "+password);
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(new LoginRequest(
                    phone,
                    password,
                    getApplicationContext(),
                    responseCode -> {
                        if(responseCode == 200) {
                            sessionManager.saveLoginDetails(phone, password, state);
                            openMainActivity();
                        }
                        else if(responseCode == 400)
                            Toast.makeText(getApplicationContext(), "Wrong mobile/password.", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_SHORT).show();
                    },
                    state
            ));
        });

        mSignUp.setOnClickListener(view -> {
            if(state == LoginRequest.SCRAP_COLLECTOR)
                startActivity(new Intent(LoginActivity.this, ScrapCollectorFillDetailsActivity.class));
            else
                startActivity(new Intent(LoginActivity.this, UserFillDetailsActivity.class));
        });


        tv_scrap_collector = findViewById(R.id.tv_scrap_collector);
        tv_scrap_collector.setOnClickListener(view -> {
            if(state == LoginRequest.USER) {
                state = LoginRequest.SCRAP_COLLECTOR;
                textView.setText("Collector!");
                tv_scrap_collector.setText("Consumer?");
            }
            else{
                state = LoginRequest.USER;
                textView.setText("Consumer!");
                tv_scrap_collector.setText("Scrap Collector?");
            }
        });


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        state = LoginRequest.USER;
        textView.setText("Consumer");
    }

    @Override
    protected void onStart() {
        super.onStart();
        state = LoginRequest.USER;
        textView.setText("Consumer");
    }

    public void openMainActivity(){
        Intent intent;
        state = sessionManager.getState();
        if(state == LoginRequest.USER)
            intent = new Intent(LoginActivity.this, UserMainActivity.class);
        else
            intent = new Intent(LoginActivity.this, ScrapCollectorMainActivity.class);
        startActivity(intent);
        finish();
    }
}
