package com.kh.auction.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    USER_READ("user:read"),
    ADMIN_READ("admin:read");

    @Getter
    private final String permission;

}
