package org.undp.bd.survey.application.activities;

import java.util.ArrayList;
import java.util.List;

import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.data.ApplicationData;
import org.undp.bd.survey.application.data.DatabaseMixin;
import org.undp.bd.survey.application.data.DatabaseHelper;
import org.undp.bd.survey.application.data.Survey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class Surveys extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.surveys);

		final List<Survey> surveys = new ArrayList<Survey>(ApplicationData.instance().getUser().surveys);

		ListView listView = (ListView) findViewById(R.id.list);
		listView.setAdapter(new MyAdapter<Survey>(this, surveys, R.layout.survey_list_item) {
			@Override
			public void initialiseView(Context context, View view, Survey survey) {
				TextView titleView = (TextView)view.findViewById(R.id.title);
				titleView.setText(survey.title);
				TextView descriptionView = (TextView)view.findViewById(R.id.description);
				descriptionView.setText(survey.description);
			}
		});
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		      @Override
		      public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
		    	  Intent intent = new Intent(Surveys.this, SurveyHome.class);
		    	  intent.putExtra("survey_id", surveys.get(position).id);
		    	  startActivity(intent);
		      }
		});
		
	    View empty = findViewById(R.id.empty);
	    listView.setEmptyView(empty);
	}

	private DatabaseMixin db = new DatabaseMixin(this);

	public DatabaseHelper getHelper() {
		return db.getHelper();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.destroy();
	}
}
