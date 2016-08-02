/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao.common;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides the generic common implementations of GenericDao interface persistence methods
 * Extend this abstract class to implement DAO for your specific needs
 * @author Anand Kumar
 *
 */
@Repository
public abstract class GenericJpaDao<T, ID extends Serializable> implements GenericDao<T, ID> {
	
	private Class<T> persistentClass;
    @PersistenceContext
	private EntityManager entityManager;
    
    @Value("${batch.insert.size}")
	private int batchInsertSize;
	
	public GenericJpaDao() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
	}

	protected EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}
	
	@Transactional
	public T update(T entity) {
		T mergedEntity = getEntityManager().merge(entity);
        getEntityManager().flush();
		return mergedEntity;
	}
	
	@Transactional
	public T save(T entity) {
		getEntityManager().persist(entity);
        getEntityManager().flush();
		return entity;
	}
	
	@Transactional
	public void delete(T entity) {
        entity = getEntityManager().merge(entity);
        getEntityManager().remove(entity);

	}

	@Transactional(readOnly=true)
	public T findById(ID id) {
		T entity = (T) getEntityManager().find(getPersistentClass(), id);
		return entity;
	}
	
	@Transactional(readOnly=true)
	public T getReference(ID id) {
		T entity = (T) getEntityManager().getReference(getPersistentClass(), id);
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<T> findAll() {
		return getEntityManager().createQuery("select x from "+getPersistentClass().getSimpleName()+ " x").getResultList();
	}
	
	@Transactional
	public List<T> saveAll(List<T> entities) {
		List<T> savedEntities = new ArrayList<T>();
		int counter = 0;
		for(T entity: entities) {
			counter++;
			getEntityManager().persist(entity);
	        savedEntities.add(entity);
	        if ( counter % batchInsertSize == 0 ) {   
	        	getEntityManager().flush();    
	        }  
		}
		return savedEntities;
	}
}
