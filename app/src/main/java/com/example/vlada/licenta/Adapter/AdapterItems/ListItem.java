package com.example.vlada.licenta.Adapter.AdapterItems;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.vlada.licenta.Adapter.HeaderItemListAdapter;
import com.example.vlada.licenta.R;

public class ListItem implements Item {
    private final String str1;

    public ListItem(String text1) {
        this.str1 = text1;
    }

    @Override
    public int getViewType() {
        return HeaderItemListAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.adapter_item, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        TextView text1 = view.findViewById(R.id.adapter_list_item);
        text1.setText(str1);

        return view;
    }

    public String getStr1() {
        return str1;
    }
}
