package hu.beesmarter.bsmart.fontrecognizer.analyzer;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utils for the tesseract lib.
 */
public class TessUtils {

	private static final String TAG = TessUtils.class.getName();

	/**
	 * The parent directory of the 'tessdata' directory, which is necessary for the initialization.
	 */
	public static final String TESSDATA_PARENT = Environment
			.getExternalStorageDirectory().toString() + "/BSmartFontRecognizer/";

	/**
	 * The only downloaded language in the 'tessdata'.
	 */
	public static final String TESSDATA_LANGUAGE = "eng";

	/**
	 * The tesseract object. Please initialize is it first whith the {@link #initTessdata(Context)}.
	 */
	public static TessBaseAPI tess = new TessBaseAPI();

	/**
	 * Initialize the 'tessdata' directory. After this method the {@link #TESSDATA_PARENT} dir will
	 * be prepared use as a 'tessdata' parent.
	 */
	public static void initTessdata(@NonNull Context context) {
		String tessdataPath = TESSDATA_PARENT + "tessdata/";
		File tessdata = new File(tessdataPath);
		if (!tessdata.exists() && !tessdata.mkdirs()) {
			Log.e(TAG, "Unable to create: " + tessdataPath);
		}

		String traineddataPath = TESSDATA_PARENT + "tessdata/" + TESSDATA_LANGUAGE + ".traineddata";
		if (!(new File(traineddataPath)).exists()) {
			try {
				InputStream in = context.getAssets()
						.open("tessdata/" + TESSDATA_LANGUAGE + ".traineddata");
				OutputStream out = new FileOutputStream(traineddataPath);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			} catch (IOException e) {
				Log.e(TAG, "Unable to copy traineddata.");
			}
		}

		tess = new TessBaseAPI();
		tess.init(TessUtils.TESSDATA_PARENT, TessUtils.TESSDATA_LANGUAGE);
	}
}
