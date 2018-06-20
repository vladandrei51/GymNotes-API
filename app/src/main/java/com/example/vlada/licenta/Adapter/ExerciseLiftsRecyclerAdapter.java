package com.example.vlada.licenta.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.Utils;
import com.example.vlada.licenta.Views.Exercise.ExerciseLiftFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.realm.RealmResults;

import static com.example.vlada.licenta.Utils.Utils.distinctByKey;

public class ExerciseLiftsRecyclerAdapter extends RecyclerView.Adapter {

    private RealmResults<Lift> mItemsList;
    private List<Lift> mAdapterItems;
    private Context mContext;
    private View.OnClickListener mListener;
    private ArrayList<Boolean> mIsLift;
    private int mExpandedPosition = -1;
    private Lift mClickedLift;
    private Fragment mFragment;
    private int mPreviousExpandedPosition = -1;


    public ExerciseLiftsRecyclerAdapter(RealmResults<Lift> itemList, Context context, View.OnClickListener listener, Fragment fragment) {
        this.mItemsList = itemList;
        this.mFragment = fragment;
        this.mContext = context;
        this.mListener = listener;
        mAdapterItems = new ArrayList<>();
        mIsLift = new ArrayList<>();
        loadAdapterItems();
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_exercise_lift, parent, false);
        view.setOnClickListener(mListener);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemViewHolder = (ViewHolder) holder;

        if (mIsLift.get(position)) {
            itemViewHolder.loadLift(mAdapterItems.get(position));
            final boolean isExpanded = position == mExpandedPosition;
            itemViewHolder.mDetailsLayout.setVisibility(isExpanded && mAdapterItems.get(position).getNotes().length() > 0 ? View.VISIBLE : View.GONE);
            itemViewHolder.mNotesTV.setText(mAdapterItems.get(position).getNotes().length() > 0 ? mAdapterItems.get(position).getNotes() : "");
            itemViewHolder.mDeleteButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            itemViewHolder.mEditButton.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            holder.itemView.setActivated(isExpanded);

            if (isExpanded)
                mPreviousExpandedPosition = position;

            holder.itemView.setOnClickListener(v -> {
                mExpandedPosition = isExpanded ? -1 : position;
                mClickedLift = mAdapterItems.get(position);
                notifyItemChanged(mPreviousExpandedPosition);
                notifyItemChanged(position);
            });
        } else itemViewHolder.loadDate(mAdapterItems.get(position));
    }

    private Lift getClickedLift() {
        return mClickedLift;
    }

    @Override
    public int getItemCount() {
        return mItemsList.size() + (int) mItemsList.stream().map(Lift::date2PrettyString).distinct().count();
    }


    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView mLiftTextTV;
        TextView mDateTV;
        TextView mNotesTV;
        LinearLayout mDetailsLayout;
        ImageButton mDeleteButton;
        ImageButton mEditButton;

        ViewHolder(View itemView) {
            super(itemView);
            mLiftTextTV = itemView.findViewById(R.id.LiftTV);
            mDateTV = itemView.findViewById(R.id.dateTV);
            mNotesTV = itemView.findViewById(R.id.notesTV);
            mDetailsLayout = itemView.findViewById(R.id.conditionally_visible_layout);
            mDeleteButton = itemView.findViewById(R.id.deleteLiftBT);
            mEditButton = itemView.findViewById(R.id.editLiftBT);
            mEditButton.setOnClickListener(v -> ((ExerciseLiftFragment) mFragment).editLift(getClickedLift()));
            mDeleteButton.setOnClickListener(v -> {
                ((ExerciseLiftFragment) mFragment).deleteLift(getClickedLift());
                notifyDataSetChanged();
            });
        }


        void loadLift(Lift currentLift) {
            mDateTV.setVisibility(View.GONE);
            mLiftTextTV.setVisibility(View.VISIBLE);
            mLiftTextTV.setText(Utils.getPrettySetFromLift(currentLift.getWeight(), currentLift.getReps()));
            if (currentLift.getNotes().length() > 0) {
                mLiftTextTV.append("*");
            }
            if (Utils.is1RM(currentLift, mItemsList)) {
                mLiftTextTV.setText(new String(Character.toChars(0x1F3C6)) + " " + mLiftTextTV.getText());
            }
        }

        void loadDate(Lift currentLift) {
            mLiftTextTV.setVisibility(View.GONE);
            mDateTV.setVisibility(View.VISIBLE);
            mDateTV.setText(currentLift.date2PrettyString());
        }

    }

}
