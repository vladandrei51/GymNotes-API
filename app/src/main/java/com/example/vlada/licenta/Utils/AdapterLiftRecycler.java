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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.realm.RealmResults;

public class AdapterLiftRecycler extends RecyclerView.Adapter {

    private RealmResults<Lift> itemList;
    private List<Lift> adapterItems;
    private Context context;
    private View.OnClickListener listener;
    private List<String> datesList;

    public AdapterLiftRecycler(RealmResults<Lift> itemList, Context context, View.OnClickListener listener) {
        this.itemList = itemList;
        this.context = context;
        this.listener = listener;
        datesList = new ArrayList<>();
        adapterItems = new ArrayList<>();
        loadAdapterItems();
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private void loadAdapterItems() {
        for (Lift lift : itemList.stream().filter(distinctByKey(Lift::date2PrettyString)).collect(Collectors.toCollection(ArrayList::new))) {
            adapterItems.add(lift);
            adapterItems.addAll(itemList.stream().filter(l -> l.date2PrettyString().equals(lift.date2PrettyString())).collect(Collectors.toCollection(ArrayList::new)));
        }
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
        ViewHolder itemViewHolder = (ViewHolder) holder;
        if (position == 0) itemViewHolder.loadDate(adapterItems.get(position));
        else {
            if (adapterItems.get(position).date2PrettyString().equals(adapterItems.get(position - 1).date2PrettyString())) {
                itemViewHolder.loadLift(adapterItems.get(position));
            } else {
                itemViewHolder.loadDate(adapterItems.get(position));
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

        void loadDate(String date) {
            mLiftTextTV.setVisibility(View.GONE);
            mDateTV.setVisibility(View.VISIBLE);
            mDateTV.setText(date);

        }


    }

}
