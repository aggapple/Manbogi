package com.aggapple.manbogi.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * @author djrain
 */
public final class IME {
    public static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * @param context
     * @param token   getCurrentFocus().getWindowToken()
     */
    public static void hideKeyboard(Context context, android.os.IBinder token) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * @param activity getActivity()
     */
    public static void hideKeyboard(Activity activity) {
        if (activity == null)
            return;

        if (activity.getCurrentFocus() == null)
            return;

        hideKeyboard(activity, activity.getCurrentFocus().getWindowToken());
    }
}
