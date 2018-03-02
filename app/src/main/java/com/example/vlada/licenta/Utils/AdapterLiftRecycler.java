package com.example.vlada.licenta.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.RealmResults;

public class AdapterLiftRecycler extends RecyclerView.Adapter {

    private RealmResults<Lift> itemList;
    private Context context;
    private View.OnClickListener listener;
    private List<String> datesList;


    public AdapterLiftRecycler(RealmResults<Lift> itemList, Context context, View.OnClickListener listener) {
        this.itemList = itemList;
        this.context = context;
        this.listener = listener;
        datesList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.lift_list_view, parent, false);
        view.setOnClickListener(listener);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Lift currentLift = itemList.get(position - datesList.size());
        if (currentLift != null) {
            if (!datesList.contains(currentLift.date2PrettyString())) {
                datesList.add(currentLift.date2PrettyString());
                ViewHolder itemViewHolder = (ViewHolder) holder;
                itemViewHolder.loadDate(currentLift);
            } else {
                ViewHolder itemViewHolder = (ViewHolder) holder;
                itemViewHolder.loadLift(currentLift);
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size()
                + (int) itemList.stream().map(Lift::date2PrettyString).distinct().count();
    }


    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView mLiftTextTV;
        TextView mDateTV;

        ViewHolder(View itemView) {
            super(itemView);
            mLiftTextTV = itemView.findViewById(R.id.label);
            mDateTV = itemView.findViewById(R.id.dateTV);
            mDateTV.setKeyListener(null);
        }

        void loadLift(Lift currentLift) {
            mDateTV.setVisibility(View.GONE);
            mLiftTextTV.setVisibility(View.VISIBLE);
            if (currentLift.getReps() > 1) {
                if (currentLift.getWeight() > 1)
                    mLiftTextTV.setText(String.format(Locale.US, "%d kgs for %d reps", currentLift.getWeight(), currentLift.getReps()));
                else if (currentLift.getWeight() == 1)
                    mLiftTextTV.setText(String.format(Locale.US, "%d kg for %d reps", currentLift.getWeight(), currentLift.getReps()));
                else if (currentLift.getWeight() == 0) {
                    mLiftTextTV.setText(String.format(Locale.US, "Bodyweight lift for %d reps", currentLift.getReps()));
                }
            } else if (currentLift.getReps() == 0) {
                if (currentLift.getWeight() > 1)
                    mLiftTextTV.setText(String.format(Locale.US, "Failed attempt for %d kgs", currentLift.getWeight()));
                else if (currentLift.getWeight() == 1)
                    mLiftTextTV.setText(String.format(Locale.US, "Failed attempt for %d kg", currentLift.getWeight()));
                else if (currentLift.getWeight() == 0)
                    mLiftTextTV.setText("Failed attempt for a bodyweight lift");
            } else if (currentLift.getReps() == 1) {
                if (currentLift.getWeight() > 1)
                    mLiftTextTV.setText(String.format(Locale.US, "%d kgs for 1 rep", currentLift.getWeight()));
                else if (currentLift.getWeight() == 1)
                    mLiftTextTV.setText(String.format(Locale.US, "%d kg for 1 rep", currentLift.getWeight()));
                else if (currentLift.getWeight() == 0) {
                    mLiftTextTV.setText("Bodyweight lift for 1 rep");
                }
            }
        }

        void loadDate(Lift currentLift) {
            mLiftTextTV.setVisibility(View.GONE);
            mDateTV.setVisibility(View.VISIBLE);
            mDateTV.setText(currentLift.date2PrettyString());
        }


    }

}
