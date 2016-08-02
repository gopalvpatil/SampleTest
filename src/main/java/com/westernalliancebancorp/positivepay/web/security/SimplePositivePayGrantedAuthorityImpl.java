package com.westernalliancebancorp.positivepay.web.security;

import com.westernalliancebancorp.positivepay.model.Permission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Basic concrete implementation of a {@link com.westernalliancebancorp.positivepay.web.security.PositivePayGrantedAuthority}.
 *
 * <p>
 * Stores a {@code String} representation of an authority  granted and Long id representation of the bank associated to the
 * {@link org.springframework.security.core.Authentication Authentication} object.
 *
 * @author Giridhar Duggirala
 */

public class SimplePositivePayGrantedAuthorityImpl implements PositivePayGrantedAuthority {
    private final Long accountId;
    private final String role;

    public SimplePositivePayGrantedAuthorityImpl(String role, Long accountId) {
        this.accountId = accountId;
        this.role=role;
    }

    @Override
    public Long getAccount() {
        return this.accountId;
    }

    @Override
    public String getAuthority() {
        return this.role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimplePositivePayGrantedAuthorityImpl that = (SimplePositivePayGrantedAuthorityImpl) o;

        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) return false;
        if (role != null ? !role.equals(that.role) : that.role != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = accountId != null ? accountId.hashCode() : 0;
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("SimplePositivePayGrantedAuthorityImpl{").append(", role='").append(role).
                append('\'').append("accountId=").append(accountId).append('}').toString();
    }
}
