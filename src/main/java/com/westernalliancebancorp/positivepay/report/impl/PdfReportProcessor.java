package com.westernalliancebancorp.positivepay.report.impl;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import com.westernalliancebancorp.positivepay.report.AbstractReportProcessor;
import com.westernalliancebancorp.positivepay.utility.common.Constants;

/**
 * User:	Gopal Patil
 * Date:	Feb 19, 2014
 * Time:	6:39:37 PM
 */
public class PdfReportProcessor extends AbstractReportProcessor {
	@Override
	public void exportReport(JasperPrint jp, ByteArrayOutputStream baos) {		 
		try {
			JRPdfExporter exporter = new JRPdfExporter();
			 
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
			exporter.exportReport();			
		} catch (JRException e) {
			throw new RuntimeException("Error occurred exporting PDF report ", e);
		}
	}

	@Override
	public void writeReport(HttpServletResponse response, ByteArrayOutputStream baos, String templateName) {
		super.writeResponse(response, baos, templateName, Constants.MEDIA_TYPE_PDF, Constants.FILE_EXTENSION_PDF);	
	}
}
