package com.overall.cleanup.apters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class OverallAdapter_Recasdycler<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {
    private static final int TAG_POSITION = 0x20000000;

    private final List<T> mItems = new ArrayList<>();
    private WeakReference<LayoutInflater> mInflaterRef;
    private OnItemClickListener mOnItemClickListener;

    public OverallAdapter_Recasdycler() {

    }

    public T getItem(int position) {
        return mItems.get(position);
    }

    public List<T> getItems() {
        return mItems;
    }

    public void addItem(T item) {
        mItems.add(item);
    }

    public void addItem(int position, T item) {
        mItems.add(0, item);
    }


    public void addItems(Collection<T> items) {
        mItems.addAll(items);
    }

    public void removeItem(int position) {
        mItems.remove(position);
    }

    public void removeItems() {
        mItems.clear();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    protected LayoutInflater getInflater(@NonNull View view) {
        LayoutInflater inflater = mInflaterRef != null ? mInflaterRef.get() : null;
        if (inflater == null) {
            inflater = LayoutInflater.from(view.getContext());
            mInflaterRef = new WeakReference<>(inflater);
        }
        return inflater;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    protected void dispatchItemClick(int position, View target) {
        if (target != null) {
            target.setTag(TAG_POSITION, position);
            target.setOnClickListener(mItemClickController);
        }
    }

    protected void interruptItemClick(View target) {
        if (target != null) {
            target.setOnClickListener(null);
        }
    }

    private View.OnClickListener mItemClickController = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            OnItemClickListener itemClickListener = mOnItemClickListener;
            if (itemClickListener != null) {
                if (itemClickListener instanceof OnAdapterClickListener) {
                    ((OnAdapterClickListener) itemClickListener).onItemClick((Integer) v.getTag(TAG_POSITION), v, OverallAdapter_Recasdycler.this);
                } else {
                    itemClickListener.onItemClick((Integer) v.getTag(TAG_POSITION), v);
                }
            }
        }
    };

    public interface OnItemClickListener {
        void onItemClick(int position, View target);
    }

    public abstract class OnAdapterClickListener<T> implements OnItemClickListener {
        @Override
        public void onItemClick(int position, View target) {
            //empty implement
        }

        abstract void onItemClick(int position, View target, OverallAdapter_Recasdycler<T, ?> adapter);
    }
}
