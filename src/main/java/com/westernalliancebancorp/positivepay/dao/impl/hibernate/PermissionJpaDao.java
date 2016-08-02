package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import com.westernalliancebancorp.positivepay.model.*;
import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.PermissionDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;

/**
 * @author Moumita Ghosh
 *
 */
@Repository
public class PermissionJpaDao extends GenericJpaDao<Permission, Long> implements PermissionDao{


	 @Override
	    public List<Permission> findResourcesByUser(String userName) {
	        EntityManager entityManager = this.getEntityManager();
	        CriteriaBuilder criteriaBuilder=entityManager.getCriteriaBuilder();
	        CriteriaQuery<Permission>criteriaQuery=criteriaBuilder.createQuery(Permission.class);
	        Metamodel metamodel = entityManager.getMetamodel();
	        EntityType<Permission> entityType = metamodel.entity(Permission.class);
	        Root<Permission> root = criteriaQuery.from(entityType);
	        criteriaQuery.select(root);

	        Subquery<Long>subquery=criteriaQuery.subquery(Long.class);
	        Root<Permission> subRoot = subquery.from(Permission.class);
	        subquery.select(subRoot.get(Permission_.id));
	        SetJoin<Permission, UserDetail> join = subRoot.join(Permission_.userDetails, JoinType.INNER);

	        ParameterExpression<String> parameterExpression=criteriaBuilder.parameter(String.class);
	        criteriaQuery.where(criteriaBuilder.in(root.get(Permission_.id)).value(subquery));
	        subquery.where(criteriaBuilder.equal(join.get(UserDetail_.userName), parameterExpression));

	        TypedQuery<Permission> typedQuery = entityManager.createQuery(criteriaQuery);
	        List<Permission> list = typedQuery.setParameter(parameterExpression, userName).getResultList();
	        return list;
	    }

	@Override
	public List<Permission> findResourcesByUserAndType(String userName,
			Permission.TYPE type) {
		EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder=entityManager.getCriteriaBuilder();
        CriteriaQuery<Permission>criteriaQuery=criteriaBuilder.createQuery(Permission.class);
        Metamodel metamodel = entityManager.getMetamodel();
        EntityType<Permission> entityType = metamodel.entity(Permission.class);
        Root<Permission> root = criteriaQuery.from(entityType);
        criteriaQuery.select(root);

        Subquery<Long>subquery=criteriaQuery.subquery(Long.class);
        Root<Permission> subRoot = subquery.from(Permission.class);
        subquery.select(subRoot.get(Permission_.id));
        SetJoin<Permission, UserDetail> join = subRoot.join(Permission_.userDetails, JoinType.INNER);

        ParameterExpression<String> parameterExpression=criteriaBuilder.parameter(String.class);
        ParameterExpression<Permission.TYPE> parameterExpressionForResource=criteriaBuilder.parameter(Permission.TYPE.class);
        criteriaQuery.where(criteriaBuilder.in(root.get(Permission_.id)).value(subquery));
        Predicate userNamePredicate = criteriaBuilder.equal(join.get(UserDetail_.userName), parameterExpression);
        Predicate resourceNamePredicate = criteriaBuilder.equal(root.get(Permission_.type), parameterExpressionForResource);
        subquery.where(criteriaBuilder.and(userNamePredicate,resourceNamePredicate));
        TypedQuery<Permission> typedQuery = entityManager.createQuery(criteriaQuery);
        
        typedQuery.setParameter(parameterExpression, userName);
        typedQuery.setParameter(parameterExpressionForResource, type);
        List<Permission> list = typedQuery.getResultList();
         
        return list;
	}

    @Override
    public List<Permission> findResourcesByUserAndResourceName(String userName,
                                                               String resourceName) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder=entityManager.getCriteriaBuilder();
        CriteriaQuery<Permission>criteriaQuery=criteriaBuilder.createQuery(Permission.class);
        Metamodel metamodel = entityManager.getMetamodel();
        EntityType<Permission> entityType = metamodel.entity(Permission.class);
        Root<Permission> root = criteriaQuery.from(entityType);
        criteriaQuery.select(root);

        Subquery<Long>subquery=criteriaQuery.subquery(Long.class);
        Root<Permission> subRoot = subquery.from(Permission.class);
        subquery.select(subRoot.get(Permission_.id));
        SetJoin<Permission, UserDetail> join = subRoot.join(Permission_.userDetails, JoinType.INNER);

        ParameterExpression<String> parameterExpression=criteriaBuilder.parameter(String.class);
        ParameterExpression<String> parameterExpressionForResource=criteriaBuilder.parameter(String.class);
        criteriaQuery.where(criteriaBuilder.in(root.get(Permission_.id)).value(subquery));
        Predicate userNamePredicate = criteriaBuilder.equal(join.get(UserDetail_.userName), parameterExpression);
        Predicate resourceNamePredicate = criteriaBuilder.equal(root.get(Permission_.name), parameterExpressionForResource);
        subquery.where(criteriaBuilder.and(userNamePredicate,resourceNamePredicate));
        TypedQuery<Permission> typedQuery = entityManager.createQuery(criteriaQuery);

        typedQuery.setParameter(parameterExpression, userName);
        typedQuery.setParameter(parameterExpressionForResource, resourceName);
        List<Permission> list = typedQuery.getResultList();

        return list;
    }

    @Override
    public List<Permission> findByRoleId(Long roleId) {
        EntityManager entityManager = this.getEntityManager();
        Query q = entityManager.createQuery("FROM Role as r inner join fetch r.permissions WHERE r.id = ?1");
        q.setParameter(1, roleId);
       // System.out.println("1:"+q.getResultList());
        List<Permission> permissionList = q.getResultList();
        return permissionList;
    }
}
