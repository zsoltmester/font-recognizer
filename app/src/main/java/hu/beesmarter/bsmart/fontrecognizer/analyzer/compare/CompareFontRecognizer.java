package hu.beesmarter.bsmart.fontrecognizer.analyzer.compare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;

import com.googlecode.leptonica.android.Box;
import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.ArrayList;
import java.util.List;

import hu.beesmarter.bsmart.fontrecognizer.analyzer.CharacterItem;
import hu.beesmarter.bsmart.fontrecognizer.analyzer.Font;
import hu.beesmarter.bsmart.fontrecognizer.analyzer.FontRecognizer;
import hu.beesmarter.bsmart.fontrecognizer.analyzer.TessUtils;
import hu.beesmarter.bsmart.fontrecognizer.util.ImageUtils;

/**
 * Font recognizer that is based on image comparison.
 */
public class CompareFontRecognizer implements FontRecognizer {

    private enum Strategy {
        EVERY_CHAR,
        FIRST_CHAR,
        LAST_CHAR
    }

    private static final Strategy STRATEGY = Strategy.EVERY_CHAR;

    private static final String LOG_TAG = CompareFontRecognizer.class.getSimpleName();

    private static final int CLEANING_THRESHOLD = 60;
    private static final ImageUtils.ColorDecider CLEANING_METHOD = ImageUtils.CLEAN_METHOD_2;
    private static final int GENERATE_TEXT_SIZE = 200;

    private List<Pair<Typeface, Font>> typefaces;

    public CompareFontRecognizer(List<Pair<Typeface, Font>> typefaces) {
        this.typefaces = typefaces;
    }

    @Override
    public
    @NonNull
    Font recognizeFontFromImage(@NonNull byte[] image) {
        return recognizeFontFromImage(BitmapFactory.decodeByteArray(image, 0, image.length));
    }

    @Override
    public
    @NonNull
    Font recognizeFontFromImage(@NonNull Bitmap image) {
        long startTime = System.currentTimeMillis();
        log("Recognition started at: " + startTime);
        Bitmap cleaned = cleanTheImage(image);
        image.recycle();
        List<CharacterItem> chars = getCharactersBitmap(cleaned);

        long leastDiff = Long.MAX_VALUE;
        long actualDiff;

        int bestIndex = -1;

        Typeface actualTypeface;
        for (int i = 0; i < typefaces.size(); i++) {
            actualTypeface = typefaces.get(i).first;

            actualDiff = 0;

            switch (STRATEGY) {
                case EVERY_CHAR: {
                    for(CharacterItem character: chars) {
                        Bitmap generated = getBitmapForCharacter(actualTypeface, character.getCharacter());
                        actualDiff += ImageUtils.getImageDifference(character.getBitmap(), generated);
                        generated.recycle();
                    }
                    break;
                }
                case FIRST_CHAR: {
                    Bitmap generated = getBitmapForCharacter(actualTypeface, chars.get(0).getCharacter());
                    actualDiff += ImageUtils.getImageDifference(chars.get(0).getBitmap(), generated);
                    generated.recycle();
                    break;
                }
                case LAST_CHAR: {
                    Bitmap generated = getBitmapForCharacter(actualTypeface, chars.get(chars.size() - 1).getCharacter());
                    actualDiff += ImageUtils.getImageDifference(chars.get(chars.size() - 1).getBitmap(), generated);
                    generated.recycle();
                    break;
                }
            }

            if (actualDiff < leastDiff) {
                leastDiff = actualDiff;
                bestIndex = i;
            }
        }

        if (bestIndex < 0) {
            Log.e(LOG_TAG, "FAILED TO FIND MATCH, CHOOSING FIRST!");
            bestIndex = 0;
        }

        long endTime = System.currentTimeMillis();
        log("Recognition ended at: " + endTime);
        log("Total duration: " + (endTime - startTime) / 1000L);
        cleaned.recycle();
        return typefaces.get(bestIndex).second;
    }


    @Override
    public
    @NonNull
    List<CharacterItem> getCharactersBitmap(@NonNull Bitmap image) {

        TessBaseAPI tess = TessUtils.tess;
        tess.setImage(image);

        String text = tess.getUTF8Text();
        Pixa pixa = tess.getConnectedComponents();
        int pixaNumber = pixa.size();
        if (pixaNumber < 1) {
            return new ArrayList<>();
        }

        List<Rect> rectList = new ArrayList<>();
        int rectNumber = 0;
        for (int i = 0; i < pixaNumber; i++) {
            Box box = pixa.getBox(i);
            Rect rect = box.getRect();
            if (rect != null) {
                rectList.add(rect);
                rectNumber++;
            }
        }

        List<CharacterItem> characterList = new ArrayList<>();
        for (int i = 0; i < rectNumber; i++) {
            Rect rect = rectList.get(i);
            Bitmap croppedImage = Bitmap.createBitmap(image, rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
            characterList.add(new CharacterItem(text.charAt(i), croppedImage, rect));
        }
        return characterList;
    }

    private static Bitmap cleanTheImage(Bitmap bitmap) {
        return ImageUtils.cleanImage(bitmap, CLEANING_THRESHOLD, CLEANING_METHOD);
    }

    private static void log(String msg) {
        Log.v(LOG_TAG, msg);
    }

    private static Bitmap getBitmapForCharacter(Typeface typeface, char character) {
        return ImageUtils.createBitmapForText(typeface, new char[]{ character}, GENERATE_TEXT_SIZE);
    }
}
