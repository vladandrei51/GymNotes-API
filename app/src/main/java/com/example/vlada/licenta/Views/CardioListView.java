package com.example.vlada.licenta.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.RealmBackup;

import io.realm.Realm;

public class CardioListView extends AppCompatActivity {

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


    public static class CardioFragment extends BaseFragment {
        private RecyclerView recycler;

        public CardioFragment() {
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_cardio_list, container, false);

            if (getActivity() != null) getActivity().setTitle("Cardio");

            recycler = rootView.findViewById(R.id.recycler);
            recycler.setHasFixedSize(true);

            final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recycler.getContext(),
                    layoutManager.getOrientation());
            recycler.addItemDecoration(dividerItemDecoration);
            recycler.setLayoutManager(layoutManager);

            recycler.setAdapter(new ExerciseListRecyclerAdapter(mRealmHelper.allCardioExercisesByRating(Exercise.class), new ItemsListener()));


            return rootView;
        }

        class ItemsListener implements AdapterView.OnClickListener {
            @Override
            public void onClick(View view) {
                Exercise exercise = mRealmHelper.allCardioExercisesByRating(Exercise.class).get(recycler.getChildAdapterPosition(view));
                Intent intent = new Intent(getContext(), ExerciseView.class);
                intent.putExtra("exercise_name", exercise.getName());
                startActivity(intent);
            }
        }
    }


}

