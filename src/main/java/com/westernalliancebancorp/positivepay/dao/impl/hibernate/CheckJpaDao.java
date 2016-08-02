/**
 *
 */
package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Account_;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.CheckStatus_;
import com.westernalliancebancorp.positivepay.model.Check_;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.Company_;
import com.westernalliancebancorp.positivepay.model.FileMetaData;
import com.westernalliancebancorp.positivepay.model.FileMetaData_;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.model.ReferenceData_;
import com.westernalliancebancorp.positivepay.utility.common.Constants;

/**
 * Data access object JPA impl to work with Check model database operations.
 *
 * @author Anand Kumar
 */
@Repository
public class CheckJpaDao extends GenericJpaDao<Check, Long> implements CheckDao {

    @Loggable
    private Logger logger;

    public CheckJpaDao() {
        super();
    }

    //More specific method implementations to follow for SampleModel entity
    public boolean isDuplicate(Check check) {
        logger.info("Checking for duplicity: " + check);
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Check> cq = cb.createQuery(Check.class);
        Root<Check> rootEntry = cq.from(Check.class);
        cq.where(
                cb.equal(rootEntry.get(Check_.account).get(Account_.number), check.getAccount().getNumber()),
                cb.equal(rootEntry.get(Check_.checkNumber), check.getCheckNumber())
        );
        TypedQuery<Check> allQuery = getEntityManager().createQuery(cq);
        List<Check> checks = allQuery.getResultList();
        if (checks.isEmpty())
            return true;
        else
            return false;
    }

    /* (non-Javadoc)
     * @see com.westernalliancebancorp.positivepay.dao.CheckDao#findCheckBy(java.lang.String, java.lang.String, java.math.BigDecimal)
     */
    @Override
    public Check findCheckBy(String strAccountNumber, String strCheckNumber,
                             BigDecimal dCheckAmount) {
        Check check = null;
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Check> cq = cb.createQuery(Check.class);
            Root<Check> rootEntry = cq.from(Check.class);
            Fetch<Check, CheckStatus> checkStatus = rootEntry.fetch(Check_.checkStatus, JoinType.LEFT);
            Fetch<Check, ReferenceData> referenceData = rootEntry.fetch(Check_.referenceData, JoinType.LEFT);
            cq.where(
                    cb.equal(rootEntry.get(Check_.account).get(Account_.number), strAccountNumber),
                    cb.equal(rootEntry.get(Check_.issuedAmount), dCheckAmount),
                    cb.equal(rootEntry.get(Check_.checkNumber), strCheckNumber)
            );
            TypedQuery<Check> allQuery = getEntityManager().createQuery(cq);
            check = (Check) allQuery.getSingleResult();
        } catch (NoResultException nre) {
            logger.error("Exception occurred at CheckJpaDao : findCheckBy " + nre.getMessage(), nre);
        }
        
        return check;
    }

    /* (non-Javadoc)
     * @see com.westernalliancebancorp.positivepay.dao.CheckDao#findCheckBy(java.lang.String, java.lang.String, java.math.BigDecimal)
     */
    @Override
    public Check findCheckBy(String accountNumber, String checkNumber) {
        Check check = null;
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Check> cq = cb.createQuery(Check.class);
            Root<Check> rootEntry = cq.from(Check.class);
            Fetch<Check, CheckStatus> checkStatus = rootEntry.fetch(Check_.checkStatus, JoinType.LEFT);
            Fetch<Check, ReferenceData> referenceData = rootEntry.fetch(Check_.referenceData, JoinType.LEFT);
            cq.where(
                    cb.equal(rootEntry.get(Check_.account).get(Account_.number), accountNumber),
                    cb.equal(rootEntry.get(Check_.checkNumber), checkNumber)
            );
            TypedQuery<Check> allQuery = getEntityManager().createQuery(cq);
            check = (Check) allQuery.getSingleResult();
        } catch (NoResultException nre) {
            logger.error("Exception occurred at CheckJpaDao : findCheckBy " + nre.getMessage(), nre);
        }
        
        return check;
    }
    
    @Override
    public List<Check> findAllChecksBy(String accountNumber){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Check> cq = cb.createQuery(Check.class);
        Root<Check> rootEntry = cq.from(Check.class);
        Fetch<Check, FileMetaData> fileMetaData = rootEntry.fetch(Check_.fileMetaData, JoinType.LEFT);
        cq.where(
                cb.equal(rootEntry.get(Check_.account).get(Account_.number), accountNumber)
        );
        TypedQuery<Check> allQuery = getEntityManager().createQuery(cq);
        List<Check> checksList = allQuery.getResultList();  
        return checksList;
    }

    @Override
    public List<Check> findStaleChecks(List<Long> accountIds, Date staleDat) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Check> cq = cb.createQuery(Check.class);
        Root<Check> rootEntry = cq.from(Check.class);
        //cq.select(rootEntry).where(rootEntry.get(Check_.account).get(Account_.id).in(accountIds));
        TypedQuery<Check> allQuery = getEntityManager().createQuery(cq);
        List<Check> checksList = allQuery.getResultList();
        
        return checksList;
    }

    @Override
    public Check findCheckBy(String checkNumber, Long accountId,
                             BigDecimal checkAmount, Long checkStatusId) {
        Check check = null;
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Check> cq = cb.createQuery(Check.class);
            Root<Check> rootEntry = cq.from(Check.class);
            Fetch<Check, Account> account = rootEntry.fetch(Check_.account, JoinType.LEFT);
            Fetch<Check, CheckStatus> checkStatus = rootEntry.fetch(Check_.checkStatus, JoinType.LEFT);
            cq.where(
                    cb.equal(rootEntry.get(Check_.checkNumber), checkNumber),
                    cb.equal(rootEntry.get(Check_.account).get(Account_.id), accountId),
                    cb.equal(rootEntry.get(Check_.issuedAmount), checkAmount),
                    cb.equal(rootEntry.get(Check_.checkStatus).get(CheckStatus_.id), checkStatusId)
            );
            TypedQuery<Check> allQuery = getEntityManager().createQuery(cq);
            check = (Check) allQuery.getSingleResult();
        } catch (NoResultException nre) {
            logger.error("Exception occured at CheckJpaDao : findCheckBy " + nre.getMessage(), nre);
        }
        return check;
    }

    @Override
    public Check findByReferenceDataId(Long referenceDataId) {
        Check check = null;
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Check> cq = cb.createQuery(Check.class);
            Root<Check> rootEntry = cq.from(Check.class);
            Fetch<Check, Account> account = rootEntry.fetch(Check_.account, JoinType.LEFT);
            Fetch<Check, CheckStatus> checkStatus = rootEntry.fetch(Check_.checkStatus, JoinType.LEFT);
            cq.where(
                    cb.equal(rootEntry.get(Check_.referenceData).get(ReferenceData_.id), referenceDataId)
            );
            TypedQuery<Check> allQuery = getEntityManager().createQuery(cq);
            check = (Check) allQuery.getSingleResult();
        } catch (NoResultException nre) {
            logger.error("Exception occured at CheckJpaDao : findCheckBy " + nre.getMessage(), nre);
        }
        return check;
    }

    @Override
    public List<Check> findChecksByCheckStatusIds(List<Long> checkStatusIds) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Check> cq = cb.createQuery(Check.class);
        Root<Check> rootEntry = cq.from(Check.class);
        Fetch<Check, CheckStatus> checkStatus = rootEntry.fetch(Check_.checkStatus, JoinType.LEFT);
        cq.select(rootEntry).where(rootEntry.get(Check_.checkStatus).get(CheckStatus_.id).in(checkStatusIds));
        TypedQuery<Check> allQuery = getEntityManager().createQuery(cq);
        return allQuery.getResultList();
    }
    
    @Override
    public List<Check> findAllChecksInExceptionForUserCompany(Company userCompany) {
    	CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Check> cq = criteriaBuilder.createQuery(Check.class);
        Root<Check> checkRoot = cq.from(Check.class);
        Join<Check, CheckStatus> checkStatus = checkRoot.join(Check_.checkStatus, JoinType.LEFT);
        Predicate isExceptionalStateCondition = criteriaBuilder.equal(checkStatus.get(CheckStatus_.isExceptionalStatus), Boolean.TRUE);
        Predicate companyCondition = criteriaBuilder.equal(checkRoot.get(Check_.account).get(Account_.company).get(Company_.id), userCompany.getId());
        Predicate hybridPredicate = criteriaBuilder.and(companyCondition, isExceptionalStateCondition);
        cq.where(hybridPredicate);
        TypedQuery<Check> allQuery = getEntityManager().createQuery(cq);
        logger.info("allQuery.getResultList() = "+allQuery.getResultList().size());
        return allQuery.getResultList();
    }
    
    @Override
    public List<Check> findAllChecksForUserCompany(Company userCompany) {
    	CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Check> cq = criteriaBuilder.createQuery(Check.class);
        Root<Check> checkRoot = cq.from(Check.class);
        Predicate companyCondition = criteriaBuilder.equal(checkRoot.get(Check_.account).get(Account_.company).get(Company_.id), userCompany.getId());
        Predicate hybridPredicate = criteriaBuilder.and(companyCondition);
        cq.where(hybridPredicate);
        TypedQuery<Check> allQuery = getEntityManager().createQuery(cq);
        return allQuery.getResultList();
    }

	@Override
	public long findItemsLoadedBy(Long id) {
		CriteriaBuilder cb =  getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Check> checkRoot = cq.from(Check.class);
		cq.select(cb.count(checkRoot));
		cq.where(
			cb.equal(checkRoot.get(Check_.fileMetaData).get(FileMetaData_.id), id)
		);        
		cq.groupBy(checkRoot.get(Check_.fileMetaData).get(FileMetaData_.id));            
        TypedQuery<Long> allQuery = getEntityManager().createQuery(cq);
        return allQuery.getSingleResult();
	}
	
	@Override
	public List<Check> findAllByFileMetaDataId(Long fileMetaDataId) {
		CriteriaBuilder cb =  getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Check> cq = cb.createQuery(Check.class);
		Root<Check> checkRoot = cq.from(Check.class);
		cq.where(
			cb.equal(checkRoot.get(Check_.fileMetaData).get(FileMetaData_.id), fileMetaDataId)
		);
		TypedQuery<Check> allQuery = getEntityManager().createQuery(cq);
		return allQuery.getResultList();
	}
	
	@Override
	public List<Check> findAllChecksBySearchParametersMap(Map<String, String> searchParametersMap) {
		CriteriaBuilder criteriaBuilder =  getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Check> cq = criteriaBuilder.createQuery(Check.class);
		Root<Check> checkRoot = cq.from(Check.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		for (Map.Entry<String, String> entry : searchParametersMap.entrySet()) {
			if(entry.getKey().equalsIgnoreCase("Bank")) {
				predicates.add(criteriaBuilder.equal(criteriaBuilder.upper(checkRoot.get(Check_.digest)), entry.getValue().toUpperCase()));
			}
		}
		cq.where(predicates.toArray(new Predicate[0]));
		TypedQuery<Check> allQuery = getEntityManager().createQuery(cq);
        return allQuery.getResultList();
	}

    @Override
    public List<Check> finalAllChecksByDigest(List<String> digestList) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Check> cq = cb.createQuery(Check.class);
        Root<Check> rootEntry = cq.from(Check.class);
        Fetch<Check, ItemType> itemType= rootEntry.fetch(Check_.itemType, JoinType.LEFT);
        cq.select(rootEntry).where(rootEntry.get(Check_.digest).in(digestList));
        TypedQuery<Check> allQuery = getEntityManager().createQuery(cq);
        return allQuery.getResultList();
    }
    
    /** Fix for WALPP-302,Need default to 'Unmatched' **/
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Check save(Check entity) {
	if (entity.getMatchStatus() == null) {
	    entity.setMatchStatus(Constants.UNMATCHED);
	}
        return super.save(entity);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Check update(Check entity) {
	if (entity.getMatchStatus() == null) {
	    entity.setMatchStatus(Constants.UNMATCHED);
	}
        return super.update(entity);
    }
    
    @Override
    public Check findCheckByCheckId(Long checkId) {
        Check check = null;
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<Check> cq = cb.createQuery(Check.class);
            Root<Check> rootEntry = cq.from(Check.class);
            rootEntry.fetch(Check_.checkStatus, JoinType.LEFT);
            cq.where(cb.equal(rootEntry.get(Check_.id), checkId));
            TypedQuery<Check> allQuery = getEntityManager().createQuery(cq);
            check = (Check) allQuery.getSingleResult();
        } catch (NoResultException nre) {
            logger.error("Exception occured at CheckJpaDao : findCheckByCheckId " + nre.getMessage(), nre);
        }
        return check;        
    }
}
