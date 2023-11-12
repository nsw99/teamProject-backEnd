package com.kh.auction.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QComments is a Querydsl query type for Comments
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QComments extends EntityPathBase<Comments> {

    private static final long serialVersionUID = 101162319L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QComments comments = new QComments("comments");

    public final NumberPath<Integer> auctionNo = createNumber("auctionNo", Integer.class);

    public final DateTimePath<java.util.Date> commentDate = createDateTime("commentDate", java.util.Date.class);

    public final NumberPath<Integer> commentNo = createNumber("commentNo", Integer.class);

    public final NumberPath<Integer> commentParent = createNumber("commentParent", Integer.class);

    public final StringPath content = createString("content");

    public final QMember memberId;

    public QComments(String variable) {
        this(Comments.class, forVariable(variable), INITS);
    }

    public QComments(Path<? extends Comments> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QComments(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QComments(PathMetadata metadata, PathInits inits) {
        this(Comments.class, metadata, inits);
    }

    public QComments(Class<? extends Comments> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.memberId = inits.isInitialized("memberId") ? new QMember(forProperty("memberId")) : null;
    }

}

