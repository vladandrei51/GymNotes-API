package com.example.vlada.licenta.Utils;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

import com.example.vlada.licenta.Domain.Cardio;
import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by andrei-valentin.vlad on 2/9/2018.
 */

public class Utils {


    public static void displayToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showAlertDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .show();
    }


    public static double getEstimated1RM(Lift lift) {
        return (double) lift.getWeight() * Math.pow((double) lift.getReps(), 0.10f);
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    public static boolean is1RMCardio(Cardio cardio, RealmResults<Cardio> cardioRealmResults) {
        return cardioRealmResults.stream().map(Cardio::getTime_spent).max(Integer::compare).orElse(-1) == cardio.getTime_spent();
    }

    public static boolean isCardio(Exercise exercise, Realm realm) {
        return realm.where(Exercise.class).contains("name", exercise.getName())
                .and().not().contains("type", "Cardio", Case.INSENSITIVE)
                .and().not().contains("type", "Plyometrics", Case.INSENSITIVE)
                .and().not().contains("type", "Stretching", Case.INSENSITIVE).findAll().size() == 0;

    }

    public static boolean is1RM(Lift lift, RealmResults<Lift> lifts) {
        if (lift.getWeight() > 0 && lift.getReps() == 0)
            return false;
        if (lifts.stream().filter(l -> l.getReps() > 0).map(Lift::getWeight).max(Integer::compare).orElse(null) == 0) { //if there are only body-weight lifts
            return lifts.stream().map(Lift::getReps).max(Integer::compare).orElse(null) == lift.getReps();
        }
        int higher = 0;
        int same = 0;
        for (Lift aux : lifts) {
            if (getEstimated1RM(lift) > getEstimated1RM(aux)) {
                higher++;
            }
            if (getEstimated1RM(lift) == getEstimated1RM(aux)) {
                same++;
            }
        }
        return higher + same == lifts.size();
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static void addPlaceHolderLifts() {
        Lift lift = new Lift();
        Random rand = new Random();

        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransaction(realm -> {
                RealmResults<Exercise> exerciseList = realm.where(Exercise.class).contains("musclegroup", "Chest").findAll();
                for (Exercise exercise : exerciseList) {
                    lift.setExercise(exercise);
                    for (int i = 0; i < rand.nextInt(6) + 5; i++) {
                        LocalDate localDate = LocalDate.of(rand.nextInt(1) + 2017, Month.of(rand.nextInt(12) + 1), rand.nextInt(25) + 1);
                        lift.setSetDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        for (int j = 0; j <= rand.nextInt(8); j++) {
                            lift.setWeight(rand.nextInt(30));
                            lift.setReps(rand.nextInt(20));
                            realm.insertOrUpdate(lift);
                        }
                    }
                }
            });
        }

    }

    public static int get1RMofExercise(String exercise_name) {
        final double[] lift = new double[1];
        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransaction(realm -> {
                lift[0] = realm.where(Lift.class).equalTo("exercise_name", exercise_name).findAll().stream().map(Utils::getEstimated1RM).max(Double::compare).orElse(0d);
            });
        }
        return (int) lift[0];
    }

    public static String getStrengthLevel(boolean isMale, int bodyweight, String exercise_name, Context context) {
        List<Integer> bw = get_bw(isMale);
        List<Integer> novice = new ArrayList<>();
        List<Integer> intermediate = new ArrayList<>();
        List<Integer> advanced = new ArrayList<>();
        List<Integer> elite = new ArrayList<>();

        if (exercise_name.equals(context.getString(R.string.benchpress_strength_exercise))) {
            novice = bp_novice(isMale);
            intermediate = bp_intermediate(isMale);
            advanced = bp_advanced(isMale);
            elite = bp_elite(isMale);
        } else if (exercise_name.equals(context.getString(R.string.pull_strength_exercise))) {
            novice = dl_novice(isMale);
            intermediate = dl_intermediate(isMale);
            advanced = dl_advanced(isMale);
            elite = dl_elite(isMale);
        } else if (exercise_name.equals(context.getString(R.string.ohp_strength_exercise))) {
            novice = ohp_novice(isMale);
            intermediate = ohp_intermediate(isMale);
            advanced = ohp_advanced(isMale);
            elite = ohp_elite(isMale);
        } else if (exercise_name.equals(context.getString(R.string.squat_strength_exercise))) {
            novice = squat_novice(isMale);
            intermediate = squat_intermediate(isMale);
            advanced = squat_advanced(isMale);
            elite = ohp_elite(isMale);
        } else if (exercise_name.equals(context.getString(R.string.row_strength_exercise))) {
            novice = row_novice(isMale);
            intermediate = row_intermediate(isMale);
            advanced = row_advanced(isMale);
            elite = row_elite(isMale);
        }

        final double[] lift = new double[1];
        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransaction(realm -> {
                lift[0] = realm.where(Lift.class).contains("exercise_name", exercise_name).findAll().stream().map(Utils::getEstimated1RM).max(Double::compare).orElse(0d);
            });
        }
        if (lift[0] != 0d) {
            int index = bw.indexOf(bw.stream().filter(i -> i < bodyweight).max(Integer::compare).orElse(bw.get(0)));
            if (novice.get(index) > lift[0]) {
                return "Untrained";
            }
            if (intermediate.get(index) > lift[0]) {
                return "Novice";
            }
            if (advanced.get(index) > lift[0]) {
                return "Intermediate";
            }
            if (elite.get(index) > lift[0]) {
                return "Advanced";
            }
            return "Elite";
        } else return context.getString(R.string.exercise_not_performed_yet);
    }

    private static List<Integer> get_bw(boolean isMale) {
        if (isMale)
            return Arrays.asList(52, 56, 60, 67, 75, 82, 90, 100, 110, 125, 145, 200);
        else return Arrays.asList(44, 48, 52, 56, 60, 67, 75, 82, 90, 150);
    }

    private static List<Integer> bp_novice(boolean isMale) {
        if (isMale) return Arrays.asList(50, 53, 58, 65, 70, 75, 80, 83, 85, 88, 90, 93);
        return Arrays.asList(30, 33, 35, 38, 40, 40, 43, 50, 53, 55);
    }

    private static List<Integer> dl_novice(boolean isMale) {
        if (isMale) return Arrays.asList(83, 88, 95, 108, 115, 125, 133, 138, 145, 148, 153, 155);
        return Arrays.asList(48, 53, 55, 60, 63, 68, 73, 80, 88, 90);
    }

    private static List<Integer> ohp_novice(boolean isMale) {
        if (isMale) return Arrays.asList(33, 35, 38, 43, 45, 50, 53, 55, 58, 60, 60, 63);
        return Arrays.asList(18, 20, 23, 23, 25, 28, 30, 33, 35, 38);
    }

    private static List<Integer> squat_novice(boolean isMale) {
        if (isMale) return Arrays.asList(65, 70, 78, 85, 93, 100, 105, 110, 115, 118, 123, 125);
        return Arrays.asList(38, 40, 45, 48, 50, 55, 58, 63, 68, 73);
    }

    private static List<Integer> row_novice(boolean isMale) {
        if (isMale) return Arrays.asList(34, 39, 44, 49, 58, 63, 71, 79, 87, 97, 110, 115);
        return Arrays.asList(20, 22, 22, 23, 24, 26, 28, 29, 31, 33);
    }


    private static List<Integer> bp_intermediate(boolean isMale) {
        if (isMale) return Arrays.asList(60, 63, 70, 78, 85, 90, 98, 103, 105, 108, 113, 115);
        return Arrays.asList(35, 38, 38, 40, 43, 48, 53, 55, 60, 63);
    }

    private static List<Integer> dl_intermediate(boolean isMale) {
        if (isMale) return Arrays.asList(93, 100, 110, 123, 135, 143, 153, 160, 165, 170, 173, 178);
        return Arrays.asList(50, 60, 63, 68, 73, 80, 85, 93, 98, 105);
    }

    private static List<Integer> ohp_intermediate(boolean isMale) {
        if (isMale) return Arrays.asList(40, 45, 48, 55, 58, 63, 65, 70, 73, 75, 75, 78);
        return Arrays.asList(23, 25, 28, 28, 30, 33, 35, 38, 40, 43);
    }

    private static List<Integer> squat_intermediate(boolean isMale) {
        if (isMale) return Arrays.asList(80, 88, 93, 105, 113, 123, 130, 135, 140, 145, 148, 150);
        return Arrays.asList(45, 48, 53, 55, 60, 63, 68, 75, 80, 85);
    }

    private static List<Integer> row_intermediate(boolean isMale) {
        if (isMale) return Arrays.asList(51, 57, 63, 69, 80, 85, 95, 104, 117, 125, 137, 150);
        return Arrays.asList(33, 33, 35, 37, 38, 40, 43, 44, 47, 52);
    }


    private static List<Integer> bp_advanced(boolean isMale) {
        if (isMale) return Arrays.asList(83, 90, 95, 108, 115, 125, 133, 138, 143, 148, 153, 155);
        else return Arrays.asList(43, 45, 50, 53, 58, 63, 65, 73, 75, 80);
    }

    private static List<Integer> dl_advanced(boolean isMale) {
        if (isMale)
            return Arrays.asList(135, 145, 155, 173, 185, 200, 208, 218, 223, 228, 230, 233);
        else return Arrays.asList(80, 85, 90, 95, 100, 110, 118, 125, 130, 138);
    }

    private static List<Integer> ohp_advanced(boolean isMale) {
        if (isMale)
            return Arrays.asList(50, 53, 58, 63, 70, 75, 78, 83, 85, 88, 90, 93);
        else return Arrays.asList(30, 33, 35, 38, 40, 43, 48, 50, 53, 58);
    }

    private static List<Integer> squat_advanced(boolean isMale) {
        if (isMale)
            return Arrays.asList(108, 118, 128, 143, 155, 168, 178, 185, 193, 198, 203, 208);
        else return Arrays.asList(60, 65, 68, 73, 78, 85, 90, 98, 105, 110);
    }

    private static List<Integer> row_advanced(boolean isMale) {
        if (isMale) return Arrays.asList(72, 79, 86, 93, 105, 111, 123, 133, 143, 157, 169, 180);
        return Arrays.asList(49, 49, 51, 53, 55, 57, 61, 62, 66, 69);
    }

    private static List<Integer> bp_elite(boolean isMale) {
        if (isMale)
            return Arrays.asList(100, 110, 118, 133, 145, 158, 163, 173, 180, 185, 190, 193);
        else return Arrays.asList(53, 58, 63, 65, 68, 75, 85, 90, 95, 100);
    }

    private static List<Integer> dl_elite(boolean isMale) {
        if (isMale)
            return Arrays.asList(175, 188, 200, 218, 235, 250, 258, 265, 270, 273, 278, 280);
        else return Arrays.asList(105, 110, 115, 120, 125, 135, 145, 150, 160, 165);
    }

    private static List<Integer> ohp_elite(boolean isMale) {
        if (isMale)
            return Arrays.asList(60, 65, 70, 78, 85, 100, 105, 115, 120, 123, 125, 130);
        else return Arrays.asList(40, 43, 45, 48, 50, 55, 63, 65, 68, 73);
    }

    private static List<Integer> squat_elite(boolean isMale) {
        if (isMale)
            return Arrays.asList(145, 158, 168, 185, 203, 218, 230, 240, 250, 258, 263, 270);
        else return Arrays.asList(75, 80, 88, 90, 95, 105, 115, 123, 133, 138);
    }

    private static List<Integer> row_elite(boolean isMale) {
        if (isMale) return Arrays.asList(95, 103, 111, 119, 133, 140, 152, 164, 175, 190, 204, 215);
        return Arrays.asList(67, 67, 69, 72, 74, 77, 81, 83, 86, 90);
    }


}