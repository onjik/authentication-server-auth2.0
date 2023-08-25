/*
 static table
 초기에 값을 박아놓고 시작 하는 테이블
 주로 M:N 관계에서 상수 역할로 많이쓰임
 */

CREATE TABLE authorization_grant_type
(
    id   bigserial PRIMARY KEY,
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
    CONSTRAINT unique_role_name UNIQUE (name)
);

CREATE TABLE scope
(
    id           bigserial PRIMARY KEY,
    uri_endpoint text NOT NULL ,
    name         varchar(255) NOT NULL ,
    CONSTRAINT unique_scope_name UNIQUE (name)
);

/*
 main table
 주체가 되는 주요 테이블
 */

CREATE TABLE client
(
    id                       varchar(255) PRIMARY KEY,
    client_id                varchar(255) NOT NULL,
    client_id_issued_at      timestamp DEFAULT NOW(),
    client_name              varchar(100) NOT NULL,
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
    client_id varchar(255) NOT NULL REFERENCES client (id),
    CONSTRAINT unique_client_uri UNIQUE (uri, client_id)
);


CREATE TABLE resource_owner
(
    id          varchar(50) PRIMARY KEY,
    name        varchar(255) NOT NULL,
    gender      char(1)      NOT NULL,
    created_at  timestamp DEFAULT NOW()
);

CREATE TABLE account
(
    id                bigserial PRIMARY KEY,
    email             varchar(255) NOT NULL,
    is_email_verified bool      DEFAULT FALSE,
    is_locked         bool      DEFAULT FALSE,
    is_disabled       bool      DEFAULT FALSE,
    expires_at        timestamp DEFAULT NULL,
    resource_owner_id varchar(50)  NOT NULL REFERENCES resource_owner (id),
    CONSTRAINT email_unique UNIQUE (email) ,
    CONSTRAINT one_account_per_resource_owner UNIQUE (resource_owner_id)
);

CREATE TABLE authentication
(
    id                  bigserial PRIMARY KEY,
    authentication_type varchar(255) NOT NULL,
    account_id          bigint REFERENCES account (id),
    UNIQUE (authentication_type, account_id)
);

CREATE TABLE password
(
    authentication_id  bigint PRIMARY KEY REFERENCES authentication (id),
    password_value varchar(255) NOT NULL,
    issued_at      timestamp DEFAULT NOW(),
    expires_at     timestamp DEFAULT NULL
);



CREATE TABLE authorization_consent
(
    id                bigserial PRIMARY KEY,
    client_id         varchar(255) NOT NULL REFERENCES client (id),
    resource_owner_id varchar(50)  NOT NULL REFERENCES resource_owner (id),
    UNIQUE (client_id, resource_owner_id)
);

CREATE TABLE oauth2_authorization
(
    id                          varchar(255) PRIMARY KEY,
    client_id                   varchar(255) NOT NULL REFERENCES client (id),
    resource_owner_id           varchar(50)  NOT NULL REFERENCES resource_owner (id),
    authorization_grant_type_id bigint       NOT NULL REFERENCES authorization_grant_type (id),
    attribute                   json         DEFAULT NULL,
    state                       varchar(500) DEFAULT NULL
);

CREATE TABLE token
(
    token_id                bigserial PRIMARY KEY,
    dtype                   varchar(100) NOT NULL,
    issued_at               timestamp DEFAULT NOW(),
    expires_at              timestamp    NOT NULL,
    oauth2_authorization_id varchar(255) REFERENCES oauth2_authorization (id)
);


CREATE TABLE access_token
(
    token_id   bigserial PRIMARY KEY REFERENCES token (token_id),
    token_type varchar(255) NOT NULL
);



CREATE TABLE refresh_token
(
    token_id bigserial PRIMARY KEY REFERENCES token (token_id)
);



CREATE TABLE authorization_code
(
    token_id bigserial PRIMARY KEY REFERENCES token (token_id)
);




CREATE TABLE oidc_id_token
(
    token_id bigserial PRIMARY KEY REFERENCES token (token_id),
    claims   json NOT NULL
);


/*
 mapping tables
 M:N 관계를 표현하기 위한 테이블들
 */

CREATE TABLE account_role
(
    role_id           bigint REFERENCES role (id),
    account_id bigint REFERENCES account (id),
    PRIMARY KEY (role_id, account_id)
);

CREATE TABLE authorization_consent_role
(
    role_id           bigint REFERENCES role (id),
    authorization_consent_id bigint REFERENCES authorization_consent(id),
    PRIMARY KEY (role_id, authorization_consent_id)
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
    client_id varchar(255) REFERENCES client (id),
    PRIMARY KEY (scope_id, client_id)
);

CREATE TABLE authorization_consent_scope
(
    scope_id                 bigint REFERENCES scope (id),
    authorization_consent_id bigint REFERENCES authorization_consent(id),
    PRIMARY KEY (scope_id, authorization_consent_id)
);

CREATE TABLE authorization_scope
(
    scope_id                bigint REFERENCES scope (id),
    oauth2_authorization_id varchar(255) REFERENCES oauth2_authorization (id),
    PRIMARY KEY (scope_id, oauth2_authorization_id)
);


CREATE TABLE client_authentication_method
(
    client_id                varchar(255) REFERENCES client (id),
    authentication_method_id bigint REFERENCES authentication_method (id),
    PRIMARY KEY (client_id, authentication_method_id)
);

CREATE TABLE client_authorization_grant_type
(
    client_id                   varchar(255) REFERENCES client (id),
    authorization_grant_type_id bigint REFERENCES authorization_grant_type (id),
    PRIMARY KEY (client_id, authorization_grant_type_id)
);
