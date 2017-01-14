package com.aggapple.manbogi.base;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class BaseActivity extends FragmentActivity {

	protected ArrayList<Dialog> mDialogs = new ArrayList<Dialog>();

	protected void clearDlgs() {
		final ArrayList<Dialog> dlgs = mDialogs;

		while (dlgs.size() > 0) {
			Dialog alertDialog = dlgs.remove(0);
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
				alertDialog = null;
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		clearDlgs();
	}

	public Dialog showDialog(Object message, Object positiveButtonText, DialogInterface.OnClickListener positiveListener, boolean cancelable) {
		return showDialog(null, null, message, null, positiveButtonText, positiveListener, null, null, null, null, cancelable);
	}

	public Dialog showDialog(Object message, //
			Object positiveButtonText, DialogInterface.OnClickListener positiveListener//
			, Object negativeButtonText, DialogInterface.OnClickListener negativeListener, boolean cancelable) {
		return showDialog(null, null, message, null, positiveButtonText, positiveListener, null, null, negativeButtonText, negativeListener, cancelable);
	}

	public Dialog showDialog(Object message, //
			Object positiveButtonText, DialogInterface.OnClickListener positiveListener//
			, Object neutralButtonText, DialogInterface.OnClickListener neutralListener//
			, Object negativeButtonText, DialogInterface.OnClickListener negativeListener//
			, boolean cancelable) {
		return showDialog(null, null, message, null, positiveButtonText, positiveListener, neutralButtonText, neutralListener, negativeButtonText, negativeListener, cancelable);
	}

	public Dialog showDialog(Object title, Object icon, Object message, View view//
	, Object positiveButtonText, DialogInterface.OnClickListener positiveListener//
	, Object neutralButtonText, DialogInterface.OnClickListener neutralListener //
	, Object negativeButtonText, DialogInterface.OnClickListener negativeListener//
	, boolean cancelable) {
		if (isFinishing())
			return null;
		final Dialog dlg = getDialog(title, icon, "\n" + message + "\n", view, positiveButtonText, positiveListener, neutralButtonText, neutralListener, negativeButtonText, negativeListener,
				cancelable);
		dlg.show();
		return dlg;
	}

	public Dialog getDialog(Object title, Object icon, Object message, View view//
	, Object positiveButtonText, DialogInterface.OnClickListener positiveListener//
	, Object neutralButtonText, DialogInterface.OnClickListener neutralListener //
	, Object negativeButtonText, DialogInterface.OnClickListener negativeListener//
	, boolean cancelable) {

		Context context = this;

		final Builder builder = new Builder(context);
		if (title != null)
			builder.setTitle(title instanceof String ? (String) title : context.getResources().getString((Integer) title));
		if (message != null)
			builder.setMessage(message instanceof String ? (String) message : context.getResources().getString((Integer) message));
		if (icon != null)
			builder.setIcon(icon instanceof Drawable ? (Drawable) icon : context.getResources().getDrawable((Integer) icon));
		if (view != null)
			builder.setView(view);
		if (positiveButtonText != null)
			builder.setPositiveButton(positiveButtonText instanceof String ? (String) positiveButtonText : context.getResources().getString((Integer) positiveButtonText), positiveListener);
		if (negativeButtonText != null)
			builder.setNegativeButton(negativeButtonText instanceof String ? (String) negativeButtonText : context.getResources().getString((Integer) negativeButtonText), negativeListener);
		if (neutralButtonText != null)
			builder.setNeutralButton(neutralButtonText instanceof String ? (String) neutralButtonText : context.getResources().getString((Integer) neutralButtonText), neutralListener);

		final AlertDialog dlg = builder.create();
		dlg.setCancelable(cancelable);

		mDialogs.add(dlg);

		return dlg;
	}

}
