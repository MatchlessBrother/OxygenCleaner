package com.overall.cleanup.apters.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.overall.cleanup.R;
import com.overall.cleanup.apters.OverallAdapter_Recasdycler;


public class OverallBaseAddapter extends OverallAdapter_Recasdycler<OverallCleanAppliction, OverallBaseAddapter.ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(getInflater(viewGroup).inflate(R.layout.itm_cache_em, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        OverallCleanAppliction baseApp = getItem(position);
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
