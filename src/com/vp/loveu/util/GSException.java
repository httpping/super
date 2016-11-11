package com.vp.loveu.util;

import java.io.PrintStream;
import java.io.PrintWriter;

public class GSException extends Exception{

	
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = 1L;

	public GSException() {
	}
	
	public GSException(String detailMessage, Throwable throwable)
    {
        super(detailMessage, throwable);
    }
    
    public GSException(String detailMessage)
    {
        super(detailMessage);
    }
    
    public GSException(Throwable throwable)
    {
        super(throwable);
    }

	@Override
	public Throwable fillInStackTrace() {
		// TODO Auto-generated method stub
		return super.fillInStackTrace();
	}

	@Override
	public Throwable getCause() {
		// TODO Auto-generated method stub
		return super.getCause();
	}

	@Override
	public String getLocalizedMessage() {
		// TODO Auto-generated method stub
		return super.getLocalizedMessage();
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return super.getMessage();
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		// TODO Auto-generated method stub
		return super.getStackTrace();
	}

	@Override
	public Throwable initCause(Throwable throwable) {
		// TODO Auto-generated method stub
		return super.initCause(throwable);
	}

	@Override
	public void printStackTrace() {
		// TODO Auto-generated method stub
		super.printStackTrace();
	}

	@Override
	public void printStackTrace(PrintStream err) {
		// TODO Auto-generated method stub
		super.printStackTrace(err);
	}

	@Override
	public void printStackTrace(PrintWriter err) {
		// TODO Auto-generated method stub
		super.printStackTrace(err);
	}

	@Override
	public void setStackTrace(StackTraceElement[] trace) {
		// TODO Auto-generated method stub
		super.setStackTrace(trace);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	
}
