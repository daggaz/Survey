package org.undp.bd.survey.application.data;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "org.undp.bd.survey.db";
	private static final int DATABASE_VERSION = 1;

	private RuntimeExceptionDao<Survey, Integer> surveyDao = null;
	private RuntimeExceptionDao<Question, Integer> questionDao = null;
	private RuntimeExceptionDao<User, Integer> userDao = null;
	private RuntimeExceptionDao<Response, Integer> responseDao = null;
	private RuntimeExceptionDao<Answer, Integer> answerDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, User.class);
			TableUtils.createTable(connectionSource, Survey.class);
			TableUtils.createTable(connectionSource, Question.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
		
		User user = new User();
		user.username = "jamie";
		user.passwordHash = "ratio".hashCode();
		getUsers().create(user);

//		// here we try inserting data in the on-create as a test
//		RuntimeExceptionDao<Survey, Integer> dao = getSimpleDataDao();
//		long millis = System.currentTimeMillis();
//		// create some entries in the onCreate
//		Survey simple = new Survey(millis);
//		dao.create(simple);
//		simple = new Survey(millis + 1);
//		dao.create(simple);
//		Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate: " + millis);
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Survey.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	public RuntimeExceptionDao<Survey, Integer> getSurveys() {
		if (surveyDao == null) {
			surveyDao = getRuntimeExceptionDao(Survey.class);
		}
		return surveyDao;
	}


	public RuntimeExceptionDao<Question, Integer> getQuestions() {
		if (questionDao == null) {
			questionDao = getRuntimeExceptionDao(Question.class);
		}
		return questionDao;
	}
	
	public RuntimeExceptionDao<User, Integer> getUsers() {
		if (userDao == null) {
			userDao = getRuntimeExceptionDao(User.class);
		}
		return userDao;
	}

	public RuntimeExceptionDao<Response, Integer> getResponses() {
		if (responseDao == null) {
			responseDao = getRuntimeExceptionDao(Response.class);
		}
		return responseDao;
	}

	public RuntimeExceptionDao<Answer, Integer> getAnswers() {
		if (answerDao == null) {
			answerDao = getRuntimeExceptionDao(Answer.class);
		}
		return answerDao;
	}
	
	@Override
	public void close() {
		super.close();
		userDao = null;
		surveyDao = null;
		questionDao = null;
		responseDao = null;
		answerDao = null;
	}
}