package com.example.productmanager.model;

public enum UserType {
    NORMAL(false, false, false),
    ADMIN(true, false, true),
    OWNER(true, true, true);

    public final boolean canManageUsers;
    public final boolean canManageAdmins;
    public final boolean canManageProducts;

    UserType(boolean canManageUsers, boolean canManageAdmins, boolean canManageProducts) {
        this.canManageUsers = canManageUsers;
        this.canManageAdmins = canManageAdmins;
        this.canManageProducts = canManageProducts;
    }
}
