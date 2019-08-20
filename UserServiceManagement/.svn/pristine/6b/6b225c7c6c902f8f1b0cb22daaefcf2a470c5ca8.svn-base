package com.brs.userservicemanagement.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Roles", schema = "UserServiceDB")
public class Roles {
	@Id
	@Column(name = "role_id")
	private Integer roleId;
	@Column(name = "role_name")
	private String role;
	@ManyToMany(mappedBy = "userRoles")
	private List<LoginDetails> loginDetails;

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
