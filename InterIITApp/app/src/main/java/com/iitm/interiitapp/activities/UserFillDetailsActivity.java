package com.iitm.interiitapp.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.iitm.interiitapp.R;
import com.iitm.interiitapp.networking.NewUserRequest;
import com.iitm.interiitapp.networking.VolleySingleton;

public class UserFillDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_fill_details);

        findViewById(R.id.btn_sc_register).setOnClickListener(view -> registerDetails());
    }

    private void registerDetails() {
        String mobile = ((EditText) findViewById(R.id.mobileET)).getText().toString();
        String password = ((EditText) findViewById(R.id.newpasswordET)).getText().toString();
        String confirmPassword = ((EditText) findViewById(R.id.confirmpasswordET)).getText().toString();


        if (mobile.equals("") || password.equals("") || confirmPassword.equals(""))
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_LONG).show();
        else if (password.length() < 8)
            Toast.makeText(this, "Password too short", Toast.LENGTH_LONG).show();
        else if(!password.equals(confirmPassword))
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
        else
            VolleySingleton.getInstance(this).addToRequestQueue(new NewUserRequest(
                    mobile,
                    password,
                    response ->{
                        Toast.makeText(getApplicationContext(), "Registered new user!", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    this
            ));
    }
}
