package com.example.vlada.licenta.domain;

public enum MuscleGroup {

    NECK("neck"),
    TRAPS("traps"),
    SHOULDERS("shoulders"),
    CHEST("chest"),
    BICEPS("biceps"),
    FOREARMS("forearms"),
    ABS("abdominals"),
    QUADS("quadriceps"),
    CALVES("calves"),
    TRICEPS("triceps"),
    LATS("lats"),
    MIDDLE_BACK("middle-back"),
    LOWER_BACK("lower-back"),
    GLUTES("glutes"),
    HAMSTRINGS("hamstrings");

    String url;
    String name;
    String foundName;

    public void setFoundName(String foundName) {
        this.foundName = foundName;
    }

    public String getUrl() {
        return url;
    }

    public String getName(){
        return name;
    }

    private MuscleGroup (String url){
        this.url = "https://www.bodybuilding.com/exercises/muscle/" + url;
    }

}
