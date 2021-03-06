package org.undp.bd.survey.application.activities;


import org.json.JSONException;
import org.json.JSONObject;
import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.actions.SynchroniseAction;
import org.undp.bd.survey.application.data.ApplicationData;
import org.undp.bd.survey.application.data.DatabaseContext;
import org.undp.bd.survey.application.data.DatabaseHelper;
import org.undp.bd.survey.application.data.User;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;


public class Home extends ActionBarActivity {
	
	private DatabaseContext db = new DatabaseContext(this);

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    db.destroy();
	}

	DatabaseHelper getHelper() {
		return db.getHelper();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
	}
	
	public void surveys(View view) {
		Intent intent = new Intent(this, Surveys.class);	
		startActivity(intent);
	}
	
	public void synchronise(View view) {
		new SynchroniseAction(db) {

			@Override
			protected void onSuccess(JSONObject data) {
				int nacks;
				try {
					nacks = data.getJSONArray("nacks").length();
				} catch (JSONException e) {
					nacks = 0;
				}
				if (nacks > 0)
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.synchronising_error) + " " + getResources().getString(R.string.submission_error).replace("{0}", Integer.toString(nacks)) , Toast.LENGTH_LONG).show();
				else
					Toast.makeText(getApplicationContext(), R.string.synchronising_success, Toast.LENGTH_LONG).show();
			}
			
			@Override
			protected void onFailure(FailureType failure, String reason) {
				switch (failure) {
				case OFFLINE:
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.synchronising_login) , Toast.LENGTH_LONG).show();
					break;
				case API:
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.synchronising_error) + " " + reason , Toast.LENGTH_LONG).show();
					break;
				default:
					throw new RuntimeException("NotImplemented");
				}
			}
		}.execute();
	}
	
	public void logout(View view) {
		logout();
	}
	
	@Override
	public void onBackPressed() {
		logout();
	}
	
	private void logout() {
	    new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle(getResources().getString(R.string.logout))
	        .setMessage(getResources().getString(R.string.confirm_logout))
	        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	User user = ApplicationData.instance().getUser();
		        	user.session_key = null;
		        	getHelper().getUsers().update(user);
		            finish();    
		        }
		})
	    .setNegativeButton(getResources().getString(R.string.no), null)
	    .show();
	}
}
