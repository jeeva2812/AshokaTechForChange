package com.iitm.interiitapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iitm.interiitapp.objects.MainMenuItem;
import com.iitm.interiitapp.R;

import java.util.ArrayList;
import java.util.List;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.ViewHolder> {

    private List<MainMenuItem> mainMenu;
    private Context context;


    public MainMenuAdapter(Context context, ArrayList<MainMenuItem> mainMenu) {
        this.mainMenu = mainMenu;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_menu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.icon.setImageResource(mainMenu.get(position).icon);
        holder.title.setText(mainMenu.get(position).title);
        holder.info.setText(mainMenu.get(position).info);
        holder.mmHolder.setOnClickListener(view -> {
            Intent intent = new Intent(context, mainMenu.get(position).toActivity);
            if(mainMenu.get(position).intentExtraName!=null)
                intent.putExtra(mainMenu.get(position).intentExtraName, mainMenu.get(position).intentExtraValue);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mainMenu.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mmHolder;
        ImageView icon;
        TextView title;
        TextView info;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.mm_icon);
            title = itemView.findViewById(R.id.mm_title);
            info = itemView.findViewById(R.id.mm_info);
            mmHolder = itemView.findViewById(R.id.mm_holder);
        }
    }


}
