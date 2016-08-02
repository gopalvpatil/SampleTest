package com.westernalliancebancorp.positivepay.model.interceptor;

/**
 * PositivePayThreadLocal is
 *
 * @author Giridhar Duggirala
 */

public class PositivePayThreadLocal {
    private static final ThreadLocal<String> userDetails = new ThreadLocal<String>();
    private static final ThreadLocal<String> sourceThreadLocal = new ThreadLocal<String>();
    private static final ThreadLocal<String> inputModeThreadLocal = new ThreadLocal<String>();
    
    public enum SOURCE {
        Batch {
            public String toString() {
                return "Batch";
            }
        },
        Unknown {
            public String toString() {
                return "Unknown";
            }

        },
        Action {
            public String toString() {
                return "Action";
            }
        },
     }

    public enum INPUT_MODE {
	ManualEntry {
            public String toString() {
                return "Manual Entry";
            }

        },
    }
    public static void setSource(String src){
        sourceThreadLocal.set(src);
    }

    public static String getSource(){
        return sourceThreadLocal.get();
    }

    public static void set(String name) {
        userDetails.set(name);
    }

    public static String get() {
        return userDetails.get();
    }

    public static void remove() {
        userDetails.remove();
    }
    
    public static void setInputMode(String name) {
	inputModeThreadLocal.set(name);
    }

    public static String getInputMode() {
        return inputModeThreadLocal.get();
    }

    public static void removeInputMode() {
	inputModeThreadLocal.remove();
    }

}
