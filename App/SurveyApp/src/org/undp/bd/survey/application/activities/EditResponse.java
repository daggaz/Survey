package org.undp.bd.survey.application.activities;

import java.util.ArrayList;
import java.util.List;

import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.data.Answer;
import org.undp.bd.survey.application.data.DatabaseHelper;
import org.undp.bd.survey.application.data.DatabaseMixin;
import org.undp.bd.survey.application.data.Question;
import org.undp.bd.survey.application.data.Response;
import org.undp.bd.survey.application.fragements.NavigationDrawerFragment;
import org.undp.bd.survey.application.fragements.QuestionFragment;
import org.undp.bd.survey.application.fragements.QuestionFragment.QuestionFragmentCallBacks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;

public class EditResponse extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, QuestionFragmentCallBacks {

	private CharSequence mTitle;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private Response response;
	private Question currentQuestion;
	private Answer currentAnswer;
	private int currentIndex;
	private List<Answer> unsavedAnswers = new ArrayList<Answer>();

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
		
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("EditResponse", "onCreateOptionsMenu");
		ActionBar actionBar = getSupportActionBar();
		if (mNavigationDrawerFragment.isDrawerOpen()) {
			mTitle = actionBar.getTitle();
			actionBar.setTitle(R.string.questions);
		} else {
			actionBar.setTitle(mTitle);
		}

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
	
	public void previousQuestion(View view) {
		if (currentQuestion.hasPrevious())
			showQuestion(currentIndex-1);
	}
	
	public void nextQuestion(View view) {
		if (currentQuestion.hasNext())
			showQuestion(currentIndex+1);
	}


	@Override
	public void onNavigationDrawerItemSelected(int index) {
		showQuestion(index);
	}

	public void showQuestion(int index) {
		// TODO if current question is required field ask for "Give answer" or "Come back later"
		currentIndex = index;
		currentQuestion = response.survey.questions.toArray(new Question[] {})[index];
		Log.d("EditResponse", "question: " + currentQuestion);
		
		currentAnswer = null;
		if (response.answers != null) {
			for (Answer answer : response.answers)
				if (answer.question.id == currentQuestion.id) {
					currentAnswer = answer;
					break;
				}
		}
		if (currentAnswer == null) {
			for (Answer answer : unsavedAnswers)
				if (answer.question.id == currentQuestion.id) {
					currentAnswer = answer;
					break;
				}
		}
		if (currentAnswer == null) {
			currentAnswer = new Answer();
			this.currentAnswer.question = currentQuestion;
			currentAnswer.reponse = response;
			unsavedAnswers.add(currentAnswer);
		}
		Log.d("EditResponse", "answer: " + currentAnswer);
		
		mTitle = getResources().getText(R.string.question) + ": " + currentQuestion.label;
		getSupportActionBar().setTitle(mTitle);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.container, new QuestionFragment())
		.commit();
	}

	@Override
	public String[] getNavigationDrawerItems() {
		Log.d("EditResponse", "Setting up question drawer");
		List<String> questions = new ArrayList<String>();
		for (Question question : response.survey.questions)
			questions.add(question.label);
		return questions.toArray(new String[] {});
	}
	
	@Override
	public Answer getAnswer() {
		return currentAnswer;
	}
	
	private DatabaseMixin db = new DatabaseMixin(this);
	public DatabaseHelper getHelper() {
		return db.getHelper();
	}
}
