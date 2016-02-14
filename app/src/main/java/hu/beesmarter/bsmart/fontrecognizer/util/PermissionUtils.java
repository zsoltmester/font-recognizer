package hu.beesmarter.bsmart.fontrecognizer.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Utility class for permission related operations.
 */
public class PermissionUtils {

    /**
     * Checks that we have all of the permissions or not.
     *
     * @param context     the Context
     * @param permissions the permissions to check
     *
     * @return true if all of the permissions are granted
     *
     * @throws NullPointerException if Context is null
     */
    public static boolean areAllGranted(Context context, String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return true;
        }

        if (context == null) {
            throw new NullPointerException("You must supply a valid Context!");
        }

        for (String permission : permissions) {
            if (permission != null && ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }


}
