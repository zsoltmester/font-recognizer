package hu.beesmarter.bsmart.fontrecognizer.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility class for Android specific tasks.
 */
public class AndroidUtils {

    private static final String CAMERA_IMAGE_FILE_NAME = "image.tmp";

    /**
     * Navigates to the requested Activity.
     *
     * @param context       context.
     * @param activityClass class of the Activity.
     */
    public static void navigateToActivity(@NonNull Context context, @NonNull Class<? extends AppCompatActivity> activityClass) {
        Intent intent = new Intent(context, activityClass);
        context.startActivity(intent);
    }


    /**
     * Starts the camera.
     *
     * @param activity    the Activity that requests the image.
     * @param requestCode request code for getting camera image.
     */
    public static void startCamera(@NonNull Activity activity, int requestCode) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createEmptyImageTempFile(activity)));
        activity.startActivityForResult(cameraIntent, requestCode);
    }

    /**
     * Starts a file chooser.
     *
     * @param activity    the Activity that requests the file choose.
     * @param requestCode request code for getting file.
     */
    public static void startFileChoosed(@NonNull Activity activity, int requestCode) {
        Intent fileChooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileChooserIntent.setType("file/*");
        activity.startActivityForResult(fileChooserIntent, requestCode);
    }

    /**
     * Creates a temp file for the images captured with camera.
     *
     * @param context context.
     * @return the temp file.
     */
    public static File getImageTempFile(Context context) {
        return new File(context.getFilesDir(), "image.tmp");
    }

    /**
     * Creates a temp file for the images captured with camera.
     *
     * @param context context.
     * @return the temp file.
     */
    private static File createEmptyImageTempFile(Context context) {
        File f = new File(context.getFilesDir(), CAMERA_IMAGE_FILE_NAME);
        f.delete();
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(CAMERA_IMAGE_FILE_NAME, Context.MODE_WORLD_WRITEABLE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return getImageTempFile(context);
    }


}
