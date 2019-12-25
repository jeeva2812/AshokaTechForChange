package com.iitm.interiitapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iitm.interiitapp.R;
import com.iitm.interiitapp.objects.WasteHistoryObject;
import com.iitm.interiitapp.objects.WasteRequestObject;
import com.iitm.interiitapp.objects.WasteTypeEnum;

import java.util.List;

public class WasteHistoryAdapter extends RecyclerView.Adapter<WasteHistoryAdapter.ViewHolder> {

    private List<WasteHistoryObject> wasteHistoryObjects;

    public WasteHistoryAdapter(List<WasteHistoryObject> wasteHistoryObjects){
        this.wasteHistoryObjects = wasteHistoryObjects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_waste_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int drawableId = 0;
        switch (wasteHistoryObjects.get(position).wasteType){
            case WasteTypeEnum.PAPER: { drawableId = R.drawable.paper; break;}
            case WasteTypeEnum.PLASTIC: {drawableId = R.drawable.plastic; break;}
            //case WasteTypeEnum.METAL: {}
            case WasteTypeEnum.EWASTE: {drawableId = R.drawable.ewaste; break;}
        }
        holder.tv_waste_id.setText(wasteHistoryObjects.get(position).id+"");
        holder.iv_waste_type.setImageResource(drawableId);
        holder.tv_created_on.setText(wasteHistoryObjects.get(position).createdAt);
        holder.tv_collected_on.setText(wasteHistoryObjects.get(position).collectedAt==null ? "-" : wasteHistoryObjects.get(position).collectedAt);
        holder.tv_collected_by.setText(wasteHistoryObjects.get(position).collectedBy==null ? "-" : wasteHistoryObjects.get(position).collectedBy);
        holder.tv_comments.setText(wasteHistoryObjects.get(position).comments);
    }

    public void setWasteHistoryObjects(List<WasteHistoryObject> wasteHistoryObjects){
        this.wasteHistoryObjects = wasteHistoryObjects;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return wasteHistoryObjects.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_waste_type;
        TextView tv_waste_id;
        TextView tv_created_on;
        TextView tv_collected_on;
        TextView tv_collected_by;
        TextView tv_comments;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_waste_id = itemView.findViewById(R.id.tv_waste_id);
            iv_waste_type = itemView.findViewById(R.id.iv_waste_type);
            tv_created_on = itemView.findViewById(R.id.tv_created_at);
            tv_collected_on = itemView.findViewById(R.id.tv_collected_at);
            tv_collected_by = itemView.findViewById(R.id.tv_collected_by);
            tv_comments = itemView.findViewById(R.id.tv_comments);
        }
    }
}
