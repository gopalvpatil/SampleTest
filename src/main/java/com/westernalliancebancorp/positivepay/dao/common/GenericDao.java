/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao.common;

import java.io.Serializable;
import java.util.List;

/**
 * Generic Interface for Data Access Objects. Must be implemented or extended
 * Contains common persistence methods.
 * @author <a href="mailto:akumar1@intraedge.com">Anand Kumar</a>
 *
 */
public interface GenericDao<T, ID extends Serializable> {
	T update(T entity);
	T save(T entity);
	void delete(T entity);
	T findById(ID id);
	T getReference(ID id);
	List<T> findAll();	
	List<T> saveAll(List<T> entities);
}
