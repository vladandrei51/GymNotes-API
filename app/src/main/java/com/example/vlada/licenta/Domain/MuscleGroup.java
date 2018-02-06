package com.example.vlada.licenta.Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum MuscleGroup {

    NECK("neck", "Neck"),
    TRAPS("traps", "Trapezius"),
    SHOULDERS("shoulders", "Shoulders"),
    CHEST("chest", "Chest"),
    BICEPS("biceps", "Biceps"),
    FOREARMS("forearms", "Forearms"),
    ABS("abdominals", "Abs"),
    QUADS("quadriceps", "Quads"),
    CALVES("calves", "Calves"),
    TRICEPS("triceps", "Triceps"),
    LATS("lats", "Lats"),
    MIDDLE_BACK("middle-back", "Middle Back"),
    LOWER_BACK("lower-back", "Lower Back"),
    GLUTES("glutes", "Glutes"),
    HAMSTRINGS("hamstrings", "Hamstrings");

    private static Map<String, MuscleGroup> nameEnumMap = new HashMap<>();
    private static Map<String, MuscleGroup> URLEnumMap = new HashMap<>();
    private static List<String> allNames = new ArrayList<>();

    static {
        for (MuscleGroup muscleGroup : MuscleGroup.values()) {
            nameEnumMap.put(muscleGroup.name, muscleGroup);
        }

        for (MuscleGroup muscleGroup1 : MuscleGroup.values()) {
            URLEnumMap.put(muscleGroup1.url, muscleGroup1);
        }
        for (MuscleGroup muscleGroup : MuscleGroup.values()) {
            allNames.add(muscleGroup.getName());
        }
    }

    String url;
    String name;


    MuscleGroup(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public static MuscleGroup getEnumFromName(String name) {
        return nameEnumMap.get(name);
    }

    public static MuscleGroup getEnumFromURL(String URL) {
        return URLEnumMap.get(URL);
    }

    public static List<String> getAllNames() {
        return allNames;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

}
