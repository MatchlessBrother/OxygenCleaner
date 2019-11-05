package com.overall.cleanup.apters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.overall.cleanup.R;
import com.overall.cleanup.model.OverallJuedkc;

public class OverallAdapter_Jkasd extends OverallAdapter_Selectasd<OverallJuedkc, OverallAdapter_Jkasd.ViewHolder> implements OverallAdapter_Recasdycler.OnItemClickListener {
    public OverallAdapter_Jkasd() {
        setItemClickListener(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(getInflater(viewGroup).inflate(R.layout.itm_junk_em, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        OverallJuedkc junk = getItem(position);
        viewHolder.nameTV.setText(junk.getName());
        viewHolder.sizeTV.setText(junk.getSizeText());
        viewHolder.selectIV.setImageResource(junk.isDelete() ? R.drawable.sasdelect : R.drawable.seleasct_not);
        dispatchItemClick(position, viewHolder.itemView);
    }

    @Override
    public void onItemClick(int position, View target) {
        OverallJuedkc junk = getItem(position);
        junk.setDelete(!junk.isDelete());
        notifyItemChanged(position, true);
        dispatchSelect();
    }

    @Override
    protected boolean isSelect(OverallJuedkc junk) {
        return junk.isDelete();
    }

    @Override
    protected void setSelect(OverallJuedkc junk, boolean select) {
        junk.setDelete(select);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV;
        TextView sizeTV;
        ImageView selectIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.tv_name);
            sizeTV = itemView.findViewById(R.id.tv_size);
            selectIV = itemView.findViewById(R.id.iv_select);
        }
    }
}
