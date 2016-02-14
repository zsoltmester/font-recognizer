package hu.beesmarter.bsmart.fontrecognizer.analyzer.basepoint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.googlecode.leptonica.android.Box;
import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.ArrayList;
import java.util.List;

import hu.beesmarter.bsmart.fontrecognizer.analyzer.CharacterItem;
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

	@Override
	public @NonNull List<CharacterItem> getCharactersBitmap(@NonNull Bitmap image) {
		TessBaseAPI tess = TessUtils.tess;
		tess.setImage(image);

		String text = tess.getUTF8Text();
		Pixa pixa = tess.getConnectedComponents();
		int pixaNumber = pixa.size();
		if (pixaNumber < 1) {
			return new ArrayList<>();
		}

		List<Rect> rectList = new ArrayList<>();
		int rectNumber = 0;
		for (int i = 0; i < pixaNumber; i++) {
			Box box = pixa.getBox(i);
			Rect rect = box.getRect();
			if (rect != null) {
				rectList.add(rect);
				rectNumber++;
			}
			box.recycle();
		}
		pixa.recycle();

		List<CharacterItem> characterList = new ArrayList<>();
		for (int i = 0; i < rectNumber; i++) {
			Rect rect = rectList.get(i);
			Bitmap croppedImage = Bitmap.createBitmap(image, rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
			characterList.add(new CharacterItem(text.charAt(i), croppedImage, rect));
		}
		return characterList;
	}
}
