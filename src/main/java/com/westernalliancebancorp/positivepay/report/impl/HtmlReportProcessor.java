package com.westernalliancebancorp.positivepay.report.impl;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import com.westernalliancebancorp.positivepay.report.AbstractReportProcessor;
import com.westernalliancebancorp.positivepay.utility.common.Constants;

/**
 * User:	Gopal Patil
 * Date:	Feb 19, 2014
 * Time:	6:49:15 PM
 */
public class HtmlReportProcessor extends AbstractReportProcessor {
	@Override
	public void exportReport(JasperPrint jp, ByteArrayOutputStream baos) {		
		try {
			JRHtmlExporter exporter = new JRHtmlExporter();
			 
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
			exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
			exporter.setParameter(JRHtmlExporterParameter.HTML_HEADER, "");
			exporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "");
			exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			exporter.setParameter(JRHtmlExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
			exporter.setParameter(JRHtmlExporterParameter.HTML_FOOTER, "");
			exporter.exportReport();			
		} catch (JRException e) {
			throw new RuntimeException("Error occurred exporting HTML report ", e);
		}
	}

	@Override
	public void writeReport(HttpServletResponse response,
			ByteArrayOutputStream baos, String templateName) {
		super.writeResponse(response, baos, templateName, Constants.MEDIA_TYPE_HTML, Constants.FILE_EXTENSION_HTML);	
	}
}
