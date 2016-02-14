package hu.beesmarter.bsmart.fontrecognizer.analyzer;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * The main interface to communicate with a font recognizer algorithm.
 */
public interface FontRecognizer {

	/**
	 * It process the image and recognise the font.
	 *
	 * @param image The image.
	 * @return The recognised font.
	 */
	@NonNull Font recognizeFontFromImage(@NonNull byte[] image);

	/**
	 * It process the image and recognise the font.
	 *
	 * @param image The image.
	 * @return The recognised font.
	 */
	@NonNull Font recognizeFontFromImage(@NonNull Bitmap image);

	/**
	 * Gets the {@link CharacterItem} list for the bitmap.
	 *
	 * @param image the image.
	 * @return the character item list.
	 */
	@NonNull List<CharacterItem> getCharactersBitmap(@NonNull Bitmap image);
}
