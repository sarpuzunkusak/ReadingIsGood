package com.readingisgood.exception;

public class OutOfStockException extends ApplicationException {

	private static final long serialVersionUID = 1L;

	public OutOfStockException(String message) {
		super(message);
	}

}
