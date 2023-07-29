
-- Postgresql query

-- user table
CREATE TABLE users
(
    user_id         bigint GENERATED ALWAYS AS IDENTITY,
    email           varchar(254) NOT NULL,
    name            varchar(100) NOT NULL,
    gender          char(1)      NOT NULL,
    birth_date      date         NOT NULL,
    registered_date timestamp    NOT NULL,
    CONSTRAINT users_pk PRIMARY KEY (user_id),
    CONSTRAINT users_email_unique UNIQUE (email)
);


-- credential table
CREATE TABLE credentials
(
    credential_id   bigint GENERATED ALWAYS AS IDENTITY,
    credential_type varchar(10) NOT NULL,
    user_id         bigint      NOT NULL,
    CONSTRAINT credentials_pk PRIMARY KEY (credential_id),
    CONSTRAINT credentials_user_id_fk FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT user_credential_type_unique UNIQUE (credential_type, user_id)
);


-- password table
CREATE TABLE passwords
(
    credential_id bigint,
    password      text NOT NULL,
    CONSTRAINT password_pk PRIMARY KEY (credential_id),
    CONSTRAINT password_credential_id_fk FOREIGN KEY (credential_id) REFERENCES credentials (credential_id)
);




-- remember me token table
CREATE TABLE remember_me_tokens
(
    remember_me_token_id bigint GENERATED ALWAYS AS IDENTITY,
    user_id              bigint    NOT NULL,
    token_value          text      NOT NULL,
    last_used_date       timestamp NOT NULL,
    created_date         timestamp NOT NULL,
    CONSTRAINT remember_me_token_pk PRIMARY KEY (remember_me_token_id),
    CONSTRAINT remember_me_tokens_user_id_fk FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT remember_me_tokens_token_value_unique UNIQUE (token_value)
);


-- role table
CREATE TABLE roles
(
    role_id integer GENERATED ALWAYS AS IDENTITY,
    name    varchar(50) NOT NULL,
    CONSTRAINT roles_pk PRIMARY KEY (role_id),
    CONSTRAINT roles_name_unique UNIQUE (name)
);


-- user - role mapping table
CREATE TABLE user_role
(
    user_id bigint,
    role_id integer,
    CONSTRAINT user_role_pk PRIMARY KEY (user_id, role_id),
    CONSTRAINT user_role_user_id_fk FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT user_role_role_id_fk FOREIGN KEY (role_id) REFERENCES roles (role_id)
);

-- service table
CREATE TABLE services
(
    service_id          bigint GENERATED ALWAYS AS IDENTITY,
    service_name        varchar(50) NOT NULL,
    user_id             bigint      NOT NULL,
    service_key         uuid        NOT NULL,
    secret_key          text        NOT NULL,
    CONSTRAINT services_pk PRIMARY KEY (service_id),
    CONSTRAINT services_user_id_fk FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT service_key_unique UNIQUE (service_key)
);

-- registered redirections table

CREATE TABLE registered_redirections(
    redirect_id bigint GENERATED ALWAYS AS IDENTITY ,
    url_pattern text NOT NULL ,
    service_id bigint NOT NULL ,
    CONSTRAINT registered_redirections_pk PRIMARY KEY (redirect_id),
    CONSTRAINT registered_redirections_url_pattern_fk FOREIGN KEY (service_id) REFERENCES services (service_id),
    CONSTRAINT url_unique UNIQUE (url_pattern,service_id)
);



-- scope table
CREATE TABLE scopes
(
    scope_id bigint GENERATED ALWAYS AS IDENTITY,
    name     varchar(50) NOT NULL,
    CONSTRAINT scopes_pk PRIMARY KEY (scope_id),
    CONSTRAINT scopes_name_unique UNIQUE (name)
);

CREATE TABLE service_scope
(
    service_id bigint,
    scope_id   bigint,
    CONSTRAINT service_scope_pk PRIMARY KEY (service_id, scope_id),
    CONSTRAINT service_scope_service_id_fk FOREIGN KEY (service_id) REFERENCES services (service_id),
    CONSTRAINT service_scope_scope_id_fk FOREIGN KEY (scope_id) REFERENCES scopes (scope_id)
);




