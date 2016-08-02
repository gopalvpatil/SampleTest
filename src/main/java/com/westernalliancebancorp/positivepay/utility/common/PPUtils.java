package com.westernalliancebancorp.positivepay.utility.common;

import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.threadlocal.AffidavitThreadLocal;
import com.westernalliancebancorp.positivepay.web.security.Affidavit;

@Component
public class PPUtils {	
	public static boolean isEmulatedUser() {
		Affidavit affidavit = AffidavitThreadLocal.get();
		if(affidavit != null && affidavit.getType().equals(Affidavit.TYPE.EMULATED.toString())) {
			return true;
		}
		return false;
	}
	
	public static String stripLeadingZeros(String strToStripLeadingZeros) {
		if (strToStripLeadingZeros == null) {        
			return null;    
		}   
		if(strToStripLeadingZeros.startsWith("0")) {
			char[] chars = strToStripLeadingZeros.toCharArray();
			int index = 0;
			for (; index < strToStripLeadingZeros.length(); index++) {       
				if (chars[index] != '0') {            
					break;       
				}    
			}    
			return (index == 0) ? strToStripLeadingZeros : strToStripLeadingZeros.substring(index);
		}
		return strToStripLeadingZeros;
	}
	
	public static String addQuotesToCSV(String csvString) {
		String replaced = "'"+csvString.replace("|", "','")+"'";
		return replaced;
	}
}
