package com.iitm.interiitapp.activities.scrap_collector_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iitm.interiitapp.R;
import com.iitm.interiitapp.activities.MapsActivity;
import com.iitm.interiitapp.networking.NewScrapCollectorRequest;
import com.iitm.interiitapp.networking.VolleySingleton;

public class ScrapCollectorFillDetailsActivity extends AppCompatActivity {

    public static final String NEED_TO_CHOOSE_LOCATION = "NeedToChooseLocation";
    public static final int LOCATION_SELECTED = 101;

    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrap_collector_fill_details);

        lat = -91; lng = -181;

        findViewById(R.id.btn_sc_register).setOnClickListener(view -> registerDetails());
        findViewById(R.id.btn_get_coordinates).setOnClickListener(view -> openMapActivity());

    }

    private void openMapActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(NEED_TO_CHOOSE_LOCATION, true);
        startActivityForResult(intent, LOCATION_SELECTED);
    }

    private void registerDetails() {
        String name = ((EditText) findViewById(R.id.nameET)).getText().toString();
        String mobile = ((EditText) findViewById(R.id.mobileET)).getText().toString();
        String password = ((EditText) findViewById(R.id.newpasswordET)).getText().toString();
        String confirmPassword = ((EditText) findViewById(R.id.confirmpasswordET)).getText().toString();

        double vehicleCap = 0;
        if(!((EditText) findViewById(R.id.vehicleCapacityET)).getText().toString().equals(""))
            vehicleCap = Double.parseDouble(((EditText) findViewById(R.id.vehicleCapacityET)).getText().toString());

        String email = ((EditText) findViewById(R.id.emailET)).getText().toString();
        String description = ((EditText) findViewById(R.id.descriptionET)).getText().toString();
        String licenseNo = ((EditText) findViewById(R.id.licenseNoET)).getText().toString();

        if (name.equals("") || mobile.equals("") || password.equals("") || confirmPassword.equals("") || vehicleCap == 0 || lat == -91 || lng == -181)
            Toast.makeText(ScrapCollectorFillDetailsActivity.this, "Fill all fields", Toast.LENGTH_LONG).show();
        else if (password.length() < 8)
            Toast.makeText(ScrapCollectorFillDetailsActivity.this, "Password too short", Toast.LENGTH_LONG).show();
        else if(!password.equals(confirmPassword))
            Toast.makeText(ScrapCollectorFillDetailsActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
        else
            VolleySingleton.getInstance(ScrapCollectorFillDetailsActivity.this).addToRequestQueue(new NewScrapCollectorRequest(
                    mobile,
                    password,
                    name,
                    email,
                    description,
                    licenseNo,
                    vehicleCap,
                    lat,
                    lng,
                    response ->{
                        Toast.makeText(getApplicationContext(), "Registered as scrap collector!", Toast.LENGTH_SHORT).show();
                        finish();
                    },
                    ScrapCollectorFillDetailsActivity.this
            ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (requestCode == LOCATION_SELECTED) {
            if (resultCode == LOCATION_SELECTED) {
                lat = resultIntent.getDoubleExtra("latitude", -91);
                lng = resultIntent.getDoubleExtra("longitude", -181);
                String s;
                if(lat == -91 || lng == -181)
                    s = "Lat: - Long: -";
                else
                    s = "Lat: "+lat+ " Long: "+lng;
                ((TextView) findViewById(R.id.tv_loc_selected)).setText(s);
            }
        }
    }
}
