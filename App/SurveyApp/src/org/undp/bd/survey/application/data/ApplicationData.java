package org.undp.bd.survey.application.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ApplicationData {
	private static ApplicationData instance;
	
	private User user;
	
	private ApplicationData() {}

	public static ApplicationData instance() {
		if (instance == null)
			instance = new ApplicationData();
		return instance;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public static boolean isConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
