package com.example.vlada.licenta;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.example.vlada.licenta.parser.HTMLParser;
import org.jsoup.nodes.Document;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private volatile HTMLParser htmlParser;

    private void initializeView(){
        textView = findViewById(R.id.textView); textView.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeView();


        htmlParser = new HTMLParser();
        System.out.println(htmlParser.findMuscleGroups());


    }
}
