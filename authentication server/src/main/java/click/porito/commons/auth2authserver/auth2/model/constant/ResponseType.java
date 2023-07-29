package click.porito.commons.auth2authserver.auth2.model.constant;

public enum ResponseType {
    CODE, TOKEN; //support code and token only

    public static ResponseType fromStringIgnoreCase(String value) {
        for (ResponseType responseType : ResponseType.values()) {
            if (responseType.name().equalsIgnoreCase(value)) {
                return responseType;
            }
        }
        return null;
    }
}
