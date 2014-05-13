package org.undp.bd.survey.application.data;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DatabaseMixin  {

	private DatabaseHelper databaseHelper = null;
	private Context context;
	
	public DatabaseMixin(Context context) {
		this.context = context;
	}

	public void destroy() {
	    if (databaseHelper != null) {
	        OpenHelperManager.releaseHelper();
	        databaseHelper = null;
	    }
	}

	public DatabaseHelper getHelper() {
	    if (databaseHelper == null)
	    	databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
	    return databaseHelper;
	}
}
