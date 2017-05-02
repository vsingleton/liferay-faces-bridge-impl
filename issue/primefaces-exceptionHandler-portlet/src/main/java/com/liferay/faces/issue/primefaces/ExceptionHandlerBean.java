package com.liferay.faces.issue.primefaces;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import java.util.Date;

@ManagedBean(name = "exceptionHandlerBean")
@RequestScoped
public class ExceptionHandlerBean {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerBean.class);

	protected boolean throwRenderException;

	protected String lastMethod;

	public void causeActionException() {
		String error = "causeActionException: Deliberate action exception.";

		System.err.println(error);
		throwRenderException = false;
		lastMethod = "causeActionException";
		throw new IllegalArgumentException(error);
	}

	public void causeRenderException() {
		String error = "causeRenderException: Deliberate render exception.";

		System.err.println(error);
		throwRenderException = true;
		lastMethod = "causeRenderException";
	}

	public String getStatus() {
		String status = new Date() + " lastMethod = " + lastMethod;

		System.err.println("getStatus: " + status);
		if (throwRenderException) {
			throwRenderException = false;
			throw new IllegalArgumentException("getStatus: throw new IllegalArgumentException: Deliberate render exception: " + status);
		} else {
			return status;
		}
	}
}
