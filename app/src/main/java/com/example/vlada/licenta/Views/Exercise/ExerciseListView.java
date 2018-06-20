package com.example.vlada.licenta.Views.Exercise;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.vlada.licenta.Adapter.ExerciseListRecyclerAdapter;
import com.example.vlada.licenta.Base.BaseFragment;
import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Domain.MuscleGroup;
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

/**
 * Created by andrei-valentin.vlad on 2/7/2018.
 */

public class ExerciseListView extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public ListenFromActivity activityListener;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private List<String> mMuscleGroups;

    public void setActivityListener(ListenFromActivity activityListener) {
        this.activityListener = activityListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list_view);


        mTitle = getTitle();
        mMuscleGroups = MuscleGroup.getAllNames();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, mMuscleGroups));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                /* host Activity */
                mDrawerLayout,              /* DrawerLayout object */
                R.string.drawer_open,       /* "open drawer" description for accessibility */
                R.string.drawer_close       /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mTitle + " exercises");
                    invalidateOptionsMenu();
                }
            }

            public void onDrawerOpened(View drawerView) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.drawer_open);
                    invalidateOptionsMenu();
                }
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
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


    private void selectItem(int position) {

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ExerciseFragment fragment = new ExerciseFragment();
        Bundle args = new Bundle();
        args.putInt(ExerciseFragment.ARG_NUMBER, position);
        fragment.setArguments(args);

        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();


        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mMuscleGroups.get(position) + " exercises");
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTitle + " exercises");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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

    public static class ExerciseFragment extends BaseFragment implements ListenFromActivity {
        public static final String ARG_NUMBER = "drawer_number";

        private String mSelectedMG;
        private RecyclerView mRecycler;
        private RealmResults<Exercise> mResults;
        private CompositeDisposable mDisposable = new CompositeDisposable();
        private ExerciseClient mExerciseClient;
        private ExerciseListRecyclerAdapter mAdapter;

        public ExerciseFragment() {
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_exercise_list, container, false);

            if (getActivity() != null) {
                ((ExerciseListView) getActivity()).setActivityListener(ExerciseFragment.this);
            }


            if (getArguments() != null && getActivity() != null) {
                this.mSelectedMG = MuscleGroup.getAllNames().get(getArguments().getInt(ARG_NUMBER));
                getActivity().setTitle(mSelectedMG);
            }

            mExerciseClient = new ExerciseClient(getContext());
            mResults = mRealmHelper.allStrengthExercisesByRating(Exercise.class, "musclegroup", mSelectedMG);

            mAdapter = new ExerciseListRecyclerAdapter(mResults, new ItemsListener(), mRealmHelper.getRealm());
            mRecycler = rootView.findViewById(R.id.lvItems);
            mRecycler.setHasFixedSize(true);

            final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecycler.getContext(),
                    layoutManager.getOrientation());
            mRecycler.addItemDecoration(dividerItemDecoration);
            mRecycler.setLayoutManager(layoutManager);

            mRecycler.setAdapter(mAdapter); //populate from realm
            AsyncTask.execute(this::populateFromDB);

            return rootView;
        }

        private void populateFromDB() {
            mDisposable.add(mExerciseClient.getExercisesByMG(MuscleGroup.getEnumFromName(mSelectedMG).getUrl())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            this::getExercisesSuccess,
                            this::getExercisesError
                    )
            );

        }

        private void getExercisesSuccess(List<Exercise> exercises) {
            mRealmHelper.deleteAllStrengthFiltered(Exercise.class, "musclegroup", mSelectedMG);
            exercises.sort(
                    Comparator.comparing(Exercise::getRating));
            mRealmHelper.insertAllFromList(exercises);
        }

        private void getExercisesError(Throwable throwable) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Utils.displayToast(getContext(), "Cached data is used");
                    mAdapter.notifyDataSetChanged();
                });
            }
        }


        private void reinitializeAdapter() {
            mResults = mRealmHelper.allStrengthExercisesByRating(Exercise.class, "musclegroup", mSelectedMG);
            mAdapter = new ExerciseListRecyclerAdapter(mResults, new ItemsListener(), mRealmHelper.getRealm());
            mRecycler.setAdapter(mAdapter);
        }

        @Override
        public void filterResultsInFragment(String text) {
            mAdapter.filterStrengthExercises(text, mSelectedMG);
        }

        @Override
        public void clearSearch() {
            reinitializeAdapter();
        }


        class ItemsListener implements AdapterView.OnClickListener {
            @Override
            public void onClick(View view) {
                try {
                    Exercise exercise = mAdapter.getItem(mRecycler.getChildAdapterPosition(view));
                    Intent intent = new Intent(getContext(), ExerciseView.class);
                    if (exercise != null)
                        intent.putExtra("exercise_name", exercise.getName());
                    startActivity(intent);

                } catch (ArrayIndexOutOfBoundsException ex) {

                }
            }
        }


    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }


}