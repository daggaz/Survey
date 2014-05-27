package org.undp.bd.survey.application.actions;

import org.undp.bd.survey.application.api.APIListener;

import android.content.Context;

abstract class APITask extends ProgressTask<Object, Integer, APIListenerProxy> implements APIListener {
	public APITask(Context context) {
		super(context);
	}
	
	public void execute() {
		super.execute(new Object());
	}

	protected abstract void run(APIListener listener);
	
	@Override
	protected APIListenerProxy doInBackground(Object... params) {
		APIListenerProxy proxy = new APIListenerProxy();
		run(proxy);
		return proxy;
	}
	
	@Override
	protected void onPostExecute(APIListenerProxy result) {
		super.onPostExecute(result);
		result.dispatch(this);
	}
}
