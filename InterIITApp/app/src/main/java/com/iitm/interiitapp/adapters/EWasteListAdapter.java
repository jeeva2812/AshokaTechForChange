package com.iitm.interiitapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iitm.interiitapp.objects.EWasteObject;
import com.iitm.interiitapp.R;

import java.util.ArrayList;

public class EWasteListAdapter extends RecyclerView.Adapter<EWasteListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<EWasteObject> types;

    public EWasteListAdapter(Context context){
        this.context = context;

        // Need to populate this
        types = new ArrayList<>();
        types.add(new EWasteObject(R.drawable.ic_smartphone, "Phones and Tablets", 0.2));
        types.add(new EWasteObject(R.drawable.ic_laptop, "Laptops", 2));
        types.add(new EWasteObject(R.drawable.ic_washing_machine, "Heavy Appliances", 20));
        types.add(new EWasteObject(R.drawable.ic_charger, "Accessories", 0.2));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ewaste, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.icon.setImageResource(types.get(position).imageId);
        holder.text.setText(types.get(position).text);
        holder.minus.setOnClickListener(view -> {
            if(types.get(position).qty > 0) {
                types.get(position).qty--;
                holder.tv_qty.setText(Integer.toString(types.get(position).qty));
            }

        });
        holder.plus.setOnClickListener(view -> {
            types.get(position).qty++;
            holder.tv_qty.setText(Integer.toString(types.get(position).qty));
        });
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    public double getEWasteAmount() {
        double amount = 0;
        for (EWasteObject type : types){
            amount += type.qty * type.approxWeight;
        }
        return  amount;
    }

    public String getComments(){
        String comments = "";
        for (EWasteObject type : types){
            comments = comments.concat(type.text + "- qty: " + type.qty + ", approx weight: " + type.qty*type.approxWeight + " kg\\n");
        }
        return comments;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView text, tv_qty;
        Button minus, plus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.ewaste_icon);
            text = itemView.findViewById(R.id.tv_ewaste_short_desc);
            minus = itemView.findViewById(R.id.btn_minus);
            plus = itemView.findViewById(R.id.btn_plus);
            tv_qty = itemView.findViewById(R.id.tv_qty);
        }
    }

}
