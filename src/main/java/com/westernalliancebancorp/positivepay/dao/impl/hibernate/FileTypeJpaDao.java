package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.googlecode.ehcache.annotations.Cacheable;
import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.FileTypeDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.exception.PositivePayRuleVoilationException;
import com.westernalliancebancorp.positivepay.model.FileType;
import com.westernalliancebancorp.positivepay.model.FileType_;

/**
 * Data access object JPA impl to work with File type model database operations.
 *
 * @author Moumita Ghosh
 */
@Repository
public class FileTypeJpaDao extends GenericJpaDao<FileType, Long> implements FileTypeDao {
    @Override
    @Cacheable(cacheName = "fileTypeByName")
    public FileType findByName(FileType.FILE_TYPE name) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<FileType> FileTypeCriteriaQuery = criteriaBuilder.createQuery(FileType.class);
        Root<FileType> FileTypeRoot = FileTypeCriteriaQuery.from(FileType.class);
        Predicate exceptionNameCondition = criteriaBuilder.equal(FileTypeRoot.get(FileType_.name), name);
        FileTypeCriteriaQuery.where(exceptionNameCondition);
        TypedQuery<FileType> FileTypeTypedQuery = entityManager.createQuery(FileTypeCriteriaQuery);
        List<FileType> resultList = FileTypeTypedQuery.getResultList();
        if (resultList != null && resultList.size() == 1) {
            return resultList.get(0);
        } else if (resultList == null) {
            return null;
        } else if (resultList.size() > 1) {
            throw new PositivePayRuleVoilationException(String.format("Not more than one status with same name %s should exist", name));
        }
        return null;
    }
}
