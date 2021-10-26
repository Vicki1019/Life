package com.example.life;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

/**
 * QR Code產生
 *
 * @author magiclen
 *
 */
public class  QRCodeEncoder {

    // -----類別常數-----
    private static final String ENCODE_DATA = "ENCODE_DATA";

    private static final String[] STRING_INSTALL_TITLE = new String[] { "Install Barcode Scanner", "安裝條碼掃描器" };
    private static final String[] STRING_INSTALL_CLOSE = new String[] { "Close", "關閉" };
    private static final String[] STRING_INSTALL_INSTALL = new String[] { "Install", "安裝" };
    private static final String[] STRING_INSTALL_MESSAGE = new String[] { "In order to see the QR code, you neet to install Easy Barcode Scanner first.", "為了能夠看到QR Code，您需要安裝簡易條碼掃描器。" };
    private static final String[] STRING_URL_EASY_BARCODE_SCANNER = new String[] { "https://play.google.com/store/apps/details?id=org.magiclen.barcodescanner" };

    // -----類別變數-----
    private static boolean chinese = isChinese();

    // -----類別方法-----
    private static boolean isChinese() {
        final Locale locale = Locale.getDefault();
        return locale.equals(Locale.CHINESE) || locale.equals(Locale.SIMPLIFIED_CHINESE) || locale.equals(Locale.TRADITIONAL_CHINESE);
    }

    private static String getString(final String[] string) {
        int index = chinese ? 1 : 0;
        if (index >= string.length) {
            index = 0;
        }
        return string[index];
    }

    public static boolean encode(final Activity activity, final String text) {
        final PackageManager packageManager = activity.getPackageManager();
        final Intent easyBarcodeScanner = new Intent("org.magiclen.barcodescanner.ENCODE");
        easyBarcodeScanner.setPackage("org.magiclen.barcodescanner");
        final List<ResolveInfo> list = packageManager.queryIntentActivities(easyBarcodeScanner, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            easyBarcodeScanner.putExtra(ENCODE_DATA, text);
            activity.startActivity(easyBarcodeScanner);
            return true;
        } else {
            new AlertDialog.Builder(activity).setTitle(getString(STRING_INSTALL_TITLE)).setMessage(getString(STRING_INSTALL_MESSAGE)).setPositiveButton(getString(STRING_INSTALL_INSTALL), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    final String url = getString(STRING_URL_EASY_BARCODE_SCANNER);
                    final Intent openURLIntent = new Intent(Intent.ACTION_VIEW);
                    openURLIntent.setData(Uri.parse(url));
                    final List<ResolveInfo> list = packageManager.queryIntentActivities(openURLIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (list.size() > 0) {
                        final Intent destIntent = Intent.createChooser(openURLIntent, null);
                        activity.startActivity(destIntent);
                    }
                }

            }).setNegativeButton(getString(STRING_INSTALL_CLOSE), null).show();
            return false;
        }
    }

}