package com.iitm.interiitapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iitm.interiitapp.R;
import com.iitm.interiitapp.activities.user_request_activities.WasteTipsActivity;
import com.iitm.interiitapp.adapters.MainMenuAdapter;
import com.iitm.interiitapp.helpers.SessionManager;
import com.iitm.interiitapp.networking.LoginRequest;
import com.iitm.interiitapp.objects.MainMenuItem;

import java.util.ArrayList;

public class UserMainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MainMenuAdapter adapter;
    SessionManager sessionManager;
    ArrayList<MainMenuItem> mainMenuItemArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);

        if(!sessionManager.isLoggedIn()){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        else if(sessionManager.getState() == LoginRequest.SCRAP_COLLECTOR){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }




        setContentView(R.layout.layout_main_menu);
        recyclerView = findViewById(R.id.rv_main_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        populateMainMenuItemArrayList();

        adapter = new MainMenuAdapter(this, mainMenuItemArrayList);
        recyclerView.setAdapter(adapter);
    }

    private void populateMainMenuItemArrayList() {
        mainMenuItemArrayList = new ArrayList<MainMenuItem>();
        mainMenuItemArrayList.add(new MainMenuItem(R.drawable.ic_new_waste, "New Waste", "Add new waste here", WasteTypesActivity.class));
        mainMenuItemArrayList.add(new MainMenuItem(R.drawable.ic_waste_history, "View Request History", "View Request History", UserWasteHistoryActivity.class));
        mainMenuItemArrayList.add(new MainMenuItem(R.drawable.mapofraddi, "Show Nearby Collectors", "Map showing closest scrap collectors to you.", MapsActivity.class));
        mainMenuItemArrayList.add(new MainMenuItem(R.drawable.ic_about, "Waste Tips", "Waste Tips", WasteTipsActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.item_menu_logout:{
                sessionManager.logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }
    }
}
