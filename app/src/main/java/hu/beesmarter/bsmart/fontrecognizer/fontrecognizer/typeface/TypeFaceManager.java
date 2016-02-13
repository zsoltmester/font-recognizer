package hu.beesmarter.bsmart.fontrecognizer.fontrecognizer.typeface;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Type face manager for loading TypeFaces.
 */
public class TypeFaceManager {

    private static final String FONTS_PATH = "fonts";
    private static final String FILE_SEPARATOR = File.separator;

    private static WeakReference<List<Typeface>> typefaces = new WeakReference<>(null);

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
    public static @Nullable List<Typeface> getTypefaces() {
        return typefaces.get();
    }

    /**
     * Loads the typefaces. On successful load, the listener's callback will be invoked. The callback
     * will be run on the UI thread.
     *
     * @param context context.
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

    private static class LoadTypefacesTask extends AsyncTask<Void, Void, List<Typeface>> {

        private Context context;
        private TypefaceLoadingListener typefaceLoadingListener;

        public LoadTypefacesTask(Context context, TypefaceLoadingListener typefaceLoadingListener) {
            this.context = context;
            this.typefaceLoadingListener = typefaceLoadingListener;
        }

        @Override
        protected List<Typeface> doInBackground(Void... params) {
            try {
                AssetManager assetManager = context.getAssets();
                String[] files = assetManager.list("fonts");
                List<Typeface> typefaces = new ArrayList<>();
                for(String file: files) {
                    typefaces.add(Typeface.createFromAsset(assetManager, FONTS_PATH + FILE_SEPARATOR + file));
                }
                return typefaces;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Typeface> typefaces) {
            if (typefaces == null) {
                typefaceLoadingListener.typefacesFailedToLoad();
            } else {
                TypeFaceManager.typefaces = new WeakReference<>(typefaces);
                typefaceLoadingListener.typefacesLoaded(typefaces);
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
        void typefacesLoaded(List<Typeface> typefaces);

        /**
         * The type faces could not be loaded.
         */
        void typefacesFailedToLoad();

    }


}
