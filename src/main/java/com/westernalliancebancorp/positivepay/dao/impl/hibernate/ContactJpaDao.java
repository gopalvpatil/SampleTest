package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.ContactDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.Contact;

@Repository
public class ContactJpaDao extends GenericJpaDao<Contact, Long> implements ContactDao {


}
