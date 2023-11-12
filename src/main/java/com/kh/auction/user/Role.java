package com.kh.auction.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Collections;
import java.util.Set;
@RequiredArgsConstructor
public enum Role {
    USER(Collections.emptySet()),
    ADMIN(Collections.emptySet())

    ;

    @Getter
    private final Set<Permission> permissions;
}
