package hu.beesmarter.bsmart.fontrecognizer.analyzer;

/**
 * Font object.
 */
public class FontObject {

	private String fontName;

	private String fileName;

	public FontObject(String fontName, String fileName) {
		this.fontName = fontName;
		this.fileName = fileName;
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


}
