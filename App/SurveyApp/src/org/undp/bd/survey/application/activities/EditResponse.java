package org.undp.bd.survey.application.activities;

import java.util.ArrayList;
import java.util.List;

import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.data.DatabaseHelper;
import org.undp.bd.survey.application.data.DatabaseMixin;
import org.undp.bd.survey.application.data.Question;
import org.undp.bd.survey.application.data.Response;
import org.undp.bd.survey.application.fragements.NavigationDrawerFragment;
import org.undp.bd.survey.application.fragements.QuestionFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

public class EditResponse extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	private CharSequence mTitle;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private Response response;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int responseId = getIntent().getIntExtra("response_id", 0);
		if (responseId > 0) {
			response = getHelper().getResponses().queryForId(responseId);
			getHelper().getSurveys().refresh(response.survey);
		} else {
			response = new Response();
			response.survey = getHelper().getSurveys().queryForId(getIntent().getIntExtra("survey_id", 0));
		}
		Log.d("EditResponse", "Editing response " + response);

		setContentView(R.layout.edit_response);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.edit_response, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("EditResponse", "Resume");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		// TODO possible save response?
		Log.d("EditResponse", "Stopped");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("EditResponse", "Destroyed");
		db.destroy();
	}
	
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle(getResources().getString(R.string.discard_response))
	        .setMessage(getResources().getString(R.string.save_draft_query))
	        .setPositiveButton(getResources().getString(R.string.save_draft), new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		            saveDraft();
		            finish();
		        }

			})
			.setCancelable(true)
		    .setNegativeButton(getResources().getString(R.string.discard), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
		    .show();
	}

	public void saveDraft() {
		if (response.id == 0)
			getHelper().getResponses().create(response);
		else
			getHelper().getResponses().update(response);
		finish();
	}
	
	private DatabaseMixin db = new DatabaseMixin(this);

	public DatabaseHelper getHelper() {
		return db.getHelper();
	}


	@Override
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						QuestionFragment.newInstance(position + 1)).commit();
	}

	@Override
	public String[] getNavigationDrawerItems() {
		Log.d("EditResponse", "Setting up question drawer");
		List<String> questions = new ArrayList<String>();
		for (Question question : response.survey.questions)
			questions.add(question.label);
		return questions.toArray(new String[] {});
	}
}
