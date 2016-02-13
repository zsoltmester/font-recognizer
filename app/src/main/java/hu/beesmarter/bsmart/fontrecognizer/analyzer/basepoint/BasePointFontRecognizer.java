package hu.beesmarter.bsmart.fontrecognizer.analyzer.basepoint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.googlecode.tesseract.android.TessBaseAPI;

import hu.beesmarter.bsmart.fontrecognizer.analyzer.Font;
import hu.beesmarter.bsmart.fontrecognizer.analyzer.FontRecognizer;
import hu.beesmarter.bsmart.fontrecognizer.analyzer.TessUtils;

/**
 * A {@link FontRecognizer} based on a 'base point' strategy.
 */
public class BasePointFontRecognizer implements FontRecognizer {

	private static final String TAG = BasePointFontRecognizer.class.getSimpleName();

	@Override
	public @NonNull Font recognizeFontFromImage(@NonNull byte[] rawImage) {
		return recognizeFontFromImage(BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length));
	}

	@Override
	public @NonNull Font recognizeFontFromImage(@NonNull Bitmap image) {
		TessBaseAPI tess = TessUtils.tess;
		tess.setImage(image);
		String text = tess.getUTF8Text();
		tess.clear();
		return new Font(text);
	}
}
