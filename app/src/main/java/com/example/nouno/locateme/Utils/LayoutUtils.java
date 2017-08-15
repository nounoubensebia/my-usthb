package com.example.nouno.locateme.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by nouno on 15/08/2017.
 */

public class LayoutUtils {

    public static float dpToPixel (Context context, float dp)
    {
        Resources resources = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return px;
    }
}
