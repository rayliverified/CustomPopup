package stream.custompopupsample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import stream.custompopup.CustomPopupWindow;
import stream.slimchart.SlimChart;
import stream.slimchart.Stat;

public class MainActivity extends AppCompatActivity {

    private CustomPopupWindow customPopupWindow;
    private SlimChart statScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customPopupWindow = new CustomPopupWindow(MainActivity.this);
        customPopupWindow.initLayout(R.layout.item_popup_layout);
        ViewGroup customPopupView = customPopupWindow.getLayout();
        TextView title = (TextView) customPopupView.findViewById(R.id.title);
        title.setText("Happy Days!");

        ArrayList<Keyword> KeywordList = new ArrayList<>();
        KeywordList.add(new Keyword("Sample", (float) 0.17));
        KeywordList.add(new Keyword("Keyword", (float) -0.48));
        KeywordList.add(new Keyword("VeryLongWord", (float) -0.83));
        KeywordList.add(new Keyword("Test", (float) 0.64));

        RecyclerView statRecyclerView = (RecyclerView) customPopupView.findViewById(R.id.stat_list);
        KeywordAdapter keywordAdapter = new KeywordAdapter(MainActivity.this, KeywordList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        statRecyclerView.setAdapter(keywordAdapter);
        statRecyclerView.setLayoutManager(layoutManager);

        statScore = (SlimChart) customPopupView.findViewById(R.id.stat_score);
        statScore.setText("32%");
        statScore.setStartAnimationDuration(1600);
        statScore.setStacked(true);
        ArrayList<Stat> statList = new ArrayList<Stat>();
        for (int i = 0; i < KeywordList.size(); i++)
        {
            statList.add(new Stat(Math.abs(KeywordList.get(i).getScore()), Color.HSVToColor(GetStatColor((int) (KeywordList.get(i).getScore()*100 + 100)/2))));
        }
        statScore.setStatList(statList);
        TextView statType = (TextView) customPopupView.findViewById(R.id.stat_type);
        statType.setText("POSITIVE");

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPopupWindow.showPopupWindow(v);
                statScore.playStartAnimation();
            }
        });
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPopupWindow.showPopupWindow(v);
                statScore.playStartAnimation();
            }
        });

        ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPopupWindow.showPopupWindow(v);
                statScore.playStartAnimation();
            }
        });

        ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPopupWindow.showPopupWindow(v);
                statScore.playStartAnimation();
            }
        });
    }

    public float[] GetStatColor(int hue)
    {
        float H = (float) ((float) hue * 1.3); // Hue (note 0.4 = Green, see huge chart below)
        float S = (float) 1; // Saturation
        float B = (float) 0.9; // Brightness

        return new float[]{H, S, B};
    }
}
