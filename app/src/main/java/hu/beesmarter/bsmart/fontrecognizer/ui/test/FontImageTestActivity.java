package hu.beesmarter.bsmart.fontrecognizer.ui.test;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hu.beesmarter.bsmart.fontrecognizer.analyzer.Font;
import hu.beesmarter.bsmart.fontrecognizer.fontrecognizer.R;
import hu.beesmarter.bsmart.fontrecognizer.typeface.TypeFaceManager;
import hu.beesmarter.bsmart.fontrecognizer.ui.BaseActivity;
import hu.beesmarter.bsmart.fontrecognizer.ui.dialog.ProgressDialogFragment;
import hu.beesmarter.bsmart.fontrecognizer.util.ImageUtils;

/**
 * Test Activity for creating text images.
 */
public class FontImageTestActivity extends BaseActivity implements TypeFaceManager.TypefaceLoadingListener {

    private static final String PROGRESS_DIALOG_FRAGMENT_TAG = "PROGRESS_DIALOG_FRAGMENT_TAG";

    private static final String STATE_SELECTED_TYPEFACE_INDEX = "STATE_SELECTED_TYPEFACE_INDEX";

    private List<Pair<Typeface, Font>> typefaces;
    private String[] typefaceNames;

    private Button fontButton;
    private TextView fontTextView;
    private EditText textEditText;
    private EditText textSizeEditText;
    private Button generateButton;
    private ImageView resultImageView;

    private int selectedTypefaceIndex = -1;

    private Bitmap resultBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_activity_image_from_text);

        fontButton = (Button) findViewById(R.id.font_button);
        fontTextView = (TextView) findViewById(R.id.font_text);
        textEditText = (EditText) findViewById(R.id.text_edittext);
        textSizeEditText = (EditText) findViewById(R.id.textsize_edittext);
        generateButton = (Button) findViewById(R.id.generate_button);
        resultImageView = (ImageView) findViewById(R.id.result_image);

        fontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typefaces == null || typefaces == null) {
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(FontImageTestActivity.this);

                builder.setItems(typefaceNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setSelectedTypefaceIndex(which);
                    }
                });
                builder.create().show();
            }
        });

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typefaces == null || selectedTypefaceIndex < 0 || selectedTypefaceIndex >= typefaces.size()) {
                    return;
                }

                if (textEditText.getText() == null || textEditText.getText().toString().length() == 0) {
                    return;
                }

                int textSize;
                try {
                    textSize = getTextSizeFromTheEditText();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                Typeface typeface = typefaces.get(selectedTypefaceIndex).first;
                recycleResultBitmap();
                resultBitmap = ImageUtils.createBitmapForText(typeface, textEditText.getText().toString(), textSize);
                resultImageView.setImageBitmap(resultBitmap);
            }
        });

        if (savedInstanceState != null) {
            selectedTypefaceIndex = savedInstanceState.getInt(STATE_SELECTED_TYPEFACE_INDEX, -1);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        ProgressDialogFragment.newInstance(getString(R.string.typeface_loading_dialog)).show(getSupportFragmentManager(), PROGRESS_DIALOG_FRAGMENT_TAG);
        getSupportFragmentManager().executePendingTransactions();
        TypeFaceManager.loadTypeFaces(this, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedTypefaceIndex >= 0) {
            outState.putInt(STATE_SELECTED_TYPEFACE_INDEX, selectedTypefaceIndex);
        }
    }

    @Override
    public void typefacesLoaded(List<Pair<Typeface, Font>> typefaces) {
        hideProgressDialog();
        this.typefaces = typefaces;
        typefaceNames = new String[typefaces.size()];
        for (int i = 0; i < typefaces.size(); i++) {
            typefaceNames[i] = typefaces.get(i).second.getFontName();
        }
        setSelectedTypefaceIndex(selectedTypefaceIndex >= 0 ? selectedTypefaceIndex : 0);
    }

    @Override
    public void typefacesFailedToLoad() {
        hideProgressDialog();
        setSelectedTypefaceIndex(-1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycleResultBitmap();
    }

    private void hideProgressDialog() {
        Fragment progressDialogFragment = getSupportFragmentManager().findFragmentByTag(PROGRESS_DIALOG_FRAGMENT_TAG);
        if (progressDialogFragment != null) {
            ((ProgressDialogFragment) progressDialogFragment).dismissAllowingStateLoss();
        }
    }

    private void recycleResultBitmap() {
        resultImageView.setImageDrawable(null);
        if (resultBitmap != null) {
            resultBitmap.recycle();
            resultBitmap = null;
        }
    }

    private void setSelectedTypefaceIndex(int index) {
        if (typefaces == null) {
            selectedTypefaceIndex = -1;
            fontTextView.setText(null);
            return;
        }

        if (index < 0 || index >= typefaces.size()) {
            index = 0;
        }
        selectedTypefaceIndex = index;
        fontTextView.setText(typefaces.get(selectedTypefaceIndex).second.getFontName());
    }

    private int getTextSizeFromTheEditText() throws Exception{
        if (textSizeEditText.getText() != null) {
            String textSizeText = textSizeEditText.getText().toString();
            int result = Integer.parseInt(textSizeText);
            if (result > 0) {
                return result;
            }
        }

        throw new Exception("Failed to read text size!");
    }
}
