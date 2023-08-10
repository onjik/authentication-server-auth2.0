package click.porito.commons.auth2authserver.util;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwner;
import jakarta.persistence.AttributeConverter;

import java.util.Arrays;

public class GenderConverter implements AttributeConverter<ResourceOwner.Gender,Character> {
    @Override
    public Character convertToDatabaseColumn(ResourceOwner.Gender attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getIdChar();
    }

    @Override
    public ResourceOwner.Gender convertToEntityAttribute(Character dbData) {
        if (dbData == null) {
            return null;
        }
        return Arrays.stream(ResourceOwner.Gender.values())
                .filter(gender -> dbData.equals(gender.getIdChar()))
                .findFirst()
                .orElse(null);
    }
}
