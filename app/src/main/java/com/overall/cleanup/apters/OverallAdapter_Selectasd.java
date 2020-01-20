package com.overall.cleanup.apters;
import androidx.recyclerview.widget.RecyclerView;

public abstract class OverallAdapter_Selectasd<T, V extends RecyclerView.ViewHolder> extends OverallAdapter_Recasdycler<T, V> {
    protected SelectListener mSelectListener;

    protected void dispatchSelect() {
        boolean hasSelect = false;
        boolean hasNotSelect = false;
        for (int index = 0; index < getItemCount(); index++) {
            if (!isSelect(getItem(index))) {
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
            if (mSelectListener != null) {
                mSelectListener.onPartSelect();
            }
        } else if (hasSelect) {
            if (mSelectListener != null) {
                mSelectListener.onAllSelect();
            }
        } else {
            mSelectListener.onNotSelect();
        }
    }

    public void select(boolean all) {
        if (getItemCount() == 0) {
            if (all) {
                if (mSelectListener != null) {
                    mSelectListener.onAllSelect();
                }
            } else {
                if (mSelectListener != null) {
                    mSelectListener.onNotSelect();
                }
            }
        } else {
            for (int index = 0; index < getItemCount(); index++) {
                setSelect(getItem(index), all);
                notifyItemChanged(index, true);
            }
            dispatchSelect();
        }
    }

    public void setSelectListener(SelectListener selectListener) {
        this.mSelectListener = selectListener;
    }

    protected abstract boolean isSelect(T t);

    protected abstract void setSelect(T t, boolean select);

    public interface SelectListener {
        void onAllSelect();

        void onNotSelect();

        void onPartSelect();
    }
}
