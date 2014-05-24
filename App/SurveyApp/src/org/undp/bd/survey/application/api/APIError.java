package org.undp.bd.survey.application.api;

public class APIError {
	public enum Reason {
		HTTP_ERROR, STATUS_ERROR, FAILED_RESPONSE, EXCEPTION, PARSING_EXCEPTION,
	}

	private Reason reason;
	private String detail;
	
	public APIError(Reason reason, String detail) {
		this.reason = reason;
		this.detail = detail;
	}
	
	public Reason getReason() {
		return reason;
	}
	
	public String getDetail() {
		return detail;
	}
	
	@Override
	public String toString() {
		return "<APIError " + reason + ", " + detail + ">"; 
	}
}
