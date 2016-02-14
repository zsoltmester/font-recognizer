package hu.beesmarter.bsmart.fontrecognizer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

import hu.beesmarter.bsmart.fontrecognizer.analyzer.TessUtils;

/**
 * Utility functions for the {@link Bitmap}s.
 */
public class CameraUtils {

	private static final String TAG = CameraUtils.class.getName();

	public static final String TAKEN_IMAGE_PATH = TessUtils.TESSDATA_PARENT + "raw.jpeg";

	/**
	 * @return The path for the taken image.
	 */
	public static @NonNull Uri getTakenImageUri() {
		return Uri.fromFile(new File(TAKEN_IMAGE_PATH));
	}

	/**
	 * @return The last taken image.
	 */
	public static @Nullable Bitmap getSavedBitmapProcessed() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = ImageUtils.BITMAP_SAMPLE_SIZE;
		return ImageUtils.normalizeBitmapOrientation(
				BitmapFactory.decodeFile(TAKEN_IMAGE_PATH, options));
	}

	/**
	 * Saves the camera picture to memory.
	 */
	public static void saveCameraPicture(Context context, Bitmap bitmap, String name) {
		MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, name + ".jpg", "");
	}
}
