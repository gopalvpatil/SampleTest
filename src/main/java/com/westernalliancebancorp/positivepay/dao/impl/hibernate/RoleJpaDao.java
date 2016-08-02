package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import com.westernalliancebancorp.positivepay.model.Permission;
import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.RoleDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.Role;
import com.westernalliancebancorp.positivepay.model.Role_;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * UserDetail: gduggirala
 * Date: 11/25/13
 * Time: 2:42 PM
 */
@Repository
public class RoleJpaDao extends GenericJpaDao<Role, Long> implements RoleDao {
    @Override
    public Role findByName(String roleName) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> roleCriteriaQuery = criteriaBuilder.createQuery(Role.class);
        Root<Role> roleRoot = roleCriteriaQuery.from(Role.class);

        Predicate condition = criteriaBuilder.like(roleRoot.get(Role_.name), roleName);
        roleCriteriaQuery.where(condition);
        TypedQuery<Role> roleTypedQuery = entityManager.createQuery(roleCriteriaQuery);
        return roleTypedQuery.getSingleResult();
    }
    
    @Override
    public Role findRoleBy(Long roleId) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> roleCriteriaQuery = criteriaBuilder.createQuery(Role.class);
        Root<Role> roleRoot = roleCriteriaQuery.from(Role.class);
        roleRoot.fetch(Role_.permissions, JoinType.LEFT);
        Predicate condition = criteriaBuilder.equal(roleRoot.get(Role_.id), roleId);
        roleCriteriaQuery.where(condition);
        TypedQuery<Role> roleTypedQuery = entityManager.createQuery(roleCriteriaQuery);
        return roleTypedQuery.getSingleResult();
    }

    @Override
    public Set<Permission> getRolePermissions(Long roleId) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> roleCriteriaQuery = criteriaBuilder.createQuery(Role.class);
        Root<Role> roleRoot = roleCriteriaQuery.from(Role.class);
        roleRoot.fetch(Role_.permissions, JoinType.LEFT);
        Predicate condition = criteriaBuilder.equal(roleRoot.get(Role_.id), roleId);
        roleCriteriaQuery.where(condition);
        TypedQuery<Role> roleTypedQuery = entityManager.createQuery(roleCriteriaQuery);
        Role role =  roleTypedQuery.getSingleResult();
        return role.getPermissions();
    }
}
