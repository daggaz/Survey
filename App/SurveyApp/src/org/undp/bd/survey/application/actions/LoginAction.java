package org.undp.bd.survey.application.actions;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.api.API;
import org.undp.bd.survey.application.api.APIError;
import org.undp.bd.survey.application.api.APIListener;
import org.undp.bd.survey.application.api.APIError.Reason;
import org.undp.bd.survey.application.data.ApplicationData;
import org.undp.bd.survey.application.data.DatabaseContext;
import org.undp.bd.survey.application.data.DatabaseHelper;
import org.undp.bd.survey.application.data.User;

import android.content.Context;
import android.util.Log;

public abstract class LoginAction {
	public enum FailureType {
		ONLINE_LOGIN_FAILED,
		OFFLINE_LOGIN_FAILED,
		OFFLINE_LOGIN_INVALID,
		OFFLINE_LOGIN_UNAVAILABLE,
		ONLINE_LOGIN_ERROR,
	}
	
	private User offlineUser;
	private String username;
	private String password;
	private Context context;
	private DatabaseHelper database;
	
	public LoginAction(DatabaseContext db, String username, String password) {
		this.context = db.getContext();
		this.database = db.getHelper();
		this.username = username;
		this.password = password;
	}
	
	protected abstract void onSuccess();
	protected abstract void onFailure(FailureType failure);
	private class LoginTask extends APITask {

		public LoginTask() {
			super(context);
		}

		@Override
		protected void run(APIListener listener) {
			API.login(getContext(), listener, username, password);	
		}
		
		@Override
		protected String getProgressMessage() {
			return context.getResources().getString(R.string.logging_in);
		}
		
		@Override
		public void onSuccess(JSONObject data) {
			String session_key = null;
			try {
				session_key = data.getString("session_key");
			} catch (JSONException e) {
				onFailure(FailureType.ONLINE_LOGIN_ERROR);
				return;
			}
			
			if (offlineUser != null) {
				if (!offlineUser.authenticated)
					offlineUser.authenticated = true;
				offlineUser.session_key = session_key;
				database.getUsers().update(offlineUser);
				completeLogin(offlineUser, password);
			} else {
				User newUser = new User();
				newUser.username = username;
				newUser.passwordHash = password.hashCode();
				newUser.authenticated = true;
				newUser.session_key = session_key;
				database.getUsers().create(newUser);
				newUser = database.getUsers().queryForId(newUser.id);
				completeLogin(newUser, password);
			}
		}
		
		@Override
		public void onError(APIError error) {
			Log.d("LoginTask", error.toString());
			if (error.getReason() == Reason.FAILED_RESPONSE) {
				if (offlineUser != null && offlineUser.authenticated) {
					offlineUser.authenticated = false;
					database.getUsers().update(offlineUser);
				}
				onFailure(FailureType.ONLINE_LOGIN_FAILED);
			} else
				onFailure(FailureType.ONLINE_LOGIN_ERROR);		
		}
	}

	public void execute() {
		try {
			offlineUser = database.getUsers().queryBuilder().where().eq("username", username).queryForFirst();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
    	
    	if (ApplicationData.isConnected(context)) {
    		new LoginTask().execute();
    	} else if (offlineUser != null) {
    		if (offlineUser.authenticated) { 
	    		if (offlineUser.passwordHash == password.hashCode())
	    			completeLogin(offlineUser, password);
	    		else
	    			onFailure(FailureType.OFFLINE_LOGIN_FAILED);
    		} else
    			onFailure(FailureType.OFFLINE_LOGIN_INVALID);
    	} else {
    		onFailure(FailureType.OFFLINE_LOGIN_UNAVAILABLE);
    	}
	}

	private void completeLogin(User user, String password) {
    	user.password = password;
    	ApplicationData.instance().setUser(user);
    	onSuccess();
	}
}
