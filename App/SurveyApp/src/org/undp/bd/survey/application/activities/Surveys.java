package org.undp.bd.survey.application.activities;

import org.undp.bd.survey.application.R;
import org.undp.bd.survey.application.data.ApplicationData;
import org.undp.bd.survey.application.data.DatabaseHelper;
import org.undp.bd.survey.application.data.Survey;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseListActivity;

public class Surveys extends OrmLiteBaseListActivity<DatabaseHelper> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.surveys);
		final Survey[] surveys = ApplicationData.instance().getUser().surveys.toArray(new Survey[] {});
		setListAdapter(new ArrayAdapter<Survey>(this, R.layout.survey_list_item, surveys) {
			
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				TextView rowView = (TextView) inflater.inflate(R.layout.survey_list_item, parent, false);
				rowView.setText(surveys[position].title);
				return rowView;
			}
		});
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
		      @Override
		      public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
		    	  // TODO show survey
		      }
		});
	}
	
	@Override
	public void onContentChanged() {
		super.onContentChanged();

	    View empty = findViewById(R.id.empty);
	    ListView list = (ListView) findViewById(android.R.id.list);
	    list.setEmptyView(empty);
	}
}
