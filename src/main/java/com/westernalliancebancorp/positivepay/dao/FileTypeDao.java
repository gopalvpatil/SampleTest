package com.westernalliancebancorp.positivepay.dao;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.FileType;

/**
 * User: moumita
 * Date: 13/5/14
 * Time: 3:59 PM
 */
public interface FileTypeDao extends GenericDao<FileType, Long> {
	FileType findByName(FileType.FILE_TYPE name);
}
