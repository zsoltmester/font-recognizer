package hu.beesmarter.bsmart.fontrecognizer.ui.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;

import hu.beesmarter.bsmart.fontrecognizer.fontrecognizer.R;
import hu.beesmarter.bsmart.fontrecognizer.ui.BaseActivity;
import hu.beesmarter.bsmart.fontrecognizer.util.AndroidUtils;
import hu.beesmarter.bsmart.fontrecognizer.util.ImageUtils;

/**
 * Test screen for the image cleaning function.
 */
public class ImageCleaningTestActivity extends BaseActivity {

    private static final String STATE_THRESHOLD = "STATE_THRESHOLD";

    private static final int REQUEST_CODE_CAMERA = 9;

    private EditText thresholdEditText;
    private Button cameraButton;
    private ImageView resultImageView;

    private int threshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_image_cleaning);

        thresholdEditText = (EditText) findViewById(R.id.threshold_edittext);
        cameraButton = (Button) findViewById(R.id.camera_button);
        resultImageView = (ImageView) findViewById(R.id.result_image);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    threshold = getThresholdFromEditText();
                    AndroidUtils.startCamera(ImageCleaningTestActivity.this, REQUEST_CODE_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (savedInstanceState != null) {
            threshold = savedInstanceState.getInt(STATE_THRESHOLD, 0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_THRESHOLD, threshold);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap capturedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(AndroidUtils.getImageTempFile(this)));
                resultImageView.setImageBitmap(ImageUtils.cleanImage(capturedImage, threshold));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private int getThresholdFromEditText() throws Exception {
        if (thresholdEditText.getText() != null) {
            String thresholdText = thresholdEditText.getText().toString();
            int result = Integer.parseInt(thresholdText);
            if (result >= 0 && result <= 255) {
                return result;
            }
        }

        throw new Exception("Failed to read threshold!");
    }
}
