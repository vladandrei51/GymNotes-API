package com.example.vlada.licenta.Views.Cardio;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.vlada.licenta.Base.BaseFragment;
import com.example.vlada.licenta.Domain.Cardio;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Views.Exercise.ExerciseListView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.realm.Sort;

/**
 * Created by andrei-valentin.vlad on 2/12/2018.
 */

public class CardioChartFragment extends BaseFragment {
    List<Cardio> mCardioList;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private BarChart mBarChart;
    private List<String> mXAxis;
    private String mExerciseName;

    public CardioChartFragment() {

    }

    public static CardioChartFragment newInstance(String text) {

        CardioChartFragment f = new CardioChartFragment();
        Bundle b = new Bundle();
        b.putString("exercise_name", text);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() == null) {
            Intent intent = new Intent(getActivity(), ExerciseListView.class);
            startActivity(intent);
        }

        if (getArguments() != null)
            mExerciseName = getArguments().getString("exercise_name");
        else {
            Intent intent = new Intent(getActivity(), ExerciseListView.class);
            startActivity(intent);
        }

        View rootView = inflater.inflate(R.layout.fragment_chart, container, false);
        mBarChart = rootView.findViewById(R.id.chart);
        Spinner spinner = rootView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.cardio_chart_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mSwipeRefreshLayout = rootView.findViewById(R.id.refresh_chart);
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshActivity);


        TextView mExerciseTV = rootView.findViewById(R.id.exercise_name_chart);
        mExerciseTV.setText(R.string.chart_type_selector);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCardioList = mRealmHelper.findAllFilteredSorted(Cardio.class, "exercise_name", mExerciseName, "setDate", Sort.ASCENDING);
                try {
                    setupChart(() -> {
                        switch (position) {
                            case 0:
                                return getMinutesDataSet();
                            default:
                                return getMinutesDataSet();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return rootView;
    }

    private void refreshActivity() {
        if (getFragmentManager() != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }

    }


    private ArrayList<BarDataSet> getMinutesDataSet() {
        ArrayList<BarDataSet> dataSets;
        ArrayList<BarEntry> valueSet = new ArrayList<>();

        mXAxis.forEach(cardioDate -> {
            BarEntry barEntry = new BarEntry((long) mCardioList.stream().filter(cardio -> cardio.date2PrettyString().equals(cardioDate)).map(Cardio::getTime_spent).max(Integer::compareTo).orElse(0), mXAxis.indexOf(cardioDate));
            valueSet.add(barEntry);
        });

        BarDataSet barDataSet = new BarDataSet(valueSet, "Highest Time");
        barDataSet.setColors(ColorTemplate.PASTEL_COLORS);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet);
        return dataSets;
    }


    void setupChart(Callable<ArrayList<BarDataSet>> barDataFunc) throws Exception {
        mXAxis = new ArrayList<>();
        mCardioList.forEach(l -> {
            String date = l.date2PrettyString();
            if (!mXAxis.contains(date)) mXAxis.add(date);
        });
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                BarData data;
                try {
                    data = new BarData(mXAxis, barDataFunc.call());
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            mBarChart.setData(data);
                            mBarChart.setDescription("");
                            mBarChart.animateXY(750, 750);
                            mBarChart.invalidate();

                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}