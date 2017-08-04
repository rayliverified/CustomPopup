package stream.custompopup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private CustomPopupWindow customPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        customPopupWindow = new CustomPopupWindow(MainActivity.this);
        customPopupWindow.initLayout(R.layout.item_popup_stat);
        ViewGroup customPopupView = customPopupWindow.getLayout();
        TextView title = (TextView) customPopupView.findViewById(R.id.title);
        title.setText("Happy Days!");

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPopupWindow.showPopupWindow(v);
            }
        });
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPopupWindow.showPopupWindow(v);
            }
        });

        ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPopupWindow.showPopupWindow(v);
            }
        });

        ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPopupWindow.showPopupWindow(v);
            }
        });
    }
}
