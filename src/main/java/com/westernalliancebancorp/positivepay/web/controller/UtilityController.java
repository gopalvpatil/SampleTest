package com.westernalliancebancorp.positivepay.web.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.westernalliancebancorp.positivepay.log.Loggable;

import javax.servlet.http.HttpServletResponse;

@Controller
public class UtilityController {
	
	@Loggable
	private Logger logger;
	
	@RequestMapping(value = "/appversion", method = RequestMethod.GET)
	public String showAppVersion() {
		return "site.app.version.page";
	}
	
	
	@RequestMapping(value = "/applogs", method = RequestMethod.GET)
	public String showLogsPage() {
		return "site.app.log.page";
	}

	@RequestMapping(value = "/fetchlogs", method = RequestMethod.GET)
    public @ResponseBody List<String> getAppLogs(@RequestParam("logLevel") String logLevel) throws IOException{
		List<String> applogs = new ArrayList<String>();
    	BufferedReader br = null;
    	String tomcatHome = System.getProperty("catalina.base");
    	String logFileLocation = tomcatHome+"/logs/positivepay.log";
    	logger.debug("logFileLocation = "+ logFileLocation);
		try {
			String currentLine;
			br = new BufferedReader(new FileReader(logFileLocation));
			while ((currentLine = br.readLine()) != null) {
				//match log level
				if(logLevel.equalsIgnoreCase("ALL")){
					applogs.add(currentLine);
				}
				else if(currentLine.contains(logLevel)){
					applogs.add(currentLine);
				}
			}
		} catch (IOException e) {
			logger.error("Error while fetching app log details: ", e);
			throw e;
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return applogs;
    }

    @RequestMapping(value = "/fetchErrorStackByTransactionId", method = RequestMethod.GET)
    public void getAppStackTraceLogsWithSupportId(@RequestParam("transactionid") String transactionId, @RequestParam(value = "logFileLocation", required = false) String logFileLocation, HttpServletResponse httpServletResponse) throws IOException {
        logger.info("Inside method getAppLogsWithSupportId");
        String tomcatHome = System.getProperty("catalina.base");
        logger.info("Inside method getAppLogsWithSupportId "+tomcatHome);
        if (logFileLocation == null || logFileLocation.trim().isEmpty()) {
            logFileLocation = tomcatHome + "/logs";
        }
        // awk '/<event>.*<transactionid>G2qldw<\/transactionid>.*<stacktrace>/,/<\/stacktrace><\/event>/'   /usr/share/tomcat6/logs/positivepay*
        //cat /usr/share/tomcat6/logs/positivepay.*|awk '/<event>.*<transactionid>I3AhiS<\/transactionid>/,/<\/event>/'
       // String command = " cat " + logFileLocation + "/positivepay.*|awk '/<event>.*<transactionid>" + transactionId + "<\\/transactionid>.*<stacktrace>/,/<\\/stacktrace><\\/event>/'";
        String command = String.format("awk '/<event>.*<transactionid>%s<\\/transactionid>.*<stacktrace>/,/<\\/stacktrace><\\/event>/'   %s/positivepay*", transactionId, logFileLocation);
        logger.info("Command to execute "+command);
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            pb.redirectErrorStream(true);
            logger.info("Command to execute "+command);
            Process shell = pb.start();
            InputStream shellIn = shell.getInputStream();
            int shellExitStatus = shell.waitFor();
            logger.info("Exit status" + shellExitStatus);
            BufferedReader reader = new BufferedReader(new InputStreamReader(shellIn));
            String line = "";
            while ((line = reader.readLine()) != null) {
                logger.info("Line : "+command);
                httpServletResponse.getOutputStream().println(line);
            }
            shellIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/fetchByTransactionId", method = RequestMethod.GET)
    public void getAppLogsWithSupportId(@RequestParam("transactionid") String transactionId, @RequestParam(value = "logFileLocation", required = false) String logFileLocation, HttpServletResponse httpServletResponse) throws IOException {
        logger.info("Inside method getAppLogsWithSupportId");
        String tomcatHome = System.getProperty("catalina.base");
        logger.info("Inside method getAppLogsWithSupportId "+tomcatHome);
        if (logFileLocation == null || logFileLocation.trim().isEmpty()) {
            logFileLocation = tomcatHome + "/logs";
        }
        //cat /usr/share/tomcat6/logs/positivepay.*|awk '/<event>.*<transactionid>I3AhiS<\/transactionid>/,/<\/event>/'
        String command = " cat " + logFileLocation + "/positivepay.*|awk '/<event>.*<transactionid>" + transactionId + "<\\/transactionid>/,/<\\/event>/'";
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            pb.redirectErrorStream(true);
            logger.info("Command to execute "+command);
            Process shell = pb.start();
            InputStream shellIn = shell.getInputStream();
            int shellExitStatus = shell.waitFor();
            logger.info("Exit status" + shellExitStatus);
            BufferedReader reader = new BufferedReader(new InputStreamReader(shellIn));
            String line = "";
            while ((line = reader.readLine()) != null) {
                logger.info("Line : "+command);
                httpServletResponse.getOutputStream().println(line);
            }
            shellIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/fetchByPattern", method = RequestMethod.GET)
    public void getAppLogsWithPattern(@RequestParam("pattern") String pattern, @RequestParam(value = "logFileLocation", required = false) String logFileLocation, HttpServletResponse httpServletResponse) throws IOException {
        logger.info("Inside method getAppLogsWithSupportId");
        String tomcatHome = System.getProperty("catalina.base");
        logger.info("Inside method getAppLogsWithSupportId "+tomcatHome);
        if (logFileLocation == null || logFileLocation.trim().isEmpty()) {
            logFileLocation = tomcatHome + "/logs";
        }
        //cat /usr/share/tomcat6/logs/positivepay.*|awk '/<event>.*<transactionid>I3AhiS<\/transactionid>/,/<\/event>/'
        String command = " cat " + logFileLocation + "/positivepay.*|awk '/" + pattern + "/{print NR\":\"$0}'";
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            pb.redirectErrorStream(true);
            logger.info("Command to execute "+command);
            Process shell = pb.start();
            InputStream shellIn = shell.getInputStream();
            int shellExitStatus = shell.waitFor();
            logger.info("Exit status" + shellExitStatus);
            BufferedReader reader = new BufferedReader(new InputStreamReader(shellIn));
            String line = "";
            while ((line = reader.readLine()) != null) {
                logger.info("Line : "+command);
                httpServletResponse.getOutputStream().println(line);
            }
            shellIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/sample", method = RequestMethod.GET)
    public void getSample(HttpServletResponse httpServletResponse) throws IOException {
        String response = "";
        String command = "ping -c 3 google.com";
        boolean waitForResponse = true;
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
        pb.redirectErrorStream(true);
        System.out.println("Linux command: " + command);
        try {
            Process shell = pb.start();
            if (waitForResponse) {
                InputStream shellIn = shell.getInputStream();
                int shellExitStatus = shell.waitFor();
                System.out.println("Exit status" + shellExitStatus);
                response = convertStreamToStr(shellIn);
                shellIn.close();
                httpServletResponse.getOutputStream().println(response);
            }
        } catch (IOException e) {
            logger.error("Error occured while executing Linux command. Error Description: "
                    + e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error("Error occured while executing Linux command. Error Description: "
                    + e.getMessage(), e);
        }
        System.out.print("Output: " + response);
    }

    public static String convertStreamToStr(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}
