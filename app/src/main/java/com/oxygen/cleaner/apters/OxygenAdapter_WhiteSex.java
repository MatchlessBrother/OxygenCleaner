package com.oxygen.cleaner.apters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oxygen.cleaner.R;
import com.oxygen.cleaner.model.OxygenWhites;

public class OxygenAdapter_WhiteSex extends OxygenAdapter_Recasdycler<OxygenWhites, OxygenAdapter_WhiteSex.ViewHolder> implements OxygenAdapter_Recasdycler.OnItemClickListener {
    public OxygenAdapter_WhiteSex() {
        setItemClickListener(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(getInflater(viewGroup).inflate(R.layout.itm_sdwhite_em, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        OxygenWhites white = getItem(position);
        viewHolder.iconIV.setImageDrawable(white.getIcon());
        viewHolder.nameTV.setText(white.getName());
        viewHolder.selectIV.setImageResource(white.isSelect() ? R.drawable.sasdelect : R.drawable.seleasct_not);
        dispatchItemClick(position, viewHolder.itemView);
    }

    @Override
    public void onItemClick(int position, View target) {
        OxygenWhites white = getItem(position);
        white.setSelect(!white.isSelect());
        notifyItemChanged(position, true);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconIV;
        TextView nameTV;
        ImageView selectIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconIV = itemView.findViewById(R.id.iv_icon);
            nameTV = itemView.findViewById(R.id.tv_name);
            selectIV = itemView.findViewById(R.id.iv_select);
        }
    }
}
