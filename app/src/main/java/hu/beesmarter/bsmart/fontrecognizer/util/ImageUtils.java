package hu.beesmarter.bsmart.fontrecognizer.util;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Image processing utility methods.
 */
public class ImageUtils {

    public static int MIN_RGB = 0;
    public static int MAX_RGB = 255;

    /**
     * Cleans the image.
     *
     * @param bitmap bitmap to clean.
     * @param threshold threshold for choosing final color of pixel, should be between 0-255
     * @return the cleaned image.
     */
    public static Bitmap cleanImage(Bitmap bitmap, int threshold) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int red, green, blue;
        int pixel;
        int colorSum, finalColor;
        for(int i = 0; i < pixels.length; i++) {
            pixel = pixels[i];
            colorSum = Color.red(pixel) + Color.green(pixel) + Color.blue(pixel);
            finalColor = colorSum <= 3 * threshold ? MIN_RGB : MAX_RGB;
            pixels[i] = Color.argb(255, finalColor, finalColor, finalColor);
        }
        return Bitmap.createBitmap(pixels, width, height, bitmap.getConfig());
    }
}
