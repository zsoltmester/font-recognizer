package hu.beesmarter.bsmart.fontrecognizer.ui.dialog;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;

import java.lang.reflect.Field;

/**
 * Base class for {@code DialogFragment}s that support
 * showing and dismissing with state loss.
 */
public class BaseDialogFragment extends AppCompatDialogFragment {

    private static Field DISMISSED;
    private static Field SHOWN_BY_ME;

    static {
        try {
            DISMISSED = DialogFragment.class.getDeclaredField("mDismissed");
            DISMISSED.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            DISMISSED = null;
        }

        try {
            SHOWN_BY_ME = DialogFragment.class.getDeclaredField("mShownByMe");
            SHOWN_BY_ME.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            SHOWN_BY_ME = null;
        }
    }

    public void showAllowingStateLoss(FragmentManager fm, String tag) {
        if (DISMISSED != null) {
            try {
                DISMISSED.setBoolean(this, false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (SHOWN_BY_ME != null) {
            try {
                SHOWN_BY_ME.setBoolean(this, true);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        fm.beginTransaction().add(this, tag).commitAllowingStateLoss();
    }

}
