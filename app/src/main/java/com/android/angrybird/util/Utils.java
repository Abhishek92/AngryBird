package com.android.angrybird.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.List;

/**
 * Created by hp pc on 05-05-2017.
 */

public final class Utils {
    public static final String DEFAULT_DATE = "01-01-1901";
    private Utils()
    {

    }

    public static boolean listNotNull(List<?> list)
    {
        return list != null && !list.isEmpty();
    }

    public static void shareImage(Context context, String filePath) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(filePath));
        context.startActivity(Intent.createChooser(share, "Share Image"));
    }
}
