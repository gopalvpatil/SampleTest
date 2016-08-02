package com.westernalliancebancorp.positivepay.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomerDashboardDto {
	public static class PaymentData {
		private Long accountId;
        private String accountName;
        private String accountNumber;
		private Map<String, Integer> countByStatus = new HashMap<String, Integer>();
		private Map<String, Double> amountByStatus = new HashMap<String, Double>();
		private Map<String,Double> amountByDate = new HashMap<String, Double>();
		private Map<String,Long> countByDate = new HashMap<String, Long>();
		
		public Long getAccountId() {
			return accountId;
		}
		
		public void setAccountId(Long accountId) {
			this.accountId = accountId;
		}
		
		public String getAccountNumber() {
			return accountNumber;
		}
		
		public void setAccountNumber(String accountNumber) {
			this.accountNumber = accountNumber;
		}
		
		public Map<String, Integer> getCountByStatus() {
			return countByStatus;
		}
		
		public void setCountByStatus(Map<String, Integer> countByStatus) {
			this.countByStatus = countByStatus;
		}
		
		public Map<String, Double> getAmountByStatus() {
			return amountByStatus;
		}
		
		public void setAmountByStatus(Map<String, Double> amountByStatus) {
			this.amountByStatus = amountByStatus;
		}

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }
        
		public Map<String, Double> getAmountByDate() {
			return amountByDate;
		}
		
		public void setAmountByDate(Map<String, Double> amountByDate) {
			this.amountByDate = amountByDate;
		}
		
		public Map<String, Long> getCountByDate() {
			return countByDate;
		}
		
		public void setCountByDate(Map<String, Long> countByDate) {
			this.countByDate = countByDate;
		}
    }
}
