package com.westernalliancebancorp.positivepay.dto;

import java.util.Arrays;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * UserDto is
 *
 * @author Giridhar Duggirala
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserDto {
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String email;
    private boolean isActive;
    private boolean locked;
    private String accountNumber;
    private Long bankId;
    private Long companyId;
    private Long userId;
    private Long roleId;
    private String baseRole; 
    
    private String userActivityTime;
    private String userActivityDate;
    private String userActivityName;
    private String userSystemComments;

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
    
    public String getBaseRole() {
		return baseRole;
	}

	public void setBaseRole(String baseRole) {
		this.baseRole = baseRole;
	}

	public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

	public String getUserActivityTime() {
		return userActivityTime;
	}

	public void setUserActivityTime(String userActivityTime) {
		this.userActivityTime = userActivityTime;
	}

	public String getUserActivityDate() {
		return userActivityDate;
	}

	public void setUserActivityDate(String userActivityDate) {
		this.userActivityDate = userActivityDate;
	}

	public String getUserActivityName() {
		return userActivityName;
	}

	public void setUserActivityName(String userActivityName) {
		this.userActivityName = userActivityName;
	}

	public String getUserSystemComments() {
		return userSystemComments;
	}

	public void setUserSystemComments(String userSystemComments) {
		this.userSystemComments = userSystemComments;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserDto [firstName=" + firstName + ", lastName=" + lastName
				+ ", userName=" + userName + ", password=" + password
				+ ", email=" + email + ", isActive=" + isActive + ", locked="
				+ locked + ", accountNumber=" + accountNumber + ", bankId="
				+ bankId + ", userId=" + userId + ", roleId="
				+ roleId + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDto other = (UserDto) obj;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
}
