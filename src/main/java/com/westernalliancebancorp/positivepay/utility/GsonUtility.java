package com.westernalliancebancorp.positivepay.utility;

import com.google.gson.Gson;
import com.westernalliancebancorp.positivepay.web.security.Affidavit;

/**
 * GsonUtility is
 *
 * @author Giridhar Duggirala
 */

public class GsonUtility {
    public static Affidavit getAffidavit(String jsonString) {
        Gson gson = new Gson();
        Affidavit affidavit = gson.fromJson(jsonString, Affidavit.class);
        return affidavit;
    }

    public static String toString(Affidavit affidavit) {
        Gson gson = new Gson();
        return gson.toJson(affidavit);
    }
}
