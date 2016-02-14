package hu.beesmarter.bsmart.fontrecognizer.analyzer.compare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.googlecode.tesseract.android.TessBaseAPI;

import hu.beesmarter.bsmart.fontrecognizer.analyzer.Font;
import hu.beesmarter.bsmart.fontrecognizer.analyzer.FontRecognizer;
import hu.beesmarter.bsmart.fontrecognizer.analyzer.TessUtils;

/**
 * Font recognizer that is based on image comparison.
 */
public class CompareFontRecognizer implements FontRecognizer {

    @Override
    public @NonNull Font recognizeFontFromImage(@NonNull byte[] image) {
        return recognizeFontFromImage(BitmapFactory.decodeByteArray(image, 0, image.length));
    }

    @Override
    public @NonNull Font recognizeFontFromImage(@NonNull Bitmap image) {
        TessBaseAPI tess = TessUtils.tess;
        tess.setImage(image);
        String text = tess.getUTF8Text();
        // TODO: Tomi, help pls :D hogy szerzek ebből szöveget + pixeleket, amiket hasonlíthatnék?
        tess.clear();
        return new Font(text);
    }
}
