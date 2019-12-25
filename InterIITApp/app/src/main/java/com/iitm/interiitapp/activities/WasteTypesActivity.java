package com.iitm.interiitapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.iitm.interiitapp.R;
import com.iitm.interiitapp.activities.user_request_activities.EWasteRequestActivity;
import com.iitm.interiitapp.activities.user_request_activities.PaperPlasticMetalRequestActivity;
import com.iitm.interiitapp.adapters.MainMenuAdapter;
import com.iitm.interiitapp.helpers.SessionManager;
import com.iitm.interiitapp.objects.MainMenuItem;
import com.iitm.interiitapp.objects.WasteTypeEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class WasteTypesActivity extends AppCompatActivity {

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

        setContentView(R.layout.layout_main_menu);
        recyclerView = findViewById(R.id.rv_main_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        populateMainMenuItemArrayList();

        adapter = new MainMenuAdapter(this, mainMenuItemArrayList);
        recyclerView.setAdapter(adapter);
    }

    private void populateMainMenuItemArrayList() {
        mainMenuItemArrayList = new ArrayList<MainMenuItem>();
        mainMenuItemArrayList.add(new MainMenuItem(
                R.drawable.paper,
                "Paper Waste",
                "Newspapers, Books, Magazines, Cardboard boxes and other paper derived items.",
                PaperPlasticMetalRequestActivity.class,
                WasteTypeEnum.INTENT_NAME,
                WasteTypeEnum.PAPER
        ));
        mainMenuItemArrayList.add(new MainMenuItem(
                R.drawable.plastic,
                "Plastic Waste",
                "PET Bottles, Oil Cans, Plastic jars and other plastic containers.",
                PaperPlasticMetalRequestActivity.class,
                WasteTypeEnum.INTENT_NAME,
                WasteTypeEnum.PLASTIC
        ));
        mainMenuItemArrayList.add(new MainMenuItem(
                R.drawable.metal,
                "Metal Waste",
                "Cans, utensils, used metallic ladders, stools, chairs, bicycles.",
                PaperPlasticMetalRequestActivity.class,
                WasteTypeEnum.INTENT_NAME,
                WasteTypeEnum.METAL
        ));
        mainMenuItemArrayList.add(new MainMenuItem(
                R.drawable.ewaste,
                "E-Waste",
                "Phones, Tablets, Laptops, Electronic Accessories, Printers and other electronic gadgets and appliances.",
                EWasteRequestActivity.class
        ));
    }
}
