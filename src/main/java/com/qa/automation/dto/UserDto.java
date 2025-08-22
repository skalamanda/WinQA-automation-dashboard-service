package com.qa.automation.dto;

import java.time.LocalDateTime;

public class UserDto {
    private String userName;
    private String password;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String token;
    private String permission;
    private Long userPermission;

    public UserDto(String userName, String password, LocalDateTime createdAt, String role, LocalDateTime updatedAt, Long userPermission) {
        this.userName = userName;
        this.password = password;
        this.createdAt = createdAt;
        this.role = role;
        this.updatedAt = updatedAt;
        this.userPermission = userPermission;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUserPermission() {
        return userPermission;
    }

    public void setUserPermission(Long userPermission) {
        this.userPermission = userPermission;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
