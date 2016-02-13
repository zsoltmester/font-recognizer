package hu.beesmarter.bsmart.fontrecognizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import hu.beesmarter.bsmart.fontrecognizer.analyzer.TessUtils;

/**
 * Utility functions for the {@link Bitmap}s.
 */
public class CameraUtils {

	private static final String TAG = CameraUtils.class.getName();

	private static final String TAKEN_IMAGE_PATH = TessUtils.TESSDATA_PARENT + "raw.jpeg";

	/**
	 * @return The path for the taken image.
	 */
	public static @NonNull Uri getTakenImageUri() {
		return Uri.fromFile(new File(TAKEN_IMAGE_PATH));
	}

	/**
	 * @return The last taken image.
	 */
	public static @Nullable Bitmap getSavedBitmapNormalized() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8; // TODO ezt egy jó értékre állítani felbontástól függően
		return normalizeBitmapOrientation(BitmapFactory.decodeFile(TAKEN_IMAGE_PATH, options));
	}

	/**
	 * Normalize the orientation of the given bitmap.
	 *
	 * @param bitmap The bitmap to normalize the orientation.
	 * @return The normalized bitmap.
	 */
	private static @NonNull Bitmap normalizeBitmapOrientation(@NonNull Bitmap bitmap) {
		try {
			ExifInterface exif = new ExifInterface(TAKEN_IMAGE_PATH);
			int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			int rotate = 0;
			switch (exifOrientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
			}

			if (rotate != 0) {
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();

				Matrix m = new Matrix();
				m.preRotate(rotate);

				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, m, false);
			}

			return bitmap.copy(Bitmap.Config.ARGB_8888, true);
		} catch (IOException e) {
			Log.e(TAG, "Unable to change the orientation.");
			return bitmap;
		}
	}
}
