CREATE DATABASE auth2_authorization_server;
/*
 static table
 초기에 값을 박아놓고 시작 하는 테이블
 주로 M:N 관계에서 상수 역할로 많이쓰임
 */

CREATE TABLE authorization_grant_type
(
    id   bigint PRIMARY KEY,
    name varchar(255) NOT NULL,
    CONSTRAINT unique_grant_type_name UNIQUE (name)
);


CREATE TABLE authentication_method
(
    id   bigserial PRIMARY KEY,
    name varchar(255) NOT NULL,
    CONSTRAINT unique_authentication_method_name UNIQUE (name)
);

CREATE TABLE role
(
    id            bigserial PRIMARY KEY,
    name          varchar(255) NOT NULL,
    is_super_user bool     DEFAULT FALSE,
    priority      smallint DEFAULT 0,
    CONSTRAINT unique_role_name UNIQUE (name)
);

CREATE TABLE scope
(
    id           bigserial PRIMARY KEY,
    uri_endpoint text,
    name         varchar(255),
    CONSTRAINT unique_scope_name UNIQUE (name)
);

/*
 main table
 주체가 되는 주요 테이블
 */

CREATE TABLE client
(
    id                       bigserial PRIMARY KEY,
    client_id                varchar(255) NOT NULL,
    client_id_issued_at      timestamp DEFAULT NOW(),
    client_secret            varchar(255) NOT NULL,
    client_secret_expires_at timestamp    NOT NULL,
    client_settings          json         NOT NULL,
    token_settings           json         NOT NULL,
    CONSTRAINT unique_client_id UNIQUE (client_id)
);

CREATE TABLE redirect_uri
(
    id        bigserial PRIMARY KEY,
    uri       text   NOT NULL,
    client_id bigint NOT NULL REFERENCES client (id),
    CONSTRAINT unique_client_uri UNIQUE (uri, client_id)
);


CREATE TABLE resource_owner
(
    id     varchar(50) PRIMARY KEY,
    name   varchar(255) NOT NULL,
    gender char(1)      NOT NULL,
    email  varchar(255) NOT NULL,
    CONSTRAINT unique_email UNIQUE (email)
);

CREATE TABLE credential
(
    id                bigserial PRIMARY KEY,
    credential_type   varchar(255) NOT NULL,
    resource_owner_id varchar(50)  NOT NULL REFERENCES resource_owner (id)
);


CREATE TABLE password
(
    credential_id bigint PRIMARY KEY REFERENCES credential (id),
    value         varchar(255) NOT NULL
);


CREATE TABLE authorization_consent
(
    id                bigserial PRIMARY KEY,
    client_id         bigint      NOT NULL REFERENCES client (id),
    resource_owner_id varchar(50) NOT NULL REFERENCES resource_owner (id),
    CONSTRAINT unique_consent UNIQUE (client_id, resource_owner_id)
);


CREATE TABLE oauth2_authorization
(
    id                          bigserial PRIMARY KEY,
    resource_owner_id           varchar(50) NOT NULL REFERENCES resource_owner (id),
    authorization_grant_type_id bigint      NOT NULL REFERENCES authorization_grant_type (id),
    attribute                   json         DEFAULT NULL,
    state                       varchar(500) DEFAULT NULL
);


CREATE TABLE token
(
    id               bigserial PRIMARY KEY,
    oauth2_authorization_id bigint    NOT NULL REFERENCES oauth2_authorization (id),
    metadata         json      DEFAULT NULL,
    value            text      NOT NULL,
    issued_at        timestamp DEFAULT NOW(),
    expired_at       timestamp DEFAULT NULL,
    token_type       varchar(255)
);

CREATE TABLE access_token
(
    token_id   bigint PRIMARY KEY REFERENCES token (id),
    token_type varchar(255) NOT NULL
);

CREATE TABLE refresh_token
(
    token_id bigint PRIMARY KEY REFERENCES token (id)
);

CREATE TABLE authorization_code
(
    token_id bigint PRIMARY KEY REFERENCES token (id)
);

CREATE TABLE oidc_id_token
(
    token_id bigint PRIMARY KEY REFERENCES token (id),
    claims   json NOT NULL
);

/*
 mapping tables
 M:N 관계를 표현하기 위한 테이블들
 */

CREATE TABLE resource_owner_role
(
    role_id           bigint REFERENCES role (id),
    resource_owner_id varchar(50) REFERENCES resource_owner (id),
    PRIMARY KEY (role_id, resource_owner_id)
);

CREATE TABLE authorization_consent_role
(
    role_id                  bigint REFERENCES role (id),
    authorization_consent_id bigint REFERENCES authorization_consent (id),
    PRIMARY KEY (role_id,authorization_consent_id)
);

CREATE TABLE access_token_scope
(
    scope_id bigint REFERENCES scope (id),
    token_id bigint REFERENCES access_token (token_id),
    PRIMARY KEY (scope_id,token_id)
);

CREATE TABLE client_scope
(
    scope_id  bigint REFERENCES scope (id),
    client_id bigint REFERENCES client (id),
    PRIMARY KEY (scope_id, client_id)
);

CREATE TABLE authorization_consent_scope
(
    scope_id                 bigint REFERENCES scope (id),
    authorization_consent_id bigint REFERENCES authorization_consent (id),
    PRIMARY KEY (scope_id, authorization_consent_id)
);

CREATE TABLE authorization_scope
(
    scope_id         bigint REFERENCES scope (id),
    oauth2_authorization_id bigint REFERENCES oauth2_authorization (id),
    PRIMARY KEY (scope_id, oauth2_authorization_id)
);

CREATE TABLE client_authentication_method
(
    client_id                bigint REFERENCES client (id),
    authentication_method_id bigint REFERENCES authentication_method (id),
    PRIMARY KEY (client_id, authentication_method_id)
);

CREATE TABLE client_authorization_grant_type
(
    client_id                   bigint REFERENCES client (id),
    authorization_grant_type_id bigint REFERENCES authorization_grant_type (id),
    PRIMARY KEY (client_id, authorization_grant_type_id)
);











