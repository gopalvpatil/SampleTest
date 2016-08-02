package com.westernalliancebancorp.positivepay.service.impl;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.DecisionWindowDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.DecisionWindow;
import com.westernalliancebancorp.positivepay.service.CompanyService;
import com.westernalliancebancorp.positivepay.service.DecisionWindowService;

@Service
public class DecisionWindowServiceImpl implements DecisionWindowService {

	@Loggable
    private Logger logger;
	@Autowired
	DecisionWindowDao decisionWindowDao;	
	@Autowired
	CompanyService companyService;

	@Override
	public DecisionWindow update(DecisionWindow decisionWindow) {
		return decisionWindowDao.update(decisionWindow);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public DecisionWindow save(DecisionWindow decisionWindow) {
		DecisionWindow dw = decisionWindowDao.save(decisionWindow);
		return dw;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCompanyWithDecisionWindow(Long id, DecisionWindow decisionWindow) 
	{
		Company company = companyService.findById(id);
		company.setDecisionWindow(decisionWindow);
	}

	@Override
	public void delete(DecisionWindow decisionWindow)
	{
		decisionWindowDao.delete(decisionWindow);
	}

	@Override
	public DecisionWindow findById(Long id) {
		return decisionWindowDao.findById(id);
	}

	@Override
	public List<DecisionWindow> findAll() {
		return decisionWindowDao.findAll();
	}

	@Override
	public List<DecisionWindow> saveAll(List<DecisionWindow> decisionWindows) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isWithinDecisionWindow(DecisionWindow decisionWindow) {
		String startWindowTimeStr = decisionWindow.getStartWindow().toString();
		String endWindowTimeStr = decisionWindow.getEndWindow().toString();
		TimeZone timeZone = TimeZone.getTimeZone(decisionWindow.getTimeZone());
		logger.info("timeZone = "+timeZone.getDisplayName());
		
	    Calendar calendarStartWindowTime = Calendar.getInstance(timeZone);
	    calendarStartWindowTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startWindowTimeStr.split(":")[0]));
	    calendarStartWindowTime.set(Calendar.MINUTE, Integer.parseInt(startWindowTimeStr.split(":")[1]));
	    calendarStartWindowTime.set(Calendar.SECOND, Integer.parseInt(startWindowTimeStr.split(":")[2]));
	    logger.info(" calendarStartWindowTime = hourOfDay {}, minute {}, and second {}", new Object[]{calendarStartWindowTime.get(Calendar.HOUR_OF_DAY), calendarStartWindowTime.get(Calendar.MINUTE), calendarStartWindowTime.get(Calendar.SECOND)});
	    Calendar calendarEndWindowTime = Calendar.getInstance(timeZone);
	    calendarEndWindowTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endWindowTimeStr.split(":")[0]));
	    calendarEndWindowTime.set(Calendar.MINUTE, Integer.parseInt(endWindowTimeStr.split(":")[1]));
	    calendarEndWindowTime.set(Calendar.SECOND, Integer.parseInt(endWindowTimeStr.split(":")[2]));
	    logger.info(" calendarEndWindowTime = hourOfDay {}, minute {}, and second {}", new Object[]{calendarEndWindowTime.get(Calendar.HOUR_OF_DAY), calendarEndWindowTime.get(Calendar.MINUTE), calendarEndWindowTime.get(Calendar.SECOND)});
	    
	    Calendar calendarRightNowTime = Calendar.getInstance(timeZone);
		int hourOfDay  = calendarRightNowTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
		int minute     = calendarRightNowTime.get(Calendar.MINUTE);
		int second     = calendarRightNowTime.get(Calendar.SECOND);
		calendarRightNowTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendarRightNowTime.set(Calendar.MINUTE, minute);
		calendarRightNowTime.set(Calendar.SECOND, second);
		logger.info(" calendarRightNowTime = hourOfDay {}, minute {}, and second {}", new Object[]{calendarRightNowTime.get(Calendar.HOUR_OF_DAY), calendarRightNowTime.get(Calendar.MINUTE), calendarRightNowTime.get(Calendar.SECOND)});	    
	    logger.info("calendarRightNowTime.getTimeInMillis() = "+calendarRightNowTime.getTimeInMillis());
	    logger.info("calendarStartWindowTime.getTimeInMillis() = "+calendarStartWindowTime.getTimeInMillis());
	    logger.info("calendarEndWindowTime.getTimeInMillis() = "+calendarEndWindowTime.getTimeInMillis());
	    if (calendarRightNowTime.getTimeInMillis() > calendarStartWindowTime.getTimeInMillis() && calendarRightNowTime.getTimeInMillis() < calendarEndWindowTime.getTimeInMillis()) {
	    	return Boolean.TRUE;
	    } else {
	    	return Boolean.FALSE;
	    }
	}
}
