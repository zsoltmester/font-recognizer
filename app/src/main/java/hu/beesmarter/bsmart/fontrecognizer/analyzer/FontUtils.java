package hu.beesmarter.bsmart.fontrecognizer.analyzer;

import java.util.Set;

/**
 * Utility method for the {@link Font}'s.
 */
public class FontUtils {

	/**
	 * The collection if the available fonts.
	 */
	public static Set<Font> fonts;

	static {
		fonts.add(new Font("Akiza Sans"));
		// TODO add the other fonts
	}
}
