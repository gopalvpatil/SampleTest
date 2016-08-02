package com.westernalliancebancorp.positivepay.web.interceptor;

import org.springframework.ws.soap.server.endpoint.interceptor.SoapEnvelopeLoggingInterceptor;

public class PositivePaySoapEnvelopeLoggingInterceptor extends SoapEnvelopeLoggingInterceptor{
	
	@Override
	protected boolean isLogEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	protected void logMessage(String message) {
		logger.info(message);
	}


}
