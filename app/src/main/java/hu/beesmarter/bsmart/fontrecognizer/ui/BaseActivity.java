package hu.beesmarter.bsmart.fontrecognizer.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import hu.beesmarter.bsmart.fontrecognizer.fontrecognizer.R;
import hu.beesmarter.bsmart.fontrecognizer.ui.dialog.AlertDialogFragment;
import hu.beesmarter.bsmart.fontrecognizer.util.PermissionUtils;

/**
 * Base class for our activities.
 */
public abstract class BaseActivity extends AppCompatActivity implements AlertDialogFragment.OkClickListener {

    protected static final String PERMISSION_ERROR_ALERT_DIALOG_TAG = "PERMISSION_ERROR_ALERT_DIALOG_TAG";

    protected static final String[] REQUIRED_PERMISSIONS = {
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (!PermissionUtils.areAllGranted(this, REQUIRED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 0);
        } else {
            onPermissionsGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!PermissionUtils.areAllGranted(this, REQUIRED_PERMISSIONS)) {
            // If we don't have every permissions, we cannot run!
            Fragment previousDialog = getSupportFragmentManager().findFragmentByTag(PERMISSION_ERROR_ALERT_DIALOG_TAG);
            if (previousDialog != null) {
                getSupportFragmentManager().beginTransaction().remove(previousDialog).commit();
                getSupportFragmentManager().executePendingTransactions();
            }

            AlertDialogFragment permissionErrorDialog = AlertDialogFragment.newInstance(R.string.permission_error_title, R.string.permission_error_message);
            permissionErrorDialog.setOkClickListener(this);
            permissionErrorDialog.showAllowingStateLoss(getSupportFragmentManager(), PERMISSION_ERROR_ALERT_DIALOG_TAG);
        } else {
            onPermissionsGranted();
        }
    }

    /**
     * Called after all of the permissions are granted. You should start "initialization" tasks that require runtime permissions in this method.
     */
    protected void onPermissionsGranted() {
    }

    @Override
    public void onOkClicked() {
        // Called when alert dialog about permission error is closed.
        finish();
    }
}
