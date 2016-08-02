package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * UserDetail: gduggirala
 * Date: 11/25/13
 * Time: 12:02 PM
 */
public class UserBankRoleId implements Serializable {
    public static final long serialVersionUID = 0x1L;
    private Long userDetail;
    private Long account;
    private Long role;

    public UserBankRoleId() {
    }

    public UserBankRoleId(Long userDetail, Long account, Long role) {
        this.userDetail = userDetail;
        this.account = account;
        this.role = role;
    }

    public Long getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(Long user) {
        this.userDetail = user;
    }

    public Long getAccount() {
        return account;
    }

    public void setAccount(Long account) {
        this.account = account;
    }

    public Long getRole() {
        return role;
    }

    public void setRole(Long role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserBankRoleId that = (UserBankRoleId) o;

        if (account != null ? !account.equals(that.account) : that.account != null) return false;
        if (role != null ? !role.equals(that.role) : that.role != null) return false;
        if (userDetail != null ? !userDetail.equals(that.userDetail) : that.userDetail != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userDetail != null ? userDetail.hashCode() : 0;
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }
}
