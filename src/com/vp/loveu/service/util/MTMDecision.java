package com.vp.loveu.service.util;

 
class MTMDecision {
	public final static int DECISION_INVALID	= 0;
	public final static int DECISION_ABORT		= 1;
	public final static int DECISION_ONCE		= 2;
	public final static int DECISION_ALWAYS	= 3;

	int state = DECISION_INVALID;
}
