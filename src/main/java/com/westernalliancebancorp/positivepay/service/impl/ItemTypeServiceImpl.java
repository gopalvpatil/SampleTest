package com.westernalliancebancorp.positivepay.service.impl;

import java.util.List;

import com.googlecode.ehcache.annotations.Cacheable;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.ItemTypeDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.service.ItemTypeService;

@Service
public class ItemTypeServiceImpl implements ItemTypeService {
	
    @Loggable
    private Logger logger;
    @Autowired
    private ItemTypeDao itemTypeDao;
	
	/* (non-Javadoc)
	 * @see com.westernalliancebancorp.positivepay.service.ItemTypeService#update(com.westernalliancebancorp.positivepay.model.ItemType)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ItemType update(ItemType itemType) {
		return itemTypeDao.update(itemType);
	}

	/* (non-Javadoc)
	 * @see com.westernalliancebancorp.positivepay.service.ItemTypeService#save(com.westernalliancebancorp.positivepay.model.ItemType)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ItemType save(ItemType itemType) {
		return itemTypeDao.save(itemType);
	}

	/* (non-Javadoc)
	 * @see com.westernalliancebancorp.positivepay.service.ItemTypeService#delete(com.westernalliancebancorp.positivepay.model.ItemType)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(ItemType itemType) {
		itemTypeDao.delete(itemType);
	}

	/* (non-Javadoc)
	 * @see com.westernalliancebancorp.positivepay.service.ItemTypeService#findById(java.lang.Long)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ItemType findById(Long id) {
		return itemTypeDao.findById(id);
	}

	/* (non-Javadoc)
	 * @see com.westernalliancebancorp.positivepay.service.ItemTypeService#findAll()
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ItemType> findAll() {
		return itemTypeDao.findAll();
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ItemType> findAllActiveItemTypes() {
		return itemTypeDao.findAllActiveItemTypes();
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
    @Cacheable(cacheName = "itemCodeCache")
	public ItemType findByCode(String itemCode) {
		return itemTypeDao.findByCode(itemCode);
	}

    @Override
    @Cacheable(cacheName = "getItemCodeFromReferenceDataItemType")
    public String getItemCodeFromReferenceDataItemType(ReferenceData.ITEM_TYPE item_type){
        return (item_type.equals(ReferenceData.ITEM_TYPE.STOP) || item_type.equals(ReferenceData.ITEM_TYPE.STOP_PRESENTED))?"S":"P";
    }
}
