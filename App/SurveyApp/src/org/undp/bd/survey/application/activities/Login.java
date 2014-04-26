package org.undp.bd.survey.application.activities;

import java.sql.SQLException;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;
import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.api.APITask;
import org.undp.bd.survey.application.data.ApplicationData;
import org.undp.bd.survey.application.data.DatabaseHelper;
import org.undp.bd.survey.application.data.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

public class Login extends OrmLiteBaseActivity<DatabaseHelper> {
	EditText usernameWidget;
	EditText passwordWidget;
	Button loginButton;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        usernameWidget = (EditText)findViewById(R.id.username);
        passwordWidget = (EditText)findViewById(R.id.password);
    }
    
    public void login(View view) throws SQLException {
    	String username = usernameWidget.getText().toString();
    	final String password = passwordWidget.getText().toString();
    	
    	User user = getHelper().getUsers().queryBuilder().where().eq("username", username).queryForFirst();
    	
    	if (ApplicationData.isConnected(this)) {
    		doConnectedLogin(user, username, password);
    	} else if (user != null && user.passwordHash == password.hashCode() && user.authenticated) {
    		completeLogin(user, password);
    	} else {
    		Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_LONG).show();
    	}
    }
    
    private void doConnectedLogin(final User user, final String username, final String password) {
    	APITask<Integer> doLogin = new APITask<Integer>(Login.this) {
		
	    	@Override
			protected String getMessage() {
				return getResources().getString(R.string.logging_in);
			}
	    	
	    	@Override
	    	protected void success(JSONObject data) {
	    		String session_key = null;
	    		try {
	    			session_key = data.getString("session_key");
	    		} catch (JSONException e) {
	    			invalidateUsersLogin(user);
	    		}
	    			
				if (user != null) {
    				if (!user.authenticated)
	    				user.authenticated = true;
    				user.session_key = session_key;
    				getHelper().getUsers().update(user);
    				completeLogin(user, password);
    			} else {
    				User newUser = new User();
    				newUser.username = username;
    				newUser.passwordHash = password.hashCode();
    				newUser.authenticated = true;
    				newUser.session_key = session_key;
    				getHelper().getUsers().create(newUser);
    				newUser = getHelper().getUsers().queryForId(newUser.id);
    				completeLogin(newUser, password);
    			}
	    	};
	    	
	    	@Override
	    	protected void failed(String reason) {
	    		Log.d("Survey.Login", "login failed: " + reason);
	    		invalidateUsersLogin(user);
				Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_LONG).show();
	    	};

			private void invalidateUsersLogin(final User user) {
				if (user != null && user.authenticated) {
					user.authenticated = false;
					getHelper().getUsers().update(user);
				}
			}
		};
		doLogin.execute(new HttpGet("http://10.0.2.2:8000/api/login/?username=" + username + "&password=" + password));
	}

	private void completeLogin(User user, String password) {
    	user.password = password;
    	ApplicationData.instance().setUser(user);
		Intent intent = new Intent(this, Home.class);
		usernameWidget.setText("");
		passwordWidget.setText("");
		startActivity(intent);
    }
}
