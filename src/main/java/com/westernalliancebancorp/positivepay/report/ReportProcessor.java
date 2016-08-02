package com.westernalliancebancorp.positivepay.report;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.dbcp.BasicDataSource;

import com.westernalliancebancorp.positivepay.dto.ReportDto;
import com.westernalliancebancorp.positivepay.model.Report;

/**
 * User:	Gopal Patil
 * Date:	Feb 19, 2014
 * Time:	6:32:01 PM
 */
public interface ReportProcessor {	
	JasperPrint buildReport(Report report, BasicDataSource dataSource);	
	void exportReport(JasperPrint jp, ByteArrayOutputStream baos);
	void writeReport(HttpServletResponse response, ByteArrayOutputStream baos, String templateName);
}
