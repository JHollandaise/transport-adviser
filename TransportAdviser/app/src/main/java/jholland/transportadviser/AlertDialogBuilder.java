package jholland.transportadviser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

// SimpleAlertDialog.displayWithOK(this, "Hello", "Title");
public class AlertDialogBuilder {
    public static void displayWithOK(Context c, String message) {
        displayWithOK(c, message, "ouh");
    }

    public static void displayWithOK(Context c, String message, String title) {
        final AlertDialog alertDialog = new AlertDialog.Builder(c).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting OK Button
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "flag", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.hide();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}