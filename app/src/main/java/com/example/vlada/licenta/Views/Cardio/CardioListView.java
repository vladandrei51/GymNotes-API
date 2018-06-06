package com.example.vlada.licenta.Views.Cardio;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.vlada.licenta.Adapter.ExerciseListRecyclerAdapter;
import com.example.vlada.licenta.Base.BaseFragment;
import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Net.Client.ExerciseClient;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.RealmBackup;
import com.example.vlada.licenta.Utils.Utils;
import com.example.vlada.licenta.Views.ExerciseView;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

public class CardioListView extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public ListenFromActivity activityListener;


    public void setActivityListener(ListenFromActivity activityListener) {
        this.activityListener = activityListener;
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio_list_view);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CardioFragment fragment = new CardioFragment();

        fragmentTransaction.replace(R.id.cardio_frame, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        inflater.inflate(R.menu.search_view, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(() -> {
            if (activityListener != null)
                activityListener.clearSearch();
            return false;

        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        RealmBackup realmBackup = new RealmBackup(getApplicationContext(), Realm.getDefaultConfiguration());
        switch (item.getItemId()) {
            case R.id.backup_menu:
                realmBackup.backup();
                break;
            case R.id.restore_menu:
                realmBackup.restore();
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();

                overridePendingTransition(0, 0);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (activityListener != null && !Objects.equals(query, ""))
            activityListener.filterResultsInFragment(query);
        return false;

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (activityListener != null && !Objects.equals(newText, ""))
            activityListener.filterResultsInFragment(newText);
        return false;
    }


    public interface ListenFromActivity {
        void filterResultsInFragment(String text);

        void clearSearch();
    }

    public static class CardioFragment extends BaseFragment implements ListenFromActivity {
        private RecyclerView mRecycler;
        private RealmResults<Exercise> mResults;
        private CompositeDisposable mDisposable = new CompositeDisposable();
        private ExerciseClient mExerciseClient;
        private ExerciseListRecyclerAdapter mAdapter;


        public CardioFragment() {
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_cardio_list, container, false);

            if (getActivity() != null) {
                ((CardioListView) getActivity()).setActivityListener(CardioFragment.this);
            }

            if (getActivity() != null) getActivity().setTitle("Cardio");

            mExerciseClient = new ExerciseClient(getContext());
            mResults = mRealmHelper.allCardioExercisesByRating(Exercise.class);
            mAdapter = new ExerciseListRecyclerAdapter(mResults, new ItemsListener(), mRealmHelper.getRealm());


            mRecycler = rootView.findViewById(R.id.recycler);
            mRecycler.setHasFixedSize(true);

            final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecycler.getContext(),
                    layoutManager.getOrientation());
            mRecycler.addItemDecoration(dividerItemDecoration);
            mRecycler.setLayoutManager(layoutManager);

            mRecycler.setAdapter(mAdapter);
            AsyncTask.execute(this::populateFromDB);


            return rootView;
        }

        private void populateFromDB() {
            mDisposable.add(mExerciseClient.getCardioExercises()
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            this::getExercisesSuccess,
                            this::getExercisesError
                    )
            );

        }

        private void getExercisesSuccess(List<Exercise> exercises) {
            mRealmHelper.deleteAllCardioFiltered(Exercise.class);
            exercises.sort(
                    Comparator.comparing(Exercise::getRating));
            mRealmHelper.insertAllFromList(exercises);
        }

        private void getExercisesError(Throwable throwable) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Utils.showAlertDialog(getContext(), "Cached data is being used", throwable.getMessage());
                    mAdapter.notifyDataSetChanged();
                });
            }
        }

        private void reinitializeAdapter() {
            mAdapter = new ExerciseListRecyclerAdapter(mResults, new ItemsListener(), mRealmHelper.getRealm());
            mRecycler.setAdapter(mAdapter);
        }


        @Override
        public void filterResultsInFragment(String text) {
            mAdapter.filterCardioExercises(text);
        }

        @Override
        public void clearSearch() {
            reinitializeAdapter();
        }


        class ItemsListener implements AdapterView.OnClickListener {
            @Override
            public void onClick(View view) {
                Exercise exercise = mAdapter.getItem(mRecycler.getChildAdapterPosition(view));
                if (exercise != null) {
                    Intent intent = new Intent(getContext(), ExerciseView.class);
                    intent.putExtra("exercise_name", exercise.getName());
                    startActivity(intent);
                }
            }
        }
    }
}

