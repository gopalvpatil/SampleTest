package com.westernalliancebancorp.positivepay.threadlocal;

import com.westernalliancebancorp.positivepay.web.security.Affidavit;

/**
 * Affidavit Thread Local
 * @author akumar1
 *
 */
public class AffidavitThreadLocal {
	
	private static final ThreadLocal<Affidavit> affidavitThreadLocal = new ThreadLocal<Affidavit>();
	private static final ThreadLocal<Boolean> affidavitTTLChanged = new ThreadLocal<Boolean>();

    public static void set(Affidavit affidavit) {
    	affidavitThreadLocal.set(affidavit);
    }

    public static Affidavit get() {
        return affidavitThreadLocal.get();
    }

    public static void remove() {
    	affidavitThreadLocal.remove();
    }
    
    public static void markTTLChanged(Boolean status){
    	affidavitTTLChanged.set(status);
    }
    
    public static Boolean isTTLChanged(){
    	return affidavitTTLChanged.get();
    }
    
    public static void removeTTLChanged(){
    	affidavitTTLChanged.remove();
    }
}
