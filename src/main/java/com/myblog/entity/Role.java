package com.myblog.entity;

/**
 *              角色类
 *
 *
 */
public class Role {
    
    private Integer id;          //int(20) NOT NULL AUTO_INCREMENT,
    private String roleName;          //varchar(50) NULL,
    private String roleTag;
    private String roleDesc;          //varchar(50) NULL,

    //==========================


    public Role(Integer id, String roleNam, String roleTag, String roleDesc) {
        this.id = id;
        this.roleName = roleNam;
        this.roleTag = roleTag;
        this.roleDesc = roleDesc;
    }

    public String getRoleTag() {
        return roleTag;
    }

    public void setRoleTag(String roleTag) {
        this.roleTag = roleTag;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleNam() {
        return roleName;
    }

    public void setRoleNam(String roleNam) {
        this.roleName = roleNam;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", roleNam='" + roleName + '\'' +
                ", roleTag='" + roleTag + '\'' +
                ", roleDesc='" + roleDesc + '\'' +
                '}';
    }
}
