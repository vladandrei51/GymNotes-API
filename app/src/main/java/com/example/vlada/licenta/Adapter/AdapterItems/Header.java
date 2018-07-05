package com.example.vlada.licenta.Adapter.AdapterItems;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.vlada.licenta.Adapter.HeaderItemListAdapter;
import com.example.vlada.licenta.R;

public class Header implements Item {
    private final String name;

    public String getName() {
        return name;
    }

    public Header(String name) {
        this.name = name;
    }

    @Override
    public int getViewType() {
        return HeaderItemListAdapter.RowType.HEADER_ITEM.ordinal();
    }


    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.adapter_header, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        TextView text = (TextView) view.findViewById(R.id.adapter_list_header);
        text.setText(name);

        return view;
    }

}
