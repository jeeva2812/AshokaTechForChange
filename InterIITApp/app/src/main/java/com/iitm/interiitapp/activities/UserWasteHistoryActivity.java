package com.iitm.interiitapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.iitm.interiitapp.R;
import com.iitm.interiitapp.adapters.WasteHistoryAdapter;
import com.iitm.interiitapp.helpers.SessionManager;
import com.iitm.interiitapp.networking.VolleySingleton;
import com.iitm.interiitapp.networking.WasteHistoryRequest;

import java.util.ArrayList;

public class UserWasteHistoryActivity extends AppCompatActivity {

    RecyclerView wasteHistoryList;
    WasteHistoryAdapter adapter;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_menu);
        sessionManager = new SessionManager(getApplicationContext());
        wasteHistoryList = findViewById(R.id.rv_main_menu);
        wasteHistoryList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WasteHistoryAdapter(new ArrayList<>());
        wasteHistoryList.setAdapter(adapter);

        VolleySingleton.getInstance(this).addToRequestQueue(new WasteHistoryRequest(
                sessionManager.getMobile(),
                sessionManager.getPassword(),
                response -> adapter.setWasteHistoryObjects(response),
                getApplicationContext()
        ));

    }


}
