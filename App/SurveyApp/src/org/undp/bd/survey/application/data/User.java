package org.undp.bd.survey.application.data;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="user")
public class User {
	public User() {}
	
	@DatabaseField(generatedId=true)
	public int id;
	
	@DatabaseField(canBeNull=false)
	public String username;
	
	@DatabaseField(canBeNull=false)
	public int passwordHash;
	
	@DatabaseField()
	public String session_key;
	
	@ForeignCollectionField(eager=false)
	public ForeignCollection<Survey> surveys;

	// Not persisted to database
	public String password;
	
	@DatabaseField(canBeNull=false)
	public boolean authenticated = false;
}
