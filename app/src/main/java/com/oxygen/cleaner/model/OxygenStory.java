package com.oxygen.cleaner.model;

import android.graphics.drawable.Drawable;
import java.util.List;

public class OxygenStory {
    private long size;
    private String textSize;
    private CharSequence label;
    private Drawable icon;
    private boolean delete;
    private List<OxygenJuedkc> junks;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getTextSize() {
        return textSize;
    }

    public void setTextSize(String textSize) {
        this.textSize = textSize;
    }

    public CharSequence getLabel() {
        return label;
    }

    public void setLabel(CharSequence label) {
        this.label = label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public List<OxygenJuedkc> getJunks() {
        return junks;
    }

    public void setJunks(List<OxygenJuedkc> junks) {
        this.junks = junks;
    }
}
