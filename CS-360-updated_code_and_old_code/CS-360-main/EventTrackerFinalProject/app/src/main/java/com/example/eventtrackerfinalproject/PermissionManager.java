import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {
    private Context context;

    public PermissionManager(Context context) {
        this.context = context;
    }

    public void requestSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_SMS)) {
            new AlertDialog.Builder(context)
                    .setTitle("SMS messages")
                    .setMessage("You need to give permissions for reminders")
                    .setPositiveButton("OK", (dialogInterface, i) -> ActivityCompat.requestPermissions(
                            (Activity) context,
                            new String[]{Manifest.permission.READ_SMS},
                            Constants.SMS_PERMISSION_CODE))
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_SMS},
                    Constants.SMS_PERMISSION_CODE);
        }
    }

    public boolean hasSmsPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }
}
