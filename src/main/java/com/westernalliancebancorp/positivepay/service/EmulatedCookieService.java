package com.westernalliancebancorp.positivepay.service;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/13/14
 * Time: 12:54 PM
 */
public interface EmulatedCookieService {
    String createEmulationCookie(String userName) throws UnsupportedEncodingException;
    String exitEmulationCookie() throws UnsupportedEncodingException;
}
