package com.westernalliancebancorp.positivepay.web.security;

import com.westernalliancebancorp.positivepay.model.Permission;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Set;

/**
 * PositivePayGrantedAuthority is
 *
 * @author Giridhar Duggirala
 */

public interface PositivePayGrantedAuthority extends GrantedAuthority {
    Long getAccount();
}
