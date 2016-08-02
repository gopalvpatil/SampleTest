package com.westernalliancebancorp.positivepay.web.controller;

import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.FiservService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * User: gduggirala
 * Date: 6/5/14
 * Time: 7:12 PM
 */
@Controller
public class FiservController {
    @Loggable
    private Logger logger;

    @Autowired
    private FiservService fiservService;

    @RequestMapping(value = "/zeroedcheck/image", method = RequestMethod.GET)
    public
    @ResponseBody
    void retrieveImageUrl(@RequestParam(value = "traceNumber", required = true) String traceNumber,
                            @RequestParam(value = "amount", required = true) String amount,
                            @RequestParam(value = "accountNumber", required = true) String accountNumber,
                            @RequestParam(value = "side", required = true) String side,
                            HttpServletResponse httpServletResponse) {

        try {
            //String imageUrl = "http://asia.olympus-imaging.com/products/dslr/e510/sample/images/sample_01.jpg"; //fiservService.getFiServUrl(checkId, side);
            String imageUrl = fiservService.getFiServUrl(traceNumber, amount, accountNumber, side);
            streamImage(httpServletResponse, imageUrl);
        } catch (Exception ex) {
            logger.error(Log.event(Event.ERROR_READIN_FISERVE_URL, String.format("Error while reading FiServe image for trace number : %d and side: %s %s", traceNumber, side, ex.getMessage()), ex), ex);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/check/image", method = RequestMethod.GET)
    public void getImage(HttpServletResponse httpServletResponse, @RequestParam(value = "checkId", required = true) Long checkId,
                         @RequestParam(value = "side", required = true) String side) {
        try {
            //String imageUrl = "http://asia.olympus-imaging.com/products/dslr/e510/sample/images/sample_01.jpg"; //fiservService.getFiServUrl(checkId, side);
            String imageUrl = fiservService.getFiServUrl(checkId, side);
            streamImage(httpServletResponse, imageUrl);
        } catch (Exception ex) {
            logger.error(Log.event(Event.ERROR_READIN_FISERVE_URL, String.format("Error while reading FiServe image for check Id : %d and side: %s %s", checkId, side, ex.getMessage()), ex), ex);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void streamImage(HttpServletResponse httpServletResponse, String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        long contentLength = connection.getContentLengthLong();
        String contentEncoding = connection.getContentEncoding();
        String contentType = connection.getContentType();
        byte[] buffer = new byte[4096];
        int n = -1;
        InputStream inputStream = connection.getInputStream();
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setContentLength((int) contentLength);
        if (contentType != null && !contentType.isEmpty()) {
            httpServletResponse.setContentType(contentType);
        }
        OutputStream outputStream = httpServletResponse.getOutputStream();
        while ((n = inputStream.read(buffer)) != -1) {
            if (n > 0) {
                outputStream.write(buffer, 0, n);
            }
        }
        outputStream.close();
        inputStream.close();
    }

    @RequestMapping(value = "/exception/image", method = RequestMethod.GET)
    public
    @ResponseBody
    void retrieveExceptionImageUrl(@RequestParam(value = "exceptionId", required = true) Long exceptionId,
                                     @RequestParam(value = "side", required = true) String side,
                                     HttpServletResponse httpServletResponse) {
        try {
            //String imageUrl = "http://asia.olympus-imaging.com/products/dslr/e510/sample/images/sample_01.jpg"; //fiservService.getFiServUrl(checkId, side);
            String imageUrl = fiservService.getFiServUrlForExceptionId(exceptionId, side);
            streamImage(httpServletResponse, imageUrl);
        } catch (Exception ex) {
            logger.error(Log.event(Event.ERROR_READIN_FISERVE_URL, String.format("Error while reading FiServe image for exception Id : %d and side: %s %s", exceptionId, side, ex.getMessage()), ex), ex);
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
