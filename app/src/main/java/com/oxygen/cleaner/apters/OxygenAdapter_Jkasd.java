package com.oxygen.cleaner.apters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.oxygen.cleaner.R;
import com.oxygen.cleaner.model.OxygenJuedkc;

public class OxygenAdapter_Jkasd extends OxygenAdapter_Selectasd<OxygenJuedkc, OxygenAdapter_Jkasd.ViewHolder> implements OxygenAdapter_Recasdycler.OnItemClickListener {
    public OxygenAdapter_Jkasd() {
        setItemClickListener(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(getInflater(viewGroup).inflate(R.layout.itm_junk_em, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        OxygenJuedkc junk = getItem(position);
        viewHolder.nameTV.setText(junk.getName());
        viewHolder.sizeTV.setText(junk.getSizeText());
        viewHolder.selectIV.setImageResource(junk.isDelete() ? R.drawable.sasdelect : R.drawable.seleasct_not);
        dispatchItemClick(position, viewHolder.itemView);
    }

    @Override
    public void onItemClick(int position, View target) {
        OxygenJuedkc junk = getItem(position);
        junk.setDelete(!junk.isDelete());
        notifyItemChanged(position, true);
        dispatchSelect();
    }

    @Override
    protected boolean isSelect(OxygenJuedkc junk) {
        return junk.isDelete();
    }

    @Override
    protected void setSelect(OxygenJuedkc junk, boolean select) {
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
