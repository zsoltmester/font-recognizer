package hu.beesmarter.bsmart.fontrecognizer.ui.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import hu.beesmarter.bsmart.fontrecognizer.fontrecognizer.R;
import hu.beesmarter.bsmart.fontrecognizer.util.AndroidUtils;

/**
 * Activity for testing image processing operations.
 */
public class ImageUtilTestActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_image_util_test);

        findViewById(R.id.btn_image_clean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.navigateToActivity(ImageUtilTestActivity.this, ImageCleaningTestActivity.class);
            }
        });
    }
}
