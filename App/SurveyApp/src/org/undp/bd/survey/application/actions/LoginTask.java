package org.undp.bd.survey.application.actions;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.api.API;
import org.undp.bd.survey.application.api.APIError;
import org.undp.bd.survey.application.api.APIListener;
import org.undp.bd.survey.application.data.ApplicationData;
import org.undp.bd.survey.application.data.DatabaseContext;
import org.undp.bd.survey.application.data.User;

import android.util.Log;

public abstract class LoginTask extends AbstractTask {
	public enum FailureType {
		ONLINE_LOGIN_FAILED,
		OFFLINE_LOGIN_FAILED,
		OFFLINE_LOGIN_INVALID,
		OFFLINE_LOGIN_UNAVAILABLE,
	}
	
	private User offlineUser;
	private String username;
	private String password;
	
	public LoginTask(DatabaseContext db, String username, String password) {
		super(db);
		this.username = username;
		this.password = password;
	}
	
	protected abstract void onSuccess();
	protected abstract void onFailure(FailureType failure);
	
	@Override
	protected String getProgressMessage() {
		return getResources().getString(R.string.logging_in);
	}

	public void run() {
		try {
			
			offlineUser = getDatabase().getUsers().queryBuilder().where().eq("username", username).queryForFirst();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
    	
    	if (ApplicationData.isConnected(getContext())) {
    		API.login(getContext(), username, password, new APIListener() {
				
				@Override
				public void success(JSONObject data) {
					String session_key = null;
					try {
						session_key = data.getString("session_key");
					} catch (JSONException e) {
						invalidateUsersLogin();
					}
					
					if (offlineUser != null) {
						if (!offlineUser.authenticated)
							offlineUser.authenticated = true;
						offlineUser.session_key = session_key;
						getDatabase().getUsers().update(offlineUser);
						completeLogin(offlineUser, password);
					} else {
						User newUser = new User();
						newUser.username = username;
						newUser.passwordHash = password.hashCode();
						newUser.authenticated = true;
						newUser.session_key = session_key;
						getDatabase().getUsers().create(newUser);
						newUser = getDatabase().getUsers().queryForId(newUser.id);
						completeLogin(newUser, password);
					}
				}
				
				@Override
				public void failed(APIError error) {
					Log.d("LoginTask", error.toString());
					invalidateUsersLogin();
				}

				private void invalidateUsersLogin() {
					if (offlineUser != null && offlineUser.authenticated) {
						offlineUser.authenticated = false;
						getDatabase().getUsers().update(offlineUser);
						onFailure(FailureType.ONLINE_LOGIN_FAILED);
					}
				}
			});
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
