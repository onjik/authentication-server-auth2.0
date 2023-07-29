package click.porito.commons.auth2authserver.learning_test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class JacksonTest {


    @Test
    @DisplayName("JsonIgnore 을 붙이면 해당 필드는 json으로 변환되지 않는다.")
    void jsonIgnoreTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Dto dto = new Dto();
        String string = objectMapper.writeValueAsString(dto);
        assertTrue(string.contains("publicInfo"));
        assertFalse(string.contains("privateInfo"));
        System.out.println(string);
    }

    private class Dto {

        @JsonIgnore
        private String privateInfo;
        private String publicInfo;

        public Dto() {
            this.privateInfo = "private";
            this.publicInfo = "public";
        }

        public String getPrivateInfo() {
            return privateInfo;
        }

        public String getPublicInfo() {
            return publicInfo;
        }
    }
}
