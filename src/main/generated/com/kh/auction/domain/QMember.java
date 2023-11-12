package com.kh.auction.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1908434101L;

    public static final QMember member = new QMember("member1");

    public final StringPath addr = createString("addr");

    public final StringPath authority = createString("authority");

    public final StringPath birthday = createString("birthday");

    public final StringPath email = createString("email");

    public final StringPath id = createString("id");

    public final StringPath name = createString("name");

    public final StringPath nick = createString("nick");

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final NumberPath<Integer> point = createNumber("point", Integer.class);

    public final EnumPath<com.kh.auction.user.Role> role = createEnum("role", com.kh.auction.user.Role.class);

    public final DateTimePath<java.util.Date> singupDate = createDateTime("singupDate", java.util.Date.class);

    public final StringPath sphone = createString("sphone");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

