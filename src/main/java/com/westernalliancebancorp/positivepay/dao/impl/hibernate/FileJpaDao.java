/**
 *
 */
package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.FileDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.AuditInfo_;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.Company_;
import com.westernalliancebancorp.positivepay.model.FileMapping;
import com.westernalliancebancorp.positivepay.model.FileMapping_;
import com.westernalliancebancorp.positivepay.model.FileMetaData;
import com.westernalliancebancorp.positivepay.model.FileMetaData_;
import com.westernalliancebancorp.positivepay.model.FileType;
import com.westernalliancebancorp.positivepay.model.FileType_;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

/**
 * Data access object JPA impl to work with FileMetaData model database operations.
 * @author Anand Kumar
 *
 */
@Repository
public class FileJpaDao extends GenericJpaDao<FileMetaData, Long> implements FileDao {
    @Loggable
    private Logger logger;

	public FileJpaDao() {
        super();
	}

	//More specific method implementations to follow for FileMetaData entity
	/**
	 * This method returns the list of FileMetaData based on originalFileName and checksum
	 * This comes handy while checking if a duplicate file is being uploaded.
	 * @param originalFileName
	 * @param checksum
	 * @return list of FileMetaData objects.
	 */
	public List<FileMetaData> findByOriginalFileNameAndChecksum(String originalFileName, String checksum) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<FileMetaData> criteriaQuery = criteriaBuilder.createQuery(FileMetaData.class);
		Root<FileMetaData> rootEntry = criteriaQuery.from(FileMetaData.class);
		criteriaQuery.where(
			criteriaBuilder.equal(rootEntry.get(FileMetaData_.originalFileName), originalFileName),
			criteriaBuilder.equal(rootEntry.get(FileMetaData_.checksum), checksum)
		  );
        TypedQuery<FileMetaData> allQuery = getEntityManager().createQuery(criteriaQuery);
        return allQuery.getResultList();
	}
	
	public List<FileMetaData> findByChecksum(String checksum) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<FileMetaData> criteriaQuery = criteriaBuilder.createQuery(FileMetaData.class);
		Root<FileMetaData> rootEntry = criteriaQuery.from(FileMetaData.class);
		criteriaQuery.where(
			criteriaBuilder.equal(rootEntry.get(FileMetaData_.checksum), checksum)
		  );
        TypedQuery<FileMetaData> allQuery = getEntityManager().createQuery(criteriaQuery);
        return allQuery.getResultList();
	}

	public List<FileMetaData> findAllFilesUploadedForUserCompany(Company userCompany) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<FileMetaData> criteriaQuery = criteriaBuilder.createQuery(FileMetaData.class);
		Root<FileMetaData> rootEntry = criteriaQuery.from(FileMetaData.class);
		Fetch<FileMetaData, FileMapping> fileMapping = rootEntry.fetch(FileMetaData_.fileMapping, JoinType.LEFT);
		fileMapping.fetch(FileMapping_.company, JoinType.LEFT);
		criteriaQuery.where(
				criteriaBuilder.equal(rootEntry.get(FileMetaData_.fileMapping).get(FileMapping_.company).get(Company_.id), userCompany.getId()),
                criteriaBuilder.equal(rootEntry.get(FileMetaData_.fileType).get(FileType_.name), FileType.FILE_TYPE.CUSTOMER_UPLOAD)
        );
		criteriaQuery.orderBy(criteriaBuilder.desc(rootEntry.get(FileMetaData_.auditInfo).get(AuditInfo_.dateCreated)));
        TypedQuery<FileMetaData> allQuery = getEntityManager().createQuery(criteriaQuery);
        //return allQuery.setMaxResults(maxResults).getResultList();
        return allQuery.getResultList();
	}
	
	public List<FileMetaData> findRecentFilesUploadedForUserCompany(int maxResults, Company userCompany) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<FileMetaData> criteriaQuery = criteriaBuilder.createQuery(FileMetaData.class);
		Root<FileMetaData> rootEntry = criteriaQuery.from(FileMetaData.class);
		Fetch<FileMetaData, FileMapping> fileMapping = rootEntry.fetch(FileMetaData_.fileMapping, JoinType.LEFT);
		fileMapping.fetch(FileMapping_.company, JoinType.LEFT);
		criteriaQuery.where(
                criteriaBuilder.equal(rootEntry.get(FileMetaData_.fileMapping).get(FileMapping_.company).get(Company_.id), userCompany.getId()),
                criteriaBuilder.equal(rootEntry.get(FileMetaData_.fileType).get(FileType_.name), FileType.FILE_TYPE.CUSTOMER_UPLOAD)
        );
		criteriaQuery.orderBy(criteriaBuilder.desc(rootEntry.get(FileMetaData_.auditInfo).get(AuditInfo_.dateCreated)));
        TypedQuery<FileMetaData> allQuery = getEntityManager().createQuery(criteriaQuery);
        return allQuery.setMaxResults(maxResults).getResultList();
	}

    @Override
    public FileMetaData findByFileName(String mappedFileName) {
        FileMetaData fileMetaData = null;
        try {
            CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<FileMetaData> criteriaQuery = criteriaBuilder.createQuery(FileMetaData.class);
            Root<FileMetaData> rootEntry = criteriaQuery.from(FileMetaData.class);
            criteriaQuery.where(
                    criteriaBuilder.equal(rootEntry.get(FileMetaData_.fileName), mappedFileName)
            );
            TypedQuery<FileMetaData> allQuery = getEntityManager().createQuery(criteriaQuery);
            return allQuery.getSingleResult();
        } catch (NoResultException nre) {
            logger.error("Exception occured at CheckJpaDao : findCheckBy " + nre.getMessage(), nre);
        }
        return fileMetaData;
    }

	@Override
	public List<FileMetaData> findDashboardFileMetaData(String companyName, FileMetaData.STATUS status, Date dateCreated, boolean isForDay) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<FileMetaData> criteriaQuery = criteriaBuilder.createQuery(FileMetaData.class);
		Root<FileMetaData> fileMetaDateRoot = criteriaQuery.from(FileMetaData.class);		

		Fetch<FileMetaData, FileMapping> fileMapping = fileMetaDateRoot.fetch(FileMetaData_.fileMapping, JoinType.LEFT);
		fileMapping.fetch(FileMapping_.company, JoinType.LEFT);
		
		Predicate companyNamePredicate = null;
		Predicate statusPredicate = null;
		Predicate lastDateCreatedPredicate = null;
		Predicate startDateCreatedPredicate = null;
        Predicate fileMetadataNotNull = null;
		Date begining, end;
        fileMetadataNotNull = criteriaBuilder.isNotNull(fileMetaDateRoot.get(FileMetaData_.fileMapping));
		if(companyName != null) {
			companyNamePredicate = criteriaBuilder.equal(fileMetaDateRoot.get(FileMetaData_.fileMapping).get(FileMapping_.company).get(Company_.name), companyName);			
		}else{
			companyNamePredicate = criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
		}
		
		if(status != null) {
			statusPredicate = criteriaBuilder.equal(fileMetaDateRoot.get(FileMetaData_.status), status);
		}else{
			statusPredicate = criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
		}		
		if(dateCreated != null) {
			if(isForDay) {
				begining = DateUtils.getBeginningOfDayTime(dateCreated);
				end = DateUtils.getEndOfDayTime(dateCreated);	
				lastDateCreatedPredicate = criteriaBuilder.greaterThanOrEqualTo(fileMetaDateRoot.get(FileMetaData_.auditInfo).get(AuditInfo_.dateCreated), begining);
				startDateCreatedPredicate = criteriaBuilder.lessThanOrEqualTo(fileMetaDateRoot.get(FileMetaData_.auditInfo).get(AuditInfo_.dateCreated), end);
			}else{
				begining = DateUtils.getBeginningOfDayTime(dateCreated);
				end = DateUtils.getEndOfDayTime(new Date());
				lastDateCreatedPredicate = criteriaBuilder.greaterThanOrEqualTo(fileMetaDateRoot.get(FileMetaData_.auditInfo).get(AuditInfo_.dateCreated), begining);
				startDateCreatedPredicate = criteriaBuilder.lessThanOrEqualTo(fileMetaDateRoot.get(FileMetaData_.auditInfo).get(AuditInfo_.dateCreated), end);
			}
		}else{
			lastDateCreatedPredicate = criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
			startDateCreatedPredicate = criteriaBuilder.equal(criteriaBuilder.literal(1), 1);
		}
		//file type predicate
		Predicate fileTypePredicate = criteriaBuilder.equal(fileMetaDateRoot.get(FileMetaData_.fileType).get(FileType_.name), FileType.FILE_TYPE.CUSTOMER_UPLOAD);
		criteriaQuery.where(companyNamePredicate, statusPredicate, lastDateCreatedPredicate, startDateCreatedPredicate, fileMetadataNotNull, fileTypePredicate);
		
		criteriaQuery.orderBy(criteriaBuilder.desc(fileMetaDateRoot.get(FileMetaData_.auditInfo).get(AuditInfo_.dateCreated)));
		
        TypedQuery<FileMetaData> allQuery = getEntityManager().createQuery(criteriaQuery);
        return allQuery.getResultList();
	}
}
