package hu.beesmarter.bsmart.fontrecognizer.analyzer;

import android.support.annotation.NonNull;

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
}
