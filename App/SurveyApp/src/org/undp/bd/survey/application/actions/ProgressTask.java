package org.undp.bd.survey.application.actions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

abstract class ProgressTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	private ProgressDialog dialog;
	private Context context;
	
	public ProgressTask(Context context) {
		super();
		this.context = context;
	}

	public Context getContext() {
		return context;
	}
	
	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setMessage(getProgressMessage());
		dialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface arg0) {
                cancel(true);
            }
        });
		dialog.show();
	}
	
	@Override
	protected void onPostExecute(Result result) {
		dialog.dismiss();
	};
	
	protected abstract String getProgressMessage();
}
