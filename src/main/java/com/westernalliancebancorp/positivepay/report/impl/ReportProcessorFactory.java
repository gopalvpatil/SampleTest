package com.westernalliancebancorp.positivepay.report.impl;

import com.westernalliancebancorp.positivepay.report.ReportProcessor;
import com.westernalliancebancorp.positivepay.utility.common.ReportType;

/**
 * User:	Gopal Patil
 * Date:	Feb 19, 2014
 * Time:	6:33:05 PM
 */
public class ReportProcessorFactory {
	public ReportProcessor buildReportProcessor(String reportType) {
		if(reportType == null) {
	         return null;
		}		
		  
		if(reportType.equalsIgnoreCase(ReportType.PDF.name())) {
			return new PdfReportProcessor();
		} else if(reportType.equalsIgnoreCase(ReportType.XLS.name())) {
			return new XlsReportProcessor();
		} else if(reportType.equalsIgnoreCase(ReportType.CSV.name())) {
			return new CsvReportProcessor();
		} else if(reportType.equalsIgnoreCase(ReportType.TIFF.name())) {
			return new TiffReportProcessor();
		} else if(reportType.equalsIgnoreCase(ReportType.HTML.name())) {
			return new HtmlReportProcessor();
		} else if(reportType.equalsIgnoreCase(ReportType.MHTML.name())) {
			return new HtmlReportProcessor();
		} else {
			return new CsvReportProcessor();
		}	
	}
}
