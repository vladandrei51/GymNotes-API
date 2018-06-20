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

import com.example.vlada.licenta.Domain.Cardio;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.Utils;
import com.example.vlada.licenta.Views.Cardio.CardioLiftFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.realm.RealmResults;

import static com.example.vlada.licenta.Utils.Utils.distinctByKey;

public class CardioLiftsRecyclerAdapter extends RecyclerView.Adapter {

    private RealmResults<Cardio> mItemsList;
    private List<Cardio> mAdapterItems;
    private Context mContext;
    private View.OnClickListener mListener;
    private ArrayList<Boolean> mIsCardio;
    private int mExpandedPosition = -1;
    private Cardio mClicked;
    private Fragment mFragment;
    private int mPreviousExpandedPosition = -1;

    public CardioLiftsRecyclerAdapter(RealmResults<Cardio> itemList, Context context, View.OnClickListener listener, Fragment fragment) {
        this.mItemsList = itemList;
        this.mFragment = fragment;
        this.mContext = context;
        this.mListener = listener;
        mAdapterItems = new ArrayList<>();
        mIsCardio = new ArrayList<>();
        loadAdapterItems();
    }

    private void loadAdapterItems() {
        for (Cardio cardio : mItemsList.stream().filter(distinctByKey(Cardio::date2PrettyString)).collect(Collectors.toCollection(ArrayList::new))) {
            mAdapterItems.add(cardio);
            mIsCardio.add(false);
            for (Cardio cardio2 : mItemsList.stream().filter(l -> l.date2PrettyString().equals(cardio.date2PrettyString())).collect(Collectors.toCollection(ArrayList::new))) {
                mAdapterItems.add(cardio2);
                mIsCardio.add(true);
            }
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_cardio_lift, parent, false);
        view.setOnClickListener(mListener);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemViewHolder = (ViewHolder) holder;
        if (mIsCardio.get(position)) itemViewHolder.loadCardio(mAdapterItems.get(position));
        else itemViewHolder.loadDate(mAdapterItems.get(position));

        if (mIsCardio.get(position)) {
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
                mClicked = mAdapterItems.get(position);
                notifyItemChanged(mPreviousExpandedPosition);
                notifyItemChanged(position);
            });
        }

    }


    private Cardio getClicked() {
        return mClicked;
    }

    @Override
    public int getItemCount() {
        return mItemsList.size()
                + (int) mItemsList.stream().map(Cardio::date2PrettyString).distinct().count();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView mCardioTextTV;
        TextView mDateTV;
        TextView mNotesTV;
        LinearLayout mDetailsLayout;
        ImageButton mDeleteButton;
        ImageButton mEditButton;

        ViewHolder(View itemView) {
            super(itemView);
            mCardioTextTV = itemView.findViewById(R.id.LiftTV_cardio);
            mDateTV = itemView.findViewById(R.id.dateTV_cardio);
            mNotesTV = itemView.findViewById(R.id.notesTV_cardio);
            mDetailsLayout = itemView.findViewById(R.id.conditionally_visible_layout_cardio);
            mDeleteButton = itemView.findViewById(R.id.deleteLiftBT_cardio);
            mEditButton = itemView.findViewById(R.id.editLiftBT_cardio);
            mEditButton.setOnClickListener(v -> ((CardioLiftFragment) mFragment).editLift(getClicked()));
            mDeleteButton.setOnClickListener(v -> {
                ((CardioLiftFragment) mFragment).deleteLift(getClicked());
                notifyDataSetChanged();
            });
        }


        void loadCardio(Cardio currentCardio) {
            mDateTV.setVisibility(View.GONE);
            mCardioTextTV.setVisibility(View.VISIBLE);
            mCardioTextTV.setText(Utils.getPrettySetFromCardio(currentCardio.getTime_spent()));
            if (currentCardio.getNotes().length() > 0) {
                mCardioTextTV.append("*");
            }

            if (Utils.is1RMCardio(currentCardio, mItemsList)) {
                mCardioTextTV.setText(new String(Character.toChars(0x1F3C6)) + " " + mCardioTextTV.getText());
            }
        }

        void loadDate(Cardio cardio) {
            mCardioTextTV.setVisibility(View.GONE);
            mDateTV.setVisibility(View.VISIBLE);
            mDateTV.setText(cardio.date2PrettyString());
        }

    }

}
