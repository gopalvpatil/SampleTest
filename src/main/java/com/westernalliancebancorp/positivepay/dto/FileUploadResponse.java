package com.westernalliancebancorp.positivepay.dto;

import java.util.List;

public class FileUploadResponse {
	
	private int noOfduplicatesChecksWithinFile;
	private int noOfchecksWithNoAccountNumberMatch;
	private int noOfduplicatesChecksWithinDatabase;
	private int totalChecksInFile;
	private int noOfValidChecks;
	private int noOfChecksInWrongDataFormat;
	private int noOfChecksWithWrongItemCode;
	private int noOfPaidAndStopChecks;
	private int status;
	private String response;
	private List<String> warnings;
	private List<String> errors;
	
	/**
	 * @return the noOfduplicatesChecksWithinFile
	 */
	public int getNoOfduplicatesChecksWithinFile() {
		return noOfduplicatesChecksWithinFile;
	}
	/**
	 * @param noOfduplicatesChecksWithinFile the noOfduplicatesChecksWithinFile to set
	 */
	public void setNoOfduplicatesChecksWithinFile(int noOfduplicatesChecksWithinFile) {
		this.noOfduplicatesChecksWithinFile = noOfduplicatesChecksWithinFile;
	}
	/**
	 * @return the noOfchecksWithNoAccountNumberMatch
	 */
	public int getNoOfchecksWithNoAccountNumberMatch() {
		return noOfchecksWithNoAccountNumberMatch;
	}
	/**
	 * @param noOfchecksWithNoAccountNumberMatch the noOfchecksWithNoAccountNumberMatch to set
	 */
	public void setNoOfchecksWithNoAccountNumberMatch(
			int noOfchecksWithNoAccountNumberMatch) {
		this.noOfchecksWithNoAccountNumberMatch = noOfchecksWithNoAccountNumberMatch;
	}
	/**
	 * @return the noOfduplicatesChecksWithinDatabase
	 */
	public int getNoOfduplicatesChecksWithinDatabase() {
		return noOfduplicatesChecksWithinDatabase;
	}
	/**
	 * @param noOfduplicatesChecksWithinDatabase the noOfduplicatesChecksWithinDatabase to set
	 */
	public void setNoOfduplicatesChecksWithinDatabase(
			int noOfduplicatesChecksWithinDatabase) {
		this.noOfduplicatesChecksWithinDatabase = noOfduplicatesChecksWithinDatabase;
	}
	/**
	 * @return the noOfChecksInWrongDataFormat
	 */
	public int getNoOfChecksInWrongDataFormat() {
		return noOfChecksInWrongDataFormat;
	}
	public int getNoOfChecksWithWrongItemCode() {
		return noOfChecksWithWrongItemCode;
	}
	public void setNoOfChecksWithWrongItemCode(int noOfChecksWithWrongItemCode) {
		this.noOfChecksWithWrongItemCode = noOfChecksWithWrongItemCode;
	}
	/**
	 * @param noOfChecksInWrongDataFormat the noOfChecksInWrongDataFormat to set
	 */
	public void setNoOfChecksInWrongDataFormat(int noOfChecksInWrongDataFormat) {
		this.noOfChecksInWrongDataFormat = noOfChecksInWrongDataFormat;
	}
	/**
	 * @return the totalChecksInFile
	 */
	public int getTotalChecksInFile() {
		return totalChecksInFile;
	}
	/**
	 * @param totalChecksInFile the totalChecksInFile to set
	 */
	public void setTotalChecksInFile(int totalChecksInFile) {
		this.totalChecksInFile = totalChecksInFile;
	}
	/**
	 * @return the noOfValidChecks
	 */
	public int getNoOfValidChecks() {
		return noOfValidChecks;
	}
	/**
	 * @param noOfValidChecks the noOfValidChecks to set
	 */
	public void setNoOfValidChecks(int noOfValidChecks) {
		this.noOfValidChecks = noOfValidChecks;
	}
	public int getNoOfPaidAndStopChecks() {
		return noOfPaidAndStopChecks;
	}
	public void setNoOfPaidAndStopChecks(int noOfPaidAndStopChecks) {
		this.noOfPaidAndStopChecks = noOfPaidAndStopChecks;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public List<String> getWarnings() {
		return warnings;
	}
	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	@Override
	public String toString() {
		return "FileUploadResponse [noOfduplicatesChecksWithinFile="
				+ noOfduplicatesChecksWithinFile
				+ ", noOfchecksWithNoAccountNumberMatch="
				+ noOfchecksWithNoAccountNumberMatch
				+ ", noOfduplicatesChecksWithinDatabase="
				+ noOfduplicatesChecksWithinDatabase + ", totalChecksInFile="
				+ totalChecksInFile + ", noOfValidChecks=" + noOfValidChecks
				+ ", noOfChecksInWrongDataFormat="
				+ noOfChecksInWrongDataFormat
				+ ", noOfChecksWithWrongItemCode="
				+ noOfChecksWithWrongItemCode + ", noOfPaidAndStopChecks="
				+ noOfPaidAndStopChecks + ", status=" + status + ", response="
				+ response + ", warnings=" + warnings + ", errors=" + errors
				+ "]";
	}
}
