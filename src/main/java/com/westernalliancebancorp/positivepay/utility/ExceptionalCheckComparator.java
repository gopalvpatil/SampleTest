package com.westernalliancebancorp.positivepay.utility;

import java.util.Comparator;

import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;

/**
 * @author Gopal Patil
 *
 */
public class ExceptionalCheckComparator implements Comparator<ExceptionalCheck> {
	
	@Override
	public int compare(ExceptionalCheck check1, ExceptionalCheck check2) {	
		
		int lineNumberForCheck1 = Integer.parseInt(check1.getLineNumber());
		int lineNumberForCheck2 = Integer.parseInt(check2.getLineNumber());
		
		return lineNumberForCheck1 < lineNumberForCheck2 ? -1
		         : lineNumberForCheck1 > lineNumberForCheck2 ? 1
		         : 0;
	}

}
