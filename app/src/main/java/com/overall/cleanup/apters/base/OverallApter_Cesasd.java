package com.overall.cleanup.apters.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.overall.cleanup.R;
import com.overall.cleanup.apters.OverallAdapter_Jkasd;
import com.overall.cleanup.apters.OverallAdapter_Recasdycler;
import com.overall.cleanup.apters.OverallAdapter_Selectasd;
import com.overall.cleanup.model.OverallStory;
import com.overall.cleanup.model.OverallJuedkc;

public class OverallApter_Cesasd extends OverallAdapter_Selectasd<OverallStory, OverallApter_Cesasd.ViewHolder> implements OverallAdapter_Recasdycler.OnItemClickListener {
    public OverallApter_Cesasd() {
        setItemClickListener(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(getInflater(viewGroup).inflate(R.layout.itm_cache_em, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final OverallStory cache = getItem(position);
        viewHolder.iconIV.setImageDrawable(cache.getIcon());
        viewHolder.nameTV.setText(cache.getLabel());
        viewHolder.sizeTV.setText(cache.getTextSize());
        setSelectIcon(cache, viewHolder);
        OverallAdapter_Jkasd junkAdapter = (OverallAdapter_Jkasd) viewHolder.cacheItemsRV.getAdapter();
        if (junkAdapter != null) {
            junkAdapter.removeItems();
        } else {
            junkAdapter = new OverallAdapter_Jkasd();
            junkAdapter.setSelectListener(new SelectListener() {
                @Override
                public void onAllSelect() {
                    viewHolder.selectIV.setImageResource(R.drawable.sasdelect);
                    viewHolder.selectIV.setActivated(true);
                    cache.setDelete(true);
                    OverallApter_Cesasd.this.dispatchSelect();
                }

                @Override
                public void onNotSelect() {
                    viewHolder.selectIV.setImageResource(R.drawable.sasdelect);
                    viewHolder.selectIV.setActivated(false);
                    cache.setDelete(false);
                    OverallApter_Cesasd.this.dispatchSelect();
                }

                @Override
                public void onPartSelect() {
                    viewHolder.selectIV.setImageResource(R.drawable.seasdlect_pasrt);
                    viewHolder.selectIV.setActivated(false);
                    cache.setDelete(false);
                    OverallApter_Cesasd.this.dispatchSelect();
                }
            });
            viewHolder.cacheItemsRV.setAdapter(junkAdapter);
            viewHolder.selectIV.setTag(junkAdapter);
        }
        junkAdapter.addItems(cache.getJunks());
        junkAdapter.notifyDataSetChanged();
        dispatchItemClick(position, viewHolder.itemView);
        dispatchItemClick(position, viewHolder.selectIV);
    }

    private void setSelectIcon(OverallStory cache, ViewHolder holder) {
        boolean hasSelect = false;
        boolean hasNotSelect = false;
        for (int index = 0; index < cache.getJunks().size(); index++) {
            if (!cache.getJunks().get(index).isDelete()) {
                hasNotSelect = true;
                if (hasSelect) {
                    break;
                }
            } else {
                hasSelect = true;
                if (hasNotSelect) {
                    break;
                }
            }
        }
        if (hasSelect && hasNotSelect) {
            holder.selectIV.setActivated(false);
            holder.selectIV.setImageResource(R.drawable.seasdlect_pasrt);
        } else if (hasSelect) {
            holder.selectIV.setActivated(true);
            holder.selectIV.setImageResource(R.drawable.sasdelect);
        } else {
            holder.selectIV.setActivated(false);
            holder.selectIV.setImageResource(R.drawable.seleasct_not);
        }
    }

    @Override
    public void onItemClick(int position, View target) {
        OverallStory cache = getItem(position);
        if (target.getId() == R.id.iv_select) {
            OverallAdapter_Jkasd junkAdapter = (OverallAdapter_Jkasd) target.getTag();
            if (junkAdapter != null) {
                junkAdapter.select(!target.isActivated());
            }
        } else {
            View view = target.findViewById(R.id.rv_cache_items);
            if (view.getVisibility() == View.GONE && !cache.getJunks().isEmpty()) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
            dispatchSelect();
        }
    }

    @Override
    protected boolean isSelect(OverallStory cache) {
        return cache.isDelete();
    }

    @Override
    protected void setSelect(OverallStory cache, boolean select) {
        for (OverallJuedkc junk : cache.getJunks()) {
            junk.setDelete(select);
        }
        cache.setDelete(select);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconIV;
        TextView nameTV;
        TextView sizeTV;
        ImageView selectIV;
        RecyclerView cacheItemsRV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconIV = itemView.findViewById(R.id.iv_icon);
            nameTV = itemView.findViewById(R.id.tv_name);
            sizeTV = itemView.findViewById(R.id.tv_size);
            selectIV = itemView.findViewById(R.id.iv_select);
            cacheItemsRV = itemView.findViewById(R.id.rv_cache_items);
        }
    }
}
