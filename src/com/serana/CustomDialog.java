package com.serana;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class CustomDialog {
	private static Dialog mDialog = null;

	public CustomDialog() {
		// do Nothing
	}

	public static void showProgressDialog(Context mContext, String text,
			boolean cancellable) {

		removeDialog();

		mDialog = new Dialog(mContext,
				android.R.style.Theme_Translucent_NoTitleBar);

		LayoutInflater mInflater = LayoutInflater.from(mContext);
		View layout = mInflater.inflate(R.layout.customdialog, null);
		mDialog.setContentView(layout);

		TextView mTextView = (TextView) layout.findViewById(R.id.text);

		if (text.equals(""))
			mTextView.setVisibility(View.GONE);
		else
			mTextView.setText(text);

		mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					return true;
				case KeyEvent.KEYCODE_SEARCH:
					return true;
				}
				return false;

			}
		});

		mDialog.setCancelable(false);
		try {

			mDialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void removeDialog() {
		try {
			if (mDialog != null)
				mDialog.dismiss();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static boolean notShowing() {
		if (mDialog != null) {
			boolean flag = mDialog.isShowing();
			return !flag;
		}
		return true;
	}
}