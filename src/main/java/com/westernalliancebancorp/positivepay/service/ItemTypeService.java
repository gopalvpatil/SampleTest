package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.googlecode.ehcache.annotations.Cacheable;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;

/**
 * Interface providing service methods to work with the ItemType Model
 * @author Anand Kumar
 */
public interface ItemTypeService {
	ItemType update(ItemType itemType);
	ItemType save(ItemType itemType);
	void delete(ItemType itemType);
	ItemType findById(Long id);
	List<ItemType> findAll();
	List<ItemType> findAllActiveItemTypes();
	ItemType findByCode(String itemCode);

    @Cacheable(cacheName = "re")
    String getItemCodeFromReferenceDataItemType(ReferenceData.ITEM_TYPE item_type);
}