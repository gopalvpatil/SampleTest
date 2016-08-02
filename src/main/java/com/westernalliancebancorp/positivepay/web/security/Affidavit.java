package com.westernalliancebancorp.positivepay.web.security;


import com.westernalliancebancorp.positivepay.model.Permission;

import java.util.Collections;
import java.util.List;

/**
 * Affidavit is
 *
 * @author Giridhar Duggirala
 */

public class Affidavit {
    public enum TYPE{
        EMULATED{
            public String toString() {
                return "EMULATED";
            }
        }, NORMAL{
            public String toString() {
                return "NORMAL";
            }
        }
    }
    private String userName;
    private long ttl;
    private long maxTtl;
    private String uid;
    private String type = TYPE.NORMAL.toString();
    private String createdByUserName;
    private List<Permission> permissionList;

    public Affidavit(String userName, long ttl, long maxTtl, String type, List<Permission> permissionList) {
        this.userName = userName;
        this.ttl = ttl;
        this.maxTtl = maxTtl;
        this.type = type;
        this.permissionList = permissionList;
    }

    public Affidavit(String userName, long ttl, long maxTtl, String uid, String type, List<Permission> permissionList) {
        this.userName = userName;
        this.ttl = ttl;
        this.maxTtl = maxTtl;
        this.uid = uid;
        this.type = type;
        this.permissionList = permissionList;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {

        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public long getMaxTtl() {
        return maxTtl;
    }

    public void setMaxTtl(long maxTtl) {
        this.maxTtl = maxTtl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedByUserName() {
        return createdByUserName;
    }

    public void setCreatedByUserName(String createdByUserName) {
        this.createdByUserName = createdByUserName;
    }

    public List<Permission> getPermissionList() {
        return Collections.unmodifiableList(permissionList);
    }

    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }

    @Override
    public String toString() {
        return "Affidavit{" +
                "userName='" + userName + '\'' +
                ", ttl=" + ttl +
                ", maxTtl=" + maxTtl +
                ", uid='" + uid + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Affidavit affidavit = (Affidavit) o;

        if (maxTtl != affidavit.maxTtl) return false;
        if (ttl != affidavit.ttl) return false;
        if (!type.equals(affidavit.type)) return false;
        if (!userName.equals(affidavit.userName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userName.hashCode();
        result = 31 * result + (int) (ttl ^ (ttl >>> 32));
        result = 31 * result + (int) (maxTtl ^ (maxTtl >>> 32));
        result = 31 * result + type.hashCode();
        return result;
    }
}
