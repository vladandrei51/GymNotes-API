package com.example.vlada.licenta.Views;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Domain.MuscleGroup;
import com.example.vlada.licenta.Net.Client.ExerciseClient;
import com.example.vlada.licenta.R;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by andrei-valentin.vlad on 2/7/2018.
 */

public class ExercisesFragment extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private List<String> muscleGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        mTitle = mDrawerTitle = getTitle();
        muscleGroups = MuscleGroup.getAllNames();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, muscleGroups));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(muscleGroups.get(position));
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

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

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class PlanetFragment extends Fragment {
        public static final String ARG_NUMBER = "drawer_number";

        private CompositeDisposable disposables = new CompositeDisposable();
        private ExerciseClient exerciseClient;

        private Realm realm;
        private RealmResults<Exercise> results;
        private RealmBaseAdapter<Exercise> adapter;
        private String selectedMG;


        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_exercise, container, false);
            int i = getArguments().getInt(ARG_NUMBER);
            this.selectedMG = MuscleGroup.getAllNames().get(i);
            exerciseClient = new ExerciseClient(getContext());

            this.realm = Realm.getDefaultInstance();
            this.results = realm.where(Exercise.class).contains("musclegroup", selectedMG, Case.INSENSITIVE).findAllAsync();

            this.adapter = new RealmBaseAdapter<Exercise>(results) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    PlanetFragment.ViewHolder viewHolder;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.list_view, parent, false);
                        viewHolder = new PlanetFragment.ViewHolder();
                        viewHolder.exerciseName = convertView.findViewById(R.id.label);
                        convertView.setTag(viewHolder);
                    } else {
                        viewHolder = (PlanetFragment.ViewHolder) convertView.getTag();
                    }

                    if (adapterData != null) {
                        final Exercise item = adapterData.get(position);
                        viewHolder.exerciseName.setText(item.getName());
                    }
                    return convertView;

                }
            };

            getActivity().setTitle(selectedMG);
            populateExerciseList();

            ListView lvItems = rootView.findViewById(R.id.lvItems);
            lvItems.setAdapter(adapter);

            lvItems.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
                Exercise clickedExercise = (Exercise) lvItems.getItemAtPosition(pos);
                displayToast(clickedExercise.getName());
                return true;
            });

            return rootView;
        }

        void populateExerciseList() {
            disposables.add(exerciseClient.getExercisesByMG(MuscleGroup.getEnumFromName(selectedMG).getUrl())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            this::getExercisesSuccess,
                            this::getExercisesError
                    )
            );
        }

        private void getExercisesError(Throwable throwable) {
            getActivity().runOnUiThread(() -> {
                displayToast("Could not connect to the server, cached data is being used");
            });
        }

        private void getExercisesSuccess(List<Exercise> exercises) {
            try (Realm r = Realm.getDefaultInstance()) {
                r.executeTransaction(realm -> {
                    realm.where(Exercise.class).contains("musclegroup", selectedMG, Case.INSENSITIVE).findAll()
                            .deleteAllFromRealm();
                    for (Exercise exercise : exercises) {
                        realm.insertOrUpdate(exercise);
                    }
                });
            }
        }

        private void displayToast(String message) {
            Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
            toast.show();
        }


        private static class ViewHolder {
            TextView exerciseName;
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}