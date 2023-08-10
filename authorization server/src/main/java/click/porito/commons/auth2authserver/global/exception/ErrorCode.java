package click.porito.commons.auth2authserver.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    UNEXPECTED_SERVER_ERROR(
            Category.INTERNAL_SERVER_ERROR,
            HttpStatus.INTERNAL_SERVER_ERROR);


    private final Category category;
    private final HttpStatus expectedHttpStatus;

    ErrorCode(Category category, HttpStatus expectedHttpStatus) {
        this.category = category;
        this.expectedHttpStatus = expectedHttpStatus;
    }

    public static enum Category {
        INTERNAL_SERVER_ERROR;
    }

    public String getCompactCode() {
        return this.category.name() + "_" + this.name();
    }

    public Category getCategory() {
        return category;
    }

    public HttpStatus getExpectedHttpStatus() {
        return expectedHttpStatus;
    }

    public static void main(String[] args) {
        System.out.println(UNEXPECTED_SERVER_ERROR.getCompactCode());
    }




}
