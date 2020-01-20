package com.overall.cleanup.apters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.overall.cleanup.R;
import com.overall.cleanup.model.OverallWhites;

public class OverallAdapter_WhiteSex extends OverallAdapter_Recasdycler<OverallWhites, OverallAdapter_WhiteSex.ViewHolder> implements OverallAdapter_Recasdycler.OnItemClickListener {
    public OverallAdapter_WhiteSex() {
        setItemClickListener(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(getInflater(viewGroup).inflate(R.layout.itm_sdwhite_em, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        OverallWhites white = getItem(position);
        viewHolder.iconIV.setImageDrawable(white.getIcon());
        viewHolder.nameTV.setText(white.getName());
        viewHolder.selectIV.setImageResource(white.isSelect() ? R.drawable.sasdelect : R.drawable.seleasct_not);
        dispatchItemClick(position, viewHolder.itemView);
    }

    @Override
    public void onItemClick(int position, View target) {
        OverallWhites white = getItem(position);
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
