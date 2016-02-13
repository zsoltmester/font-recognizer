package hu.beesmarter.bsmart.fontrecognizer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;

import hu.beesmarter.bsmart.fontrecognizer.analyzer.TessUtils;

/**
 * Utility functions for the {@link Bitmap}s.
 */
public class CameraUtils {

	private static final String TAG = CameraUtils.class.getName();

	private static final String TAKEN_IMAGE_PATH = TessUtils.TESSDATA_PARENT + "raw.jpeg";

	private static final String STORED_IMAGE_PATH = TessUtils.TESSDATA_PARENT + "picture";

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

			return bitmap.copy(Bitmap.Config.ARGB_8888, true); // TODO esetleg más config javíthatna
		} catch (IOException e) {
			Log.e(TAG, "Unable to change the orientation.");
			return bitmap;
		}
	}

	/**
	 * Saves the camera picture to memory.
	 */
	public static void saveCameraPicture(Context context, Bitmap bitmap) {
		String imagePostfix = DateFormat.getDateTimeInstance().format(new Date());

		File file = new File(STORED_IMAGE_PATH + imagePostfix + ".jpg");
		if (!file.exists()) {
			file.mkdirs();
		}

		try {
			OutputStream outputStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
			outputStream.flush();
			outputStream.close();

			MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
		} catch (IOException e) {
			Toast.makeText(context, "Save Failed", Toast.LENGTH_SHORT).show();
		}
	}
}
