package hu.beesmarter.bsmart.fontrecognizer.analyzer;

import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alit on 13/02/2016.
 */
public class FontListGen {

	public List<FontObject> getFontList(List<Typeface> typefaces) {
		ArrayList<FontObject> fontObjects = new ArrayList<>();
		fontObjects.add(new FontObject("Akiza Sans", "AkizaSans-Bold.ttf"));
		fontObjects.add(new FontObject("Akiza Sans", "AkizaSans-Italic.ttf"));
		fontObjects.add(new FontObject("Akiza Sans", "AkizaSans-Regular.ttf"));
		fontObjects.add(new FontObject("Anonymus Pro", "Anonymous Pro.ttf"));
		fontObjects.add(new FontObject("Anonymus Pro", "Anonymous Pro I.ttf"));
		fontObjects.add(new FontObject("Anonymus Pro", "Anonymous Pro B.ttf"));
		fontObjects.add(new FontObject("Autonym", "Automnym"));
		fontObjects.add(new FontObject("Averia Sans", "AveriaSans"));

		return fontObjects;
	}
}
