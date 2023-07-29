package click.porito.commons.auth2authserver.auth2.model.constant;

import click.porito.commons.auth2authserver.auth2.model.entity.User;

/**
 * Used in {@link User} for mapping gender column
 * must be single character (data type : char(1))
 */
public enum Gender {
    M("male"),
    F("female");

    private final String description;

    Gender(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
