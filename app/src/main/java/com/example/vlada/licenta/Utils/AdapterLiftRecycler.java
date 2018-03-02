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

    private RealmResults<Lift> mItemsList;
    private List<Lift> mAdapterItems;
    private Context mContext;
    private View.OnClickListener mListener;
    private ArrayList<Boolean> mIsLift;

    public AdapterLiftRecycler(RealmResults<Lift> itemList, Context context, View.OnClickListener listener) {
        this.mItemsList = itemList;
        this.mContext = context;
        this.mListener = listener;
        mAdapterItems = new ArrayList<>();
        mIsLift = new ArrayList<>();
        loadAdapterItems();
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private void loadAdapterItems() {
        for (Lift lift : mItemsList.stream().filter(distinctByKey(Lift::date2PrettyString)).collect(Collectors.toCollection(ArrayList::new))) {
            mAdapterItems.add(lift);
            mIsLift.add(false);
            for (Lift lift2 : mItemsList.stream().filter(l -> l.date2PrettyString().equals(lift.date2PrettyString())).collect(Collectors.toCollection(ArrayList::new))) {
                mAdapterItems.add(lift2);
                mIsLift.add(true);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.lift_list_view, parent, false);
        view.setOnClickListener(mListener);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemViewHolder = (ViewHolder) holder;
        if (mIsLift.get(position)) itemViewHolder.loadLift(mAdapterItems.get(position));
        else itemViewHolder.loadDate(mAdapterItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemsList.size()
                + (int) mItemsList.stream().map(Lift::date2PrettyString).distinct().count();
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
                    mLiftTextTV.setText(R.string.failed_bw);
            } else if (currentLift.getReps() == 1) {
                if (currentLift.getWeight() > 1)
                    mLiftTextTV.setText(String.format(Locale.US, "%d kgs for 1 rep", currentLift.getWeight()));
                else if (currentLift.getWeight() == 1)
                    mLiftTextTV.setText(String.format(Locale.US, "%d kg for 1 rep", currentLift.getWeight()));
                else if (currentLift.getWeight() == 0) {
                    mLiftTextTV.setText(R.string.bw_for_1);
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