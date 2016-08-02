/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.ItemType;

/**
 * Data access object interface to work with Item Type database operations
 * @author Anand Kumar
 *
 */
public interface ItemTypeDao extends GenericDao<ItemType, Long> {
	List<ItemType> findAllActiveItemTypes();
    ItemType findByName(String itemName);
    ItemType findByCode(String itemCode);
}
