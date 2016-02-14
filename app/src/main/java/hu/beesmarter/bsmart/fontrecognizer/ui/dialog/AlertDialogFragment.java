package hu.beesmarter.bsmart.fontrecognizer.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import hu.beesmarter.bsmart.fontrecognizer.fontrecognizer.R;

/**
 * A custom {@code DialogFragment} that shows a message and an OK button.
 * <p/>
 * The dialog's title and message can be customized.
 */
public class AlertDialogFragment extends BaseDialogFragment {
    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_TITLE_RESOURCE = "titleResource";
    private static final String KEY_MESSAGE_RESOURCE = "messageResource";

    /**
     * Listener for getting notified about the OK button click.
     */
    public interface OkClickListener {
        /**
         * Called when the OK button is clicked.
         */
        void onOkClicked();
    }

    /**
     * Creates an {@code AlertDialogFragment} that will show the given message
     * with a given title and has an OK button.
     *
     * @param titleResource   the title
     * @param messageResource the message to show
     * @return the new {@code AlertDialogFragment}
     */
    public static AlertDialogFragment newInstance(@StringRes int titleResource, @StringRes int messageResource) {
        AlertDialogFragment result = new AlertDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(KEY_TITLE_RESOURCE, titleResource);
        arguments.putInt(KEY_MESSAGE_RESOURCE, messageResource);
        result.setArguments(arguments);
        return result;
    }

    /**
     * Creates an {@code AlertDialogFragment} that will show the given message
     * with a given title and has an OK button.
     *
     * @param title  the title
     * @param message the message to show
     * @return the new {@code AlertDialogFragment}
     */
    public static AlertDialogFragment newInstance(String title, String message) {
        if (title == null) {
            throw new NullPointerException("AlertDialogFragment title is missing!");
        }

        if (message == null ) {
            throw new NullPointerException("AlertDialogFragment message is missing!");
        }

        AlertDialogFragment result = new AlertDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_TITLE, title);
        arguments.putString(KEY_MESSAGE, message);
        result.setArguments(arguments);
        return result;
    }

    private OkClickListener okClickListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(KEY_TITLE);
        String message = getArguments().getString(KEY_MESSAGE);
        int titleResource = getArguments().getInt(KEY_TITLE_RESOURCE);
        int messageResource = getArguments().getInt(KEY_MESSAGE_RESOURCE);

        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                synchronized (AlertDialogFragment.this) {
                    if (okClickListener != null) {
                        okClickListener.onOkClicked();
                    }
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (title != null && message != null) {
            builder.setTitle(title).setMessage(message);
        } else {
            builder.setTitle(titleResource).setMessage(messageResource);
        }
        builder.setPositiveButton(R.string.alert_dialog_ok_button, okListener);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * Sets the listener to be called when the dialog is closed with the OK button.
     *
     * @param okClickListener the new listener
     */
    public synchronized void setOkClickListener(OkClickListener okClickListener) {
        this.okClickListener = okClickListener;
    }

}
