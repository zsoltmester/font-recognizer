package hu.beesmarter.bsmart.fontrecognizer.analyzer.basepoint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

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
		TessBaseAPI tess = TessUtils.tess;

		Bitmap img = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length);
		tess.setImage(img);
		String text = tess.getUTF8Text();

		tess.clear();
		return new Font(text);
	}
}
