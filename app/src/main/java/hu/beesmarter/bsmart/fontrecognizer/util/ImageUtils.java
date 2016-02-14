package hu.beesmarter.bsmart.fontrecognizer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

/**
 * Image processing utility methods.
 */
public class ImageUtils {

    public interface ColorDecider {
        boolean isDark(@ColorInt int color, int threshold);
    }

    public static final ColorDecider CLEAN_METHOD_1 = new ColorDecider() {
        @Override
        public boolean isDark(@ColorInt int color, int threshold) {
            int colorSum = Color.red(color) + Color.green(color) + Color.blue(color);
            return colorSum <= 3 * threshold;
        }

    };

    public static final ColorDecider CLEAN_METHOD_2 = new ColorDecider() {
        @Override
        public boolean isDark(@ColorInt int color, int threshold) {
            float luminance = 0.2126f * Color.red(color) + 0.7152f * Color.green(color) + 0.0722f * Color.blue(color);
            return luminance <= 3 * threshold;
        }
    };

    private static final String TAG = ImageUtils.class.getName();

    public static final int MIN_RGB = 0;
    public static final int MAX_RGB = 255;

    public static final int THRESHOLD = 90;
    public static final int BITMAP_SAMPLE_SIZE = 8; // TODO ezt egy jó értékre állítani felbontástól függően

    public static final ColorDecider DEFAULT_METHOD = CLEAN_METHOD_1;

    /**
     * Cleans the image.
     *
     * @param bitmap bitmap to clean.
     * @param threshold threshold for choosing final color of pixel, should be between 0-255
     * @return the cleaned image.
     */
    public static Bitmap cleanImage(Bitmap bitmap, int threshold, @NonNull ColorDecider colorDecider) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int pixel;
        int finalColor;
        for(int i = 0; i < pixels.length; i++) {
            pixel = pixels[i];
            finalColor = colorDecider.isDark(pixel, threshold) ? MIN_RGB : MAX_RGB;
            pixels[i] = Color.argb(255, finalColor, finalColor, finalColor);
        }
        return Bitmap.createBitmap(pixels, width, height, bitmap.getConfig());
    }

	/**
	 * Process the image.
     */
    public static Bitmap processImage(byte[] rawImage) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = BITMAP_SAMPLE_SIZE;
        Bitmap image = ImageUtils.normalizeBitmapOrientation(
                BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options));
        return ImageUtils.cleanImage(image, ImageUtils.THRESHOLD, ImageUtils.DEFAULT_METHOD);
    }

    /**
     * Normalize the orientation of the given bitmap.
     *
     * @param bitmap The bitmap to normalize the orientation.
     * @return The normalized bitmap.
     */
    public static @NonNull Bitmap normalizeBitmapOrientation(@NonNull Bitmap bitmap) {
        try {
            ExifInterface exif = new ExifInterface(CameraUtils.TAKEN_IMAGE_PATH);
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
     * Calculates the difference between two images. The difference should be 0 for identical images, and it should be bigger for more different images.
     *
     * @param image1 the first image.
     * @param image2 the second image.
     * @return the calculated difference.
     */
    public static int getImageDifference(@NonNull Bitmap image1, @NonNull Bitmap image2)  {
        int w1 = image1.getWidth();
        int h1 = image1.getHeight();
        int w2 = image2.getWidth();
        int h2 = image2.getHeight();

        float scaleWidth = (float) w2 / w1;
        float scaleHeight = (float) h2 / h1;

        int[] pixels1 = new int[w1*h1];
        image1.getPixels(pixels1, 0, w1, 0, 0, w1, h1);
        int[] pixels2 = new int[w2*h2];
        image2.getPixels(pixels2, 0, w2, 0, 0, w2, h2);

        int sumDifference = 0;

        int x1,y1,x2,y2, j;

        for(int i = 0; i < pixels1.length; i++) {
            x1 = i % w1;
            y1 = i / w1;

            x2 = (int) (x1 * scaleWidth + 0.5f);
            y2 = (int) (y1 * scaleHeight + 0.5f);

            j = y2 * w2 + x2;

            sumDifference += Math.abs(pixels1[i] - pixels2[j]);
        }

        return sumDifference;
    }
}
