package com.westernalliancebancorp.positivepay.service;

import java.util.Map;

/**
 * Check is in the paid status, but we have received request to Stop.
 * We take the action of "StopAfterPaidService" on that check. (SequenceException but will be referring to reference data table.
 * Manual entry inserts will be taken care in ManulaEntryService.
 *
 * This is will be linked to the job.
 * User: gduggirala
 * Date: 15/6/14
 * Time: 9:46 AM
 */
public interface StopAfterPaidService {
    Map<String, Integer> markChecksStopAfterPaid();
}
