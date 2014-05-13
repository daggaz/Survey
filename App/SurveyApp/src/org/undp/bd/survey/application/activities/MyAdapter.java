package org.undp.bd.survey.application.activities;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class MyAdapter<T> extends BaseAdapter {
	private final Context context;
	private final int layout_id;
	private List<T> objects;

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
	
	public void setObjects(List<T> objects) {
		this.objects = objects;
		notifyDataSetChanged();
	}
	
	public List<T> getObjects() {
		return objects;
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