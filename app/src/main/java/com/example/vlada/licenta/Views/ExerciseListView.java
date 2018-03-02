package com.example.vlada.licenta.Views;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vlada.licenta.Base.BaseFragment;
import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Domain.MuscleGroup;
import com.example.vlada.licenta.Net.Client.ExerciseClient;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.RealmBackup;
import com.example.vlada.licenta.Utils.Utils;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;

/**
 * Created by andrei-valentin.vlad on 2/7/2018.
 */

public class ExerciseListView extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private List<String> mMuscleGroups;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list_view);


        mTitle = getTitle();
        mMuscleGroups = MuscleGroup.getAllNames();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, mMuscleGroups));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                /* host Activity */
                mDrawerLayout,              /* DrawerLayout object */
                R.string.drawer_open,       /* "open drawer" description for accessibility */
                R.string.drawer_close       /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(mTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            }

            public void onDrawerOpened(View drawerView) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.drawer_open);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
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
        return true;
    }


    private void selectItem(int position) {
        // update the main content by replacing fragments
//        Fragment fragment = new ExerciseFragment();
//        Bundle args = new Bundle();
//        args.putInt(ExerciseFragment.ARG_NUMBER, position);
//        fragment.setArguments(args);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

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
        setTitle(mMuscleGroups.get(position));
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTitle);
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
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public static class ExerciseFragment extends BaseFragment {
        public static final String ARG_NUMBER = "drawer_number";

        private CompositeDisposable mDisposable = new CompositeDisposable();
        private ExerciseClient mExerciseClient;

        private RealmBaseAdapter<Exercise> mAdapter;
        private String mSelectedMG;
        private ListView mListView;

        public ExerciseFragment() {
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_exercise_list, container, false);

            this.mSelectedMG = MuscleGroup.getAllNames().get(getArguments().getInt(ARG_NUMBER));
            getActivity().setTitle(mSelectedMG);

            mExerciseClient = new ExerciseClient(getContext());
            mListView = rootView.findViewById(R.id.lvItems);


            populateWithDataFromRealm();
            populateExerciseList();

            mListView.setAdapter(mAdapter);
            listListeners();

            return rootView;
        }

        void listListeners() {
            mListView.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
                Exercise clickedExercise = (Exercise) mListView.getItemAtPosition(pos);
                Utils.showAlertDialog(getContext(), clickedExercise.getName(), clickedExercise.toPrettyString());
                return true;
            });

            mListView.setOnItemClickListener((a, v, position, id) -> {

                Intent intent = new Intent(getContext(), ExerciseView.class);
                Exercise clickedExercise = (Exercise) mListView.getItemAtPosition(position);
                intent.putExtra("exercise_name", clickedExercise.getName());
                startActivity(intent);
            });

        }

        void populateWithDataFromRealm() {
            this.mAdapter = new RealmBaseAdapter<Exercise>(mRealmHelper.findAllAsyncFiltered(Exercise.class, "musclegroup", mSelectedMG)) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ExerciseFragment.ViewHolder viewHolder;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.exercise_list_view, parent, false);
                        viewHolder = new ExerciseFragment.ViewHolder();
                        viewHolder.exerciseName = convertView.findViewById(R.id.exercise_list_view_label);
                        convertView.setTag(viewHolder);
                    } else {
                        viewHolder = (ExerciseFragment.ViewHolder) convertView.getTag();
                    }

                    if (adapterData != null) {
                        final Exercise item = adapterData.get(position);
                        viewHolder.exerciseName.setText(item.getName());
                    }
                    return convertView;

                }
            };


        }

        void populateExerciseList() {
            mDisposable.add(mExerciseClient.getExercisesByMG(MuscleGroup.getEnumFromName(mSelectedMG).getUrl())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            this::getExercisesSuccess,
                            this::getExercisesError
                    )
            );
        }

        private void getExercisesError(Throwable throwable) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Utils.showAlertDialog(getContext(), "Cached data is being used", throwable.getMessage());
                    mAdapter.notifyDataSetChanged();
                });
            }
        }


        private void getExercisesSuccess(List<Exercise> exercises) {
            mRealmHelper.deleteAllFiltered(Exercise.class, "musclegroup", mSelectedMG);
            mRealmHelper.insertAllFromList(exercises);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
        }

        private static class ViewHolder {
            TextView exerciseName;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

}