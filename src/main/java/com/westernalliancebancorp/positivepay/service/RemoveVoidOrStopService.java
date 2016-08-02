package com.westernalliancebancorp.positivepay.service;

/**
 * Interface providing service methods to work with actions Remove void and Remove Stop
 * @author Moumita Ghosh
 */

public interface RemoveVoidOrStopService {

    void removeVoidOrStop(Long checkId) throws Exception;

}
