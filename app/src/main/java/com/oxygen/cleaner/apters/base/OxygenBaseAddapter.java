package com.oxygen.cleaner.apters.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oxygen.cleaner.R;
import com.oxygen.cleaner.apters.OxygenAdapter_Recasdycler;


public class OxygenBaseAddapter extends OxygenAdapter_Recasdycler<OxygenCleanAppliction, OxygenBaseAddapter.ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(getInflater(viewGroup).inflate(R.layout.itm_cache_em, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        OxygenCleanAppliction baseApp = getItem(position);
        viewHolder.iconIV.setImageDrawable(baseApp.getIcon());
        viewHolder.nameTV.setText(baseApp.getName());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconIV;
        TextView nameTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconIV = itemView.findViewById(R.id.iv_icon);
            nameTV = itemView.findViewById(R.id.tv_name);
        }
    }
}
