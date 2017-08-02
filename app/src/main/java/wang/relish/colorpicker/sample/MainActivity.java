package wang.relish.colorpicker.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


/**
 * @author Relish Wang
 * @since 2017/7/31
 */
public class MainActivity extends AppCompatActivity {


    private int mRed, mGreen, mBlue;

    private View mViewColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewColor = findViewById(R.id.view_color);

        mViewColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int color = mViewColor.getDrawingCacheBackgroundColor();

            }
        });
    }
}
