package com.westernalliancebancorp.positivepay.report;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Report;
import com.westernalliancebancorp.positivepay.model.ReportBankCompanyAccountParameter;
import com.westernalliancebancorp.positivepay.model.ReportParameterOptionValue;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

/**
 * User:	Gopal Patil
 * Date:	Feb 19, 2014
 * Time:	6:32:27 PM
 */
public abstract class AbstractReportProcessor implements ReportProcessor {	
	@Loggable
	private Logger logger;
	@Value("${ROOT_DIR}")
    private String rootDir = "/reports/";
    
	@Override
	public JasperPrint buildReport(Report report, BasicDataSource dataSource) {
		JasperPrint jp = null;
		try {
			String templateFileName = rootDir + report.getReportTemplate().getTemplateFileName() + ".jrxml";
			// 1. Add report parameters
			HashMap<String, Object> params = new HashMap<String, Object>(); 
			params.put("report_name", report.getReportTemplate().getName());
			params.put("ROOT_DIR", rootDir);
			
			if (report.getAsOfDate() != null) {
				params.put("in_as_of_date", report.getAsOfDate());
			} else {
			    Date utilDate = new Date();
			    java.util.Date date = new java.util.Date(utilDate.getTime());
				params.put("in_as_of_date", date);
			}
			
			String sqlCompanyIds = "( ";
			String sqlAccountIds = "( ";
			String separatorCommaForCompany = "";
			String separatorCommaForAccount = "";
			Set<ReportBankCompanyAccountParameter> parameters = report.getReportBankCompanyAccountParameters();
			if (parameters != null && parameters.size() > 0) {
				for (ReportBankCompanyAccountParameter parameter : parameters) {
					if (parameter.getCompany() != null) {
						sqlCompanyIds += separatorCommaForCompany + parameter.getCompany().getId().toString();
						separatorCommaForCompany = ",";
					}

					if (parameter.getAccount() != null) {
						sqlAccountIds += separatorCommaForAccount + parameter.getAccount().getId().toString();
						separatorCommaForAccount = ",";
					}					
				}
			}
			
			sqlCompanyIds += " )";
			sqlAccountIds += " )";
			
			params.put("in_company_ids", sqlCompanyIds);
			params.put("in_account_ids", sqlAccountIds);
			
			Set<ReportParameterOptionValue> optionalParameters = report.getReportParameterOptionValues();
			for (ReportParameterOptionValue optionValue : optionalParameters) {
				String operator = optionValue.getOperator();
						
				if (optionValue.getReportParameterOption().getDataType().equals("char")) {
					params.put(optionValue.getReportParameterOption().getName(), optionValue.getValueChar());
				} else if (optionValue.getReportParameterOption().getDataType().equals("date")) {
					Date dateFrom = optionValue.getValueDateStart();
					Date dateTo = optionValue.getValueDateStart();
					String dateFromSymbolic = optionValue.getValueDateStartSymbolicValue();
					String dateToSymbolic = optionValue.getValueDateEndSymbolicValue();
					Boolean isSymbolic = optionValue.isValueDateSymbolic();
					
					if (isSymbolic != null && isSymbolic == true) {
						if (dateFromSymbolic != null) {
							dateFrom = DateUtils.convertSymbolicDateToRealDate(dateFromSymbolic);
						}
						
						if (dateToSymbolic != null) {
							dateTo = DateUtils.convertSymbolicDateToRealDate(dateToSymbolic);
						}
					}

					
					String sql = "1 = 1";
					if (dateFrom != null) {
						if (operator.compareTo("equals") == 0) {
							sql = "= " + DateUtils.wdf.format(dateFrom);
						} else if (operator.compareTo("isBefore") == 0) {
							sql = "<= " + DateUtils.wdf.format(dateFrom);
						} else if (operator.compareTo("isAfter") == 0) {
							sql = ">= " + DateUtils.wdf.format(dateFrom);					
						} else if (operator.compareTo("isBetween") == 0) {
							if (dateTo != null) {
								sql = "BETWEEN " + DateUtils.wdf.format(dateFrom) + " AND " + DateUtils.wdf.format(dateTo);
							}
						} 
					}					

					params.put("sql_" + optionValue.getReportParameterOption().getName(), sql);
					
					
					params.put(optionValue.getReportParameterOption().getName() + "_from", dateFrom);
					params.put(optionValue.getReportParameterOption().getName() + "_to", dateTo);
				}
			}
			
			// 2. Convert template to JasperDesign
			JasperDesign jd = JRXmlLoader.load(getClass().getResourceAsStream(templateFileName));			
			 
			// 3. Compile design to JasperReport
			JasperReport jr = JasperCompileManager.compileReport(jd);
			 
			// 4. Create the JasperPrint object
			// Make sure to pass the JasperReport, report parameters, and data source
			jp = JasperFillManager.fillReport(jr, params, dataSource.getConnection());		
		} catch (JRException jre) {			
			jre.printStackTrace();
			throw new RuntimeException(jre);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return jp;
	}
	
	public void writeResponse(HttpServletResponse response, ByteArrayOutputStream baos, String templateName, String contentType, String fileExtension) {		 
		try {
			String fileName = templateName + fileExtension;
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			
			response.setContentType(contentType);
			response.setContentLength(baos.size());
			
			ServletOutputStream outputStream = response.getOutputStream();
			baos.writeTo(outputStream);
			outputStream.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
}
