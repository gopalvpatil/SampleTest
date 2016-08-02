package com.westernalliancebancorp.positivepay.web.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * Models core user information retrieved by a {@link org.springframework.security.core.userdetails.UserDetailsService}.
 * <p/>
 * {@code equals} and {@code hashcode} implementations are based on the all the available properties as username property is unique.
 * We have  {@link AuthorityComparator}which compare the {@link com.westernalliancebancorp.positivepay.web.security.PositivePayGrantedAuthority} of the user.
 *
 * @author Giridhar Duggirala
 */

public class PositivePayUser implements UserDetails {
    private String password;
    private final String username;
    private final Set<GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;

    /**
     * Calls the more complex constructor with all boolean arguments set to {@code true}.
     */
    public PositivePayUser(String username, String password, Collection<GrantedAuthority> authorities) {
        this(username, password, true, true, true, true, authorities);
    }

    public PositivePayUser(String username, String password, boolean enabled, boolean accountNonExpired,
                           boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {

        if (((username == null) || "".equals(username)) || (password == null)) {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
    }

    private static SortedSet<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        // Ensure array iteration order is predictable (as per UserDetails.getAuthorities() contract and SEC-717)
        SortedSet<GrantedAuthority> sortedAuthorities =
                new TreeSet<GrantedAuthority>(new AuthorityComparator());

        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }

        return sortedAuthorities;
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {
        private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            // Neither should ever be null as each entry is checked before adding it to the set.
            // If the authority is null, it is a custom authority and should precede others.
            if (g1 instanceof GrantedAuthority && g2 instanceof GrantedAuthority) {
                GrantedAuthority pg1 = (GrantedAuthority) g1;
                GrantedAuthority pg2 = (GrantedAuthority) g2;
                if (g2.getAuthority() == null) {
                    return -1;
                }

                if (g1.getAuthority() == null) {
                    return 1;
                }
                /* if (pg1.getAuthority().equals(pg2.getAuthority())) {
                    return pg1.getAccount().compareTo(pg2.getAccount());
                } else if (pg1.getAccount().equals(pg2.getAccount())) {
                    return pg1.getAuthority().compareTo(pg2.getAuthority());
                } else {
                    return pg1.getAccount().compareTo(pg2.getAccount());
                }*/
                
                /*
                if(pg1.getAuthority().equals(pg2.getAuthority()) &&
                		pg1.getAccount().equals(pg2.getAccount()) ) {
                	return pg1.getAccount().compareTo(pg2.getAccount());
                }else if(pg1.getAuthority().equals(pg2.getAuthority()) ||
                		pg1.getAccount().equals(pg2.getAccount()) ) {
                	if(pg1.getAuthority().equals(pg2.getAuthority()) && 
                			pg1.getAccount().equals(pg2.getAccount())) {
                		return 1;
                	}else{
                		return pg1.getAccount().compareTo(pg2.getAccount());
                	}
                }else{
                	return pg1.getAccount().compareTo(pg2.getAccount());
                } 
                */
                
                return pg1.getAuthority().compareTo(pg2.getAuthority());
            }
            return (g1.getAuthority().compareTo(g2.getAuthority()));
        }
    }

    //~ Methods ========================================================================================================

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void eraseCredentials() {
        password = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PositivePayUser that = (PositivePayUser) o;

        if (accountNonExpired != that.accountNonExpired) return false;
        if (accountNonLocked != that.accountNonLocked) return false;
        if (credentialsNonExpired != that.credentialsNonExpired) return false;
        if (enabled != that.enabled) return false;
        if (authorities != null ? !authorities.equals(that.authorities) : that.authorities != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = password != null ? password.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (authorities != null ? authorities.hashCode() : 0);
        result = 31 * result + (accountNonExpired ? 1 : 0);
        result = 31 * result + (accountNonLocked ? 1 : 0);
        result = 31 * result + (credentialsNonExpired ? 1 : 0);
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("Username: ").append(this.username).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Enabled: ").append(this.enabled).append("; ");
        sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
        sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired).append("; ");
        sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");

        if (!authorities.isEmpty()) {
            sb.append("Granted Authorities: ");

            boolean first = true;
            for (GrantedAuthority auth : authorities) {
                GrantedAuthority grantedAuthority = (GrantedAuthority) auth;
                if (!first) {
                    sb.append(",");
                }
                first = false;

                sb.append(grantedAuthority.getAuthority());
            }
        } else {
            sb.append("Not granted any authorities");
        }

        return sb.toString();
    }
}
