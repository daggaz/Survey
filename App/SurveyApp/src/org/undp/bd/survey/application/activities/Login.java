package org.undp.bd.survey.application.activities;

import java.sql.SQLException;

import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.actions.LoginTask;
import org.undp.bd.survey.application.data.DatabaseContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
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

	private DatabaseContext db = new DatabaseContext(this);
    
    public void login(View view) throws SQLException {
    	String username = usernameWidget.getText().toString();
    	String password = passwordWidget.getText().toString();
    	
    	new LoginTask(db, username, password) {
    		@Override
    		public void onSuccess() {
    			usernameWidget.setText("");
    			passwordWidget.setText("");
    			Intent intent = new Intent(Login.this, Home.class);
    			startActivity(intent);
    		}
    		
    		@Override
    		public void onFailure(FailureType failure) {
    			switch (failure) {
    			case ONLINE_LOGIN_FAILED:
    			case OFFLINE_LOGIN_FAILED:
    				Toast.makeText(Login.this, R.string.login_failed_username_password, Toast.LENGTH_LONG).show();
    				break;
    			case OFFLINE_LOGIN_INVALID:
    				Toast.makeText(Login.this, R.string.login_failed_credentials_invalid, Toast.LENGTH_LONG).show();
    				break;
    			case OFFLINE_LOGIN_UNAVAILABLE:
    				Toast.makeText(Login.this, R.string.login_failed_must_connect, Toast.LENGTH_LONG).show();
    				break;
    			}
    		}
    	}.execute(username, password);
    }
}
