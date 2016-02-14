package hu.beesmarter.bsmart.fontrecognizer.ui.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * A custom {@code DialogFragment} that shows an indeterminate,
 * not cancelable {@code ProgressDialog}. The dialog's message can
 * be customized.
 */
public class ProgressDialogFragment extends BaseDialogFragment {

    private static final String KEY_MESSAGE = "message";

    /**
     * Creates a {@code ProgressDialogFragment} that will show the given message.
     *
     * @param message the message to show
     * @return the new {@code ProgressDialogFragment}
     *
     * @throws NullPointerException if the message is null
     */
    public static ProgressDialogFragment newInstance(String message) {
        if ( message == null ) {
            throw new NullPointerException("You must supply a message!");
        }

        ProgressDialogFragment result = new ProgressDialogFragment();

        Bundle arguments = new Bundle();
        arguments.putString(KEY_MESSAGE, message);
        result.setArguments(arguments);

        return result;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString(KEY_MESSAGE);

        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIndeterminate(true);

        this.setCancelable(false);

        return dialog;
    }

}

