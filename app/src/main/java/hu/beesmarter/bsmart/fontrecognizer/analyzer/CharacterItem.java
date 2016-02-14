package hu.beesmarter.bsmart.fontrecognizer.analyzer;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Recognized characters with the char name, its bitmap and the parameters.
 */
public class CharacterItem {

	private char character;
	private Bitmap bitmap;
	private Rect rect;

	public CharacterItem(char character, Bitmap bitmap, Rect rect) {
		this.character = character;
		this.bitmap = bitmap;
		this.rect = rect;
	}

	public char getCharacter() {
		return character;
	}

	public void setCharacter(char character) {
		this.character = character;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public Rect getRect() {
		return rect;
	}

	public void setRect(Rect rect) {
		this.rect = rect;
	}
}
