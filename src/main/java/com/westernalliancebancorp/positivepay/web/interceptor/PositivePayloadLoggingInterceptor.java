package com.westernalliancebancorp.positivepay.web.interceptor;

import org.springframework.ws.server.endpoint.interceptor.PayloadLoggingInterceptor;


public class PositivePayloadLoggingInterceptor extends PayloadLoggingInterceptor {

	@Override
	protected boolean isLogEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	protected void logMessage(String message) {
		logger.info(message);
	}

}
