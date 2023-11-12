create table permission
(
    id   varchar(255) not null primary key,
    name varchar(255)
);

create table role
(
    id   varchar(255) not null primary key,
    name varchar(255)
);

create table "user"
(
    id                      uuid    not null primary key,
    created_at              timestamp(6),
    updated_at              timestamp(6),
    account_expired         boolean not null,
    account_locked          boolean not null,
    credentials_expired     boolean not null,
    email                   varchar(255) unique,
    enabled                 boolean not null,
    last_login_date         timestamp(6),
    last_login_date_display timestamp(6),
    name                    varchar(25),
    password                varchar(255),
    profile_image_url       varchar(255),
    status                  varchar(255),
    surname                 varchar(25),
    username                varchar(20) unique,
    verified                boolean not null
);

create table token
(
    id          bigint not null primary key,
    expiry_date timestamp(6),
    token       varchar(255) unique,
    "type"      varchar(255),
    user_id     uuid   not null unique,
    constraint "FK_token_user" foreign key (user_id) references "user" (id)
);

create table roles_permissions
(
    role_id       varchar(255) not null,
    permission_id varchar(255) not null,
    constraint "FK_roles_permissions_permission_id" foreign key (permission_id) references permission (id),
    constraint "FK_roles_permissions_role_id" foreign key (role_id) references role (id)
);

create table users_roles
(
    user_id uuid         not null,
    role_id varchar(255) not null,
    constraint "FK_users_roles_role_id" foreign key (role_id) references role (id),
    constraint "FK_users_roles_user_id" foreign key (user_id) references "user" (id)
);

create sequence token_seq increment by 50;