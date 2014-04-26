package org.undp.bd.survey.application.http;

public class MutableObject<T> {
	T object;
	
	public MutableObject() {
		object = null;
	}
	
	public MutableObject(T object) {
		this.object = object;
	}
	
	public synchronized void set(T object) {
		this.object = object;
	}
	
	public synchronized T get() {
		return object;
	}
}
