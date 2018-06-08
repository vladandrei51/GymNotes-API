package com.example.vlada.licenta.Net.Client;

import android.content.Context;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Net.DTOs.TokenDTO;
import com.example.vlada.licenta.R;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

interface ExerciseResource {
    String EXERCISE_PATH = "/api-1.0-SNAPSHOT/api/exercises";

    @GET(EXERCISE_PATH + "/all")
    Observable<List<Exercise>> getExercises();

    @GET(EXERCISE_PATH + "/get/musclegroup/{muscleGroup}")
    Observable<List<Exercise>> getExercisesByMG(@Path("muscleGroup") String muscleGroup);

    @GET(EXERCISE_PATH + "/get/name/{name}")
    Observable<Exercise> getExerciseByName(@Path("name") String name);

    @GET(EXERCISE_PATH + "/allNames")
    Observable<List<String>> getExercisesNames();

    @GET(EXERCISE_PATH + "/get/cardio")
    Observable<List<Exercise>> getCardioExercises();



    @GET(EXERCISE_PATH + "/get/id/{id}")
    Observable<Exercise> getExerciseByID(@Path("id") int id);

    @POST(EXERCISE_PATH + "/add")
    Observable<TokenDTO> addExercise(@Body Exercise exercise);

}

public class ExerciseClient {
    private ExerciseResource exerciseResource;
    private Context context;

    public ExerciseClient(Context context) {
        this.context = context;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_url))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.exerciseResource = retrofit.create(ExerciseResource.class);
    }

    public Observable<List<Exercise>> getExercises() {
        return exerciseResource.getExercises();
    }

    public Observable<List<String>> getExercisesNames() {
        return exerciseResource.getExercisesNames();
    }

    public Observable<List<Exercise>> getCardioExercises() {
        return exerciseResource.getCardioExercises();
    }

    public Observable<Exercise> getExerciseByName(String name) {
        return exerciseResource.getExerciseByName(name);
    }

    public Observable<Exercise> getExerciseByID(int id) {
        return exerciseResource.getExerciseByID(id);
    }

    public Observable<TokenDTO> addExercise(Exercise exercise) {
        return exerciseResource.addExercise(exercise);
    }

    public Observable<List<Exercise>> getExercisesByMG(String muscleGroup) {
        return exerciseResource.getExercisesByMG(muscleGroup);
    }

}
