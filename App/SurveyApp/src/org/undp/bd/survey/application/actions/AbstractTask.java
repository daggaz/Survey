package org.undp.bd.survey.application.actions;

import org.undp.bd.survey.application.data.DatabaseContext;
import org.undp.bd.survey.application.data.DatabaseHelper;

import android.content.Context;
import android.content.res.Resources;

abstract class AbstractTask extends ProgressTask<Object, Integer, Object> {
	private DatabaseContext db;
	
	public AbstractTask(DatabaseContext db) {
		super(db.getContext());
		this.db = db;
	}
	
	public void execute() {
		super.execute(new Object());
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		run();
		return null;
	}
	
	protected abstract void run();
	
	public Context getContext() {
		return db.getContext();
	}
	
	public DatabaseHelper getDatabase() {
		return db.getHelper();
	}
	
	public Resources getResources() {
		return getContext().getResources();
	}
}
