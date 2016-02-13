package hu.beesmarter.bsmart.fontrecognizer.analyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alit on 13/02/2016.
 */
public class FontListGen {

	public List<FontObject> getFontList() {
		ArrayList<FontObject> fontObjects = new ArrayList<>();
		fontObjects.add(new FontObject("Akiza Sans", "AkizaSans"));
		fontObjects.add(new FontObject("Anonymus Pro", "AnonymusPro"));
		fontObjects.add(new FontObject("Autonym", "Automnym"));
		fontObjects.add(new FontObject("Averia Sans", "AveriaSans"));

		return fontObjects;
	}
}
