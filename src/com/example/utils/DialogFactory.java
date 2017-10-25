package com.example.utils;

import com.xidian.xalertdialog.R;

import cn.pedant.SweetAlert.SweetAlertDialog;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.CountDownTimer;

public class DialogFactory {

	public static DialogFactory mDialogFactory = null;
	private int i = -1;

	private DialogFactory() {
	}

	public static DialogFactory getDialogFactory() {
		if (mDialogFactory == null) {
			mDialogFactory = new DialogFactory();
		}
		return mDialogFactory;
	}

	public SweetAlertDialog getProgressDialog(final Context context) {
		final SweetAlertDialog pDialog = new SweetAlertDialog(context,
				SweetAlertDialog.PROGRESS_TYPE).setTitleText("Loading");
		pDialog.show();
		pDialog.setCancelable(false);
		pDialog.showCancelButton(true);
		new CountDownTimer(800 * 7, 800) {
			public void onTick(long millisUntilFinished) {
				// you can change the progress bar color by ProgressHelper every
				// 800 millis
				i++;
				switch (i) {
				case 0:
					pDialog.getProgressHelper().setBarColor(
							context.getResources().getColor(
									R.color.blue_btn_bg_color));
					break;
				case 1:
					pDialog.getProgressHelper().setBarColor(
							context.getResources().getColor(
									R.color.material_deep_teal_50));
					break;
				case 2:
					pDialog.getProgressHelper().setBarColor(
							context.getResources().getColor(
									R.color.success_stroke_color));
					break;
				case 3:
					pDialog.getProgressHelper().setBarColor(
							context.getResources().getColor(
									R.color.material_deep_teal_20));
					break;
				case 4:
					pDialog.getProgressHelper().setBarColor(
							context.getResources().getColor(
									R.color.material_blue_grey_80));
					break;
				case 5:
					pDialog.getProgressHelper().setBarColor(
							context.getResources().getColor(
									R.color.warning_stroke_color));
					break;
				case 6:
					pDialog.getProgressHelper().setBarColor(
							context.getResources().getColor(
									R.color.success_stroke_color));
					break;
				}
			}

			public void onFinish() {
				i = -1;
				pDialog.setTitleText("Success!").setConfirmText("OK")
						.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
			}
		}.start();
		return pDialog;

	}

}
