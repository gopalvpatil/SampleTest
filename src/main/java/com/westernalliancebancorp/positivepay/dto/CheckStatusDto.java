package com.westernalliancebancorp.positivepay.dto;

/**
 * User: gduggirala
 * Date: 4/6/14
 * Time: 11:17 AM
 */
public class CheckStatusDto {
    private String name;
    private String description;
    private Integer version;
    private Long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
