package org.undp.bd.survey.application.activities;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

public class Surveys extends OrmLiteBaseActivity<DatabaseHelper> {

	public abstract class MyAdapter<T> extends BaseAdapter {
		private final List<T> objects;
		private final Context context;
		private final int layout_id;

		public MyAdapter(Context context, List<T> objects, int layout_id) {
			this.context = context;
			this.objects = objects;
			this.layout_id = layout_id;
		}
		
		public abstract void initialiseView(Context context, View view, T object);

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(layout_id, parent, false);
			initialiseView(context, view, objects.get(position));
			return view;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return objects.get(position);
		}

		@Override
		public int getCount() {
			return objects.size();
		}
	}

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
		    	  
		      }
		});
	}
	
	@Override
	public void onContentChanged() {
		super.onContentChanged();

	    View empty = findViewById(R.id.empty);
	    ListView list = (ListView) findViewById(R.id.list);
	    list.setEmptyView(empty);
	}
}
