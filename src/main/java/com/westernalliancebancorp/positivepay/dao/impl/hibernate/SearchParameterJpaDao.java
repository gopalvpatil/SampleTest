package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.SearchParameterDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.SearchParameter;
import com.westernalliancebancorp.positivepay.model.SearchParameter_;

/**
 * 
 * @author Anand Kumar
 *
 */
@Repository
public class SearchParameterJpaDao extends GenericJpaDao<SearchParameter, Long> implements SearchParameterDao {
    @Override
    public SearchParameter findByName(String searchParameterName) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchParameter> searchParameterCriteriaQuery = criteriaBuilder.createQuery(SearchParameter.class);
        Root<SearchParameter> searchParameterRoot = searchParameterCriteriaQuery.from(SearchParameter.class);

        Predicate condition = criteriaBuilder.equal(searchParameterRoot.get(SearchParameter_.name), searchParameterName);
        searchParameterCriteriaQuery.where(condition);
        TypedQuery<SearchParameter> searchParameterTypedQuery = entityManager.createQuery(searchParameterCriteriaQuery);
        return searchParameterTypedQuery.getSingleResult();
    }
    
    @Override
    public Map<Long, String> getIdNameMap() {
    	Map<Long, String> idNameMap = new HashMap<Long, String>();
    	List<SearchParameter> searchParamList = super.findAll();
    	for(SearchParameter searchParam : searchParamList) {
    		idNameMap.put(searchParam.getId(), searchParam.getName());
    	}
    	return idNameMap;
    }
}
