package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.ReferenceData;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/19/14
 * Time: 10:46 PM
 */
@Repository
public interface ReferenceDataDao  extends GenericDao<ReferenceData, Long> {
	List<ReferenceData> findAllReferenceDataBy(List<Long> ids);
    List<ReferenceData> findByCheckNumberAndAccountId(String checkNumber, Long accountId);
    List<ReferenceData> findByCheckNumberAndAccountIdByStatus(String checkNumber, Long accountId, ReferenceData.STATUS status );
    List<ReferenceData> findByCheckNumberAccountIdAndItemType(String checkNumber, Long accountId, ReferenceData.ITEM_TYPE item_type);
    List<ReferenceData> findByCheckNumberAccountNumberAndItemType(String checkNumber, String accountNumber, ReferenceData.ITEM_TYPE item_type);
    List<ReferenceData> findByCheckNumberAccountIdItemTypeAndStatus(String checkNumber, Long accountId, ReferenceData.ITEM_TYPE item_type,ReferenceData.STATUS status);

    List<ReferenceData> findByDigestAndItemType(String digest, ReferenceData.ITEM_TYPE item_type);
}
