package com.westernalliancebancorp.positivepay.web.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.westernalliancebancorp.positivepay.model.Account;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.ppolicy.PasswordPolicyControl;
import org.springframework.security.ldap.ppolicy.PasswordPolicyResponseControl;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.service.BankService;

/**
 * The context mapper used by the LDAP authentication provider to create an LDAP user object.
 * User:	Gopal Patil
 * Date:	Feb 12, 2014
 * Time:	6:00:11 PM
 */
public class PositivePayLdapUserDetailsMapper implements UserDetailsContextMapper {

    private final Log logger = LogFactory.getLog(PositivePayLdapUserDetailsMapper.class);
    private String passwordAttributeName = "userPassword";
    private String rolePrefix = "ROLE_";
    private String[] roleAttributes = null;
    private boolean convertToUpperCase = true;
    private String ldapUserRole = "ROLE_BANK_ADMIN";
    @Autowired
    private BankService bankService;

    /* (non-Javadoc)
     * @see org.springframework.security.ldap.userdetails.UserDetailsContextMapper#mapUserFromContext(org.springframework.ldap.core.DirContextOperations, java.lang.String, java.util.Collection)
     */
    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx,
                                          String username, Collection<? extends GrantedAuthority> authorities) {
        String dn = ctx.getNameInNamespace();

        logger.debug("Mapping user details from context with DN: " + dn);

        LdapUserDetailsImpl.Essence essence = new LdapUserDetailsImpl.Essence();
        essence.setDn(dn);

        Object passwordValue = ctx.getObjectAttribute(passwordAttributeName);

        if (passwordValue != null) {
            essence.setPassword(mapPassword(passwordValue));
        }

        essence.setUsername(username);

        // Map the roles
        for (int i = 0; (roleAttributes != null) && (i < roleAttributes.length); i++) {
            String[] rolesForAttribute = ctx.getStringAttributes(roleAttributes[i]);

            if (rolesForAttribute == null) {
                logger.debug("Couldn't read role attribute '" + roleAttributes[i] + "' for user " + dn);
                continue;
            }

            for (String role : rolesForAttribute) {
                GrantedAuthority authority = createAuthority(role);

                if (authority != null) {
                    essence.addAuthority(authority);
                }
            }
        }
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<GrantedAuthority>();
        // Add the supplied authorities
        //Expecting only one role for the LDAP user.
        for (GrantedAuthority authority : authorities) {
            essence.addAuthority(authority);
            SimpleGrantedAuthority grantedAuthority = (SimpleGrantedAuthority) authority;
            GrantedAuthority ppGrantedAuthority= new SimpleGrantedAuthority(grantedAuthority.getAuthority());
            grantedAuthorityList.add(ppGrantedAuthority);

        }

        // Check for PPolicy data
        PasswordPolicyResponseControl ppolicy = (PasswordPolicyResponseControl) ctx.getObjectAttribute(PasswordPolicyControl.OID);

        if (ppolicy != null) {
            essence.setTimeBeforeExpiration(ppolicy.getTimeBeforeExpiration());
            essence.setGraceLoginsRemaining(ppolicy.getGraceLoginsRemaining());
        }
        essence.createUserDetails();
        return new PositivePayUser(username, " ", grantedAuthorityList);
    }

    /* (non-Javadoc)
     * @see org.springframework.security.ldap.userdetails.UserDetailsContextMapper#mapUserToContext(org.springframework.security.core.userdetails.UserDetails, org.springframework.ldap.core.DirContextAdapter)
     */
    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        throw new UnsupportedOperationException("LdapUserDetailsMapper only supports reading from a context. Please" +
                "use a subclass if mapUserToContext() is required.");
    }

    /**
     * Extension point to allow customized creation of the user's password from
     * the attribute stored in the directory.
     *
     * @param passwordValue the value of the password attribute
     * @return a String representation of the password.
     */
    protected String mapPassword(Object passwordValue) {

        if (!(passwordValue instanceof String)) {
            // Assume it's binary
            passwordValue = new String((byte[]) passwordValue);
        }

        return (String) passwordValue;

    }

    /**
     * Creates a GrantedAuthority from a role attribute. Override to customize
     * authority object creation.
     * <p>
     * The default implementation converts string attributes to roles, making use of the <tt>rolePrefix</tt>
     * and <tt>convertToUpperCase</tt> properties. Non-String attributes are ignored.
     * </p>
     *
     * @param role the attribute returned from
     * @return the authority to be added to the list of authorities for the user, or null
     *         if this attribute should be ignored.
     */
    protected GrantedAuthority createAuthority(Object role) {
        if (role instanceof String) {
            if (convertToUpperCase) {
                role = ((String) role).toUpperCase();
            }
            //return new SimpleGrantedAuthority(rolePrefix + role);
            return new SimpleGrantedAuthority(rolePrefix + role);
        }
        return null;
    }

    /**
     * @return the passwordAttributeName
     */
    public String getPasswordAttributeName() {
        return passwordAttributeName;
    }

    /**
     * @param passwordAttributeName the passwordAttributeName to set
     */
    public void setPasswordAttributeName(String passwordAttributeName) {
        this.passwordAttributeName = passwordAttributeName;
    }

    /**
     * @return the rolePrefix
     */
    public String getRolePrefix() {
        return rolePrefix;
    }

    /**
     * @param rolePrefix the rolePrefix to set
     */
    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    /**
     * @return the roleAttributes
     */
    public String[] getRoleAttributes() {
        return roleAttributes;
    }

    /**
     * @param roleAttributes the roleAttributes to set
     */
    public void setRoleAttributes(String[] roleAttributes) {
        this.roleAttributes = roleAttributes;
    }

    /**
     * @return the convertToUpperCase
     */
    public boolean isConvertToUpperCase() {
        return convertToUpperCase;
    }

    /**
     * @param convertToUpperCase the convertToUpperCase to set
     */
    public void setConvertToUpperCase(boolean convertToUpperCase) {
        this.convertToUpperCase = convertToUpperCase;
    }

    public String getLdapUserRole() {
        return ldapUserRole;
    }

    public void setLdapUserRole(String ldapUserRole) {
        this.ldapUserRole = ldapUserRole;
    }
}
