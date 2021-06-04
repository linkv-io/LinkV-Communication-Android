package com.linkv.lvcdemo;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.linkv.lvcdemo.view.DisplayContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYu on 2020/6/4 8:24 PM
 */
public class GridListLiveAdapter extends BaseAdapter {

    private List<DisplayContainer> mListDisplay = new ArrayList<>();

    public void addView(DisplayContainer view) {
        if (view != null) {
            mListDisplay.add(view);
            notifyDataSetChanged();
        }
    }

    public void removeView(DisplayContainer view) {
        if (view != null) {
            mListDisplay.remove(view);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mListDisplay.size();
    }

    @Override
    public Object getItem(int position) {
        return mListDisplay.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = null;
        if (position < mListDisplay.size()) {
            DisplayContainer displayContainer = mListDisplay.get(position);
            if (displayContainer != null) {
                itemView = displayContainer.getNineItemLayout();
            }
        }
        if (itemView != null) {
            return itemView;
        }
        return convertView;
    }

}
