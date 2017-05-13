package com.android.angrybird.util;

import java.util.List;

/**
 * Created by hp pc on 05-05-2017.
 */

public final class Utils {
    private Utils()
    {

    }

    public static boolean listNotNull(List<?> list)
    {
        return list != null && !list.isEmpty();
    }
}
