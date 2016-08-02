/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.FileMapping;

/**
 * Data access object interface to work with File Mapping Object database operations
 * @author Anand Kumar
 *
 */
public interface FileMappingDao extends GenericDao<FileMapping, Long> {
	FileMapping findByCompanyIdAndFileMappingId(Long companyId, Long fileMappingId);
	List<FileMapping> findAllByCompanyId(Long companyId);
}
