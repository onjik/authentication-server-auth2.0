package click.porito.commons.auth2authserver.util;

import click.porito.commons.auth2authserver.domains.resource_owner.entity.ResourceOwnerEntity;
import jakarta.persistence.AttributeConverter;

import java.util.Arrays;

public class GenderConverter implements AttributeConverter<ResourceOwnerEntity.Gender,Character> {
    @Override
    public Character convertToDatabaseColumn(ResourceOwnerEntity.Gender attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getIdChar();
    }

    @Override
    public ResourceOwnerEntity.Gender convertToEntityAttribute(Character dbData) {
        if (dbData == null) {
            return null;
        }
        return Arrays.stream(ResourceOwnerEntity.Gender.values())
                .filter(gender -> dbData.equals(gender.getIdChar()))
                .findFirst()
                .orElse(null);
    }
}
