package hu.beesmarter.bsmart.fontrecognizer.analyzer;

import android.support.annotation.NonNull;

/**
 * Represents a font.
 */
public class Font {

	private String fontName;

	/**
	 * Construct a font. The name must be valid, what the server accepts.
	 *
	 * @param fontName The name of the font, which the server accepts.
	 */
	public Font(@NonNull String fontName) {
		this.fontName = fontName;
	}

	public @NonNull String getFontName() {
		return fontName;
	}

	@Override
	public int hashCode() {
		return fontName.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (o == this) {
			return true;
		}

		if (!(o instanceof Font)) {
			return false;
		}

		Font rhs = (Font) o;

		return rhs.fontName.equals(fontName);
	}
}
