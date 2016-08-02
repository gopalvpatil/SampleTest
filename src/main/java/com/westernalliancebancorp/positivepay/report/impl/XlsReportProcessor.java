package com.westernalliancebancorp.positivepay.report.impl;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;

import com.westernalliancebancorp.positivepay.report.AbstractReportProcessor;
import com.westernalliancebancorp.positivepay.utility.common.Constants;

/**
 * User:	Gopal Patil
 * Date:	Feb 19, 2014
 * Time:	6:47:00 PM
 */
public class XlsReportProcessor extends AbstractReportProcessor {
	@Override
	public void exportReport(JasperPrint jp, ByteArrayOutputStream baos) {		 
		try {
			JRXlsExporter exporter = new JRXlsExporter();
			 
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
			 
			exporter.setParameter(JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
			exporter.setParameter(JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
			exporter.setParameter(JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
			exporter.exportReport();
		} catch (JRException e) {
			throw new RuntimeException("Error occurred exporting XLS report ", e);
		}
	}

	@Override
	public void writeReport(HttpServletResponse response,
			ByteArrayOutputStream baos, String templateName) {		
		super.writeResponse(response, baos, templateName, Constants.MEDIA_TYPE_EXCEL, Constants.FILE_EXTENSION_EXCEL);
	}
}
