package model.exception;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private Map<String, String> error = new HashMap<>();
 
	public ValidationException(String msg) {
		super(msg);
	}

	public Map<String, String> getError() {
		return error;
	}
	
	public void addErro(String fieldName, String errorMessage) {
		error.put(fieldName, errorMessage);
	}
}
