package hu.beesmarter.bsmart.fontrecognizer.ui.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import java.io.IOException;
import java.io.InputStream;

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
    private static final int REQUEST_CODE_FILE = 99;

    private EditText thresholdEditText;
    private Button cameraButton;
    private Button fileButton;
    private Button cleanButton;

    private RadioButton rbMethod1;
    private RadioButton rbMethod2;

    private ImageView resultImageView;

    private int threshold;

    private Bitmap originalImage;
    private Bitmap transformedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_image_cleaning);

        thresholdEditText = (EditText) findViewById(R.id.threshold_edittext);
        cameraButton = (Button) findViewById(R.id.camera_button);
        fileButton = (Button) findViewById(R.id.file_button);
        cleanButton = (Button) findViewById(R.id.clean_button);

        resultImageView = (ImageView) findViewById(R.id.result_image);

        rbMethod1 = (RadioButton) findViewById(R.id.rb_method1);
        rbMethod2 = (RadioButton) findViewById(R.id.rb_method2);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AndroidUtils.startCamera(ImageCleaningTestActivity.this, REQUEST_CODE_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AndroidUtils.startFileChoosed(ImageCleaningTestActivity.this, REQUEST_CODE_FILE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        cleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originalImage == null) {
                    return;
                }

                int threshold;

                try {
                    threshold = getThresholdFromEditText();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                ImageUtils.ColorDecider method = rbMethod1.isChecked() ? ImageUtils.CLEAN_METHOD_1 : ImageUtils.CLEAN_METHOD_2;

                recycleTransformedBitmap();

                transformedImage = ImageUtils.cleanImage(originalImage, threshold, method);
                resultImageView.setImageBitmap(transformedImage);
            }
        });


        if (savedInstanceState != null) {
            threshold = savedInstanceState.getInt(STATE_THRESHOLD, 0);
            thresholdEditText.setText("" + threshold);
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
                recycleBitmaps();
                originalImage = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(AndroidUtils.getImageTempFile(this)));
                resultImageView.setImageBitmap(originalImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CODE_FILE && resultCode == Activity.RESULT_OK) {
            InputStream is = null;
            try {
                recycleBitmaps();
                Uri fileUri = data.getData();
                is = getContentResolver().openInputStream(fileUri);
                originalImage = BitmapFactory.decodeStream(is);
                resultImageView.setImageBitmap(originalImage);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        recycleBitmaps();
    }

    private void recycleBitmaps() {
        resultImageView.setImageDrawable(null);
        recycleOriginalBitmap();
        recycleTransformedBitmap();
    }

    private void recycleOriginalBitmap() {
        if (originalImage != null) {
            originalImage.recycle();
            originalImage = null;
        }
    }

    private void recycleTransformedBitmap() {
        if (transformedImage != null) {
            transformedImage.recycle();
            transformedImage = null;
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
