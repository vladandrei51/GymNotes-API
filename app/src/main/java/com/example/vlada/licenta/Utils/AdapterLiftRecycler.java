package com.example.vlada.licenta.Utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;

import java.util.Locale;

import io.realm.RealmResults;

public class AdapterLiftRecycler extends RecyclerView.Adapter {

    private RealmResults<Lift> itemList;
    private Context context;
    private View.OnClickListener listener;


    public AdapterLiftRecycler(RealmResults<Lift> itemList, Context context, View.OnClickListener listener) {
        this.itemList = itemList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.list_view, parent, false);
        view.setOnClickListener(listener);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Lift item = itemList.get(position);
        ViewHolder itemViewHolder = (ViewHolder) holder;
        itemViewHolder.loadItem(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;


        ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.label);
        }

        void loadItem(Lift item) {
            if (item.getReps() > 0) {
                if (item.getWeight() > 1)
                    text.setText(String.format(Locale.US, "%d kgs for %d reps", item.getWeight(), item.getReps()));
                else if (item.getWeight() == 1)
                    text.setText(String.format(Locale.US, "%d kg for %d reps", item.getWeight(), item.getReps()));
                else if (item.getWeight() == 0) {
                    text.setText(String.format(Locale.US, "Bodyweight for %d reps", item.getReps()));
                }
            } else if (item.getReps() == 0) {
                if (item.getWeight() > 1)
                    text.setText(String.format(Locale.US, "Failed attempt for %d kgs", item.getWeight()));
                else if (item.getWeight() == 1)
                    text.setText(String.format(Locale.US, "Failed attempt for %d kg", item.getWeight()));
                else if (item.getWeight() == 0)
                    text.setText("Failed attempt for bodyweight lift");
            }
        }
    }

}
