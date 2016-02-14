package hu.beesmarter.bsmart.fontrecognizer.typeface;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.util.Pair;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import hu.beesmarter.bsmart.fontrecognizer.analyzer.Font;

/**
 * Type face manager for loading TypeFaces.
 */
public class TypeFaceManager {

    private static final String FONTS_PATH = "fonts";
    private static final String FILE_SEPARATOR = File.separator;

    private static final String FONT_NAME_TYPE_SEPARATOR = "-";
    private static final int FONT_FILE_EXTENSION_LENGTH = 4; // .ttf or .otf

    private static WeakReference<List<Pair<Typeface, Font>>> typefaces = new WeakReference<>(null);

    /**
     * Checks whether the typefaces are ready for use.
     *
     * @return true if they are ready, false otherwise.
     */
    public static boolean areTypefacesReady() {
        return typefaces.get() != null;
    }

    /**
     * Returns the typefaces. This method can return null, so check if typefaces are ready before using this.
     *
     * @return the typefaces or null.
     * @see TypeFaceManager#areTypefacesReady()
     */
    public static
    @Nullable
    List<Pair<Typeface, Font>> getTypefaces() {
        return typefaces.get();
    }

    /**
     * Loads the typefaces. On successful load, the listener's callback will be invoked. The callback
     * will be run on the UI thread.
     *
     * @param context  context.
     * @param listener listener.
     */
    @UiThread
    public static void loadTypeFaces(@NonNull Context context, @NonNull TypefaceLoadingListener listener) {
        if (areTypefacesReady()) {
            listener.typefacesLoaded(getTypefaces());
            return;
        }

        new LoadTypefacesTask(context, listener).execute();
    }

    public static List<Pair<Typeface, Font>> loadTypeFacesSync(@NonNull Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            String[] files = assetManager.list("fonts");
            List<Pair<Typeface, Font>> typefaces = new ArrayList<>();
            for (String file : files) {
                String fontName = file.split(FONT_NAME_TYPE_SEPARATOR)[0];
                typefaces.add(new Pair<>(Typeface.createFromAsset(assetManager, FONTS_PATH + FILE_SEPARATOR + file), new Font(fontName)));
            }
            TypeFaceManager.typefaces = new WeakReference<>(typefaces);
            return typefaces;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static class LoadTypefacesTask extends AsyncTask<Void, Void, List<Pair<Typeface, Font>>> {

        private Context context;
        private TypefaceLoadingListener typefaceLoadingListener;

        public LoadTypefacesTask(Context context, TypefaceLoadingListener typefaceLoadingListener) {
            this.context = context;
            this.typefaceLoadingListener = typefaceLoadingListener;
        }

        @Override
        protected List<Pair<Typeface, Font>> doInBackground(Void... params) {
            return loadTypeFacesSync(context);
        }

        @Override
        protected void onPostExecute(List<Pair<Typeface, Font>> pairs) {
            if (pairs == null) {
                typefaceLoadingListener.typefacesFailedToLoad();
            } else {
                TypeFaceManager.typefaces = new WeakReference<>(pairs);
                typefaceLoadingListener.typefacesLoaded(pairs);
            }
        }

    }


    /**
     * Listener for loading typefaces.
     */
    public interface TypefaceLoadingListener {

        /**
         * Typefaces loaded.
         *
         * @param typefaces the typefaces.
         */
        void typefacesLoaded(List<Pair<Typeface, Font>> typefaces);

        /**
         * The type faces could not be loaded.
         */
        void typefacesFailedToLoad();

    }


}
