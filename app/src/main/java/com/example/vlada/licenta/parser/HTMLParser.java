package com.example.vlada.licenta.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class HTMLParser {

    public Document initializeDoc(){
        try {
            Document doc = Jsoup.connect("https://www.bodybuilding.com/exercises").get();
            return doc;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public ArrayList<String> findMuscleGroups(Document doc){
        ArrayList<String> muscleGroups = new ArrayList<>();
        Elements section = doc.select("a");
        if (section != null) {
            for (Element exercise : section) {
                if (exercise.hasText() && !muscleGroups.contains(exercise.text()) &&
                        exercise.attr("href").contains("exercises/muscle")) {
                    muscleGroups.add(exercise.text());//, exercise.attr("href"));
                }
            }
        }
        return muscleGroups;
    }
}
