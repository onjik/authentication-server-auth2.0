package click.porito.commons.auth2authserver.global.util;

import java.lang.annotation.*;

/**
 * 상수 Entity 임을 나타내는 어노테이션 입니다.
 * 여기서 말하는 상수 Entity란 런타임 중에 매우 드물게 바뀌며 상수와 같은 역할을 하는 엔티티를 말을 합니다.
 * 다음과 같은 작업이 권장됩니다.
 * <ul>데이터베이스를 생성시, 미리 데이터를 넣어놓는다.</ul>
 * <ul>런타임 중 적극적인 수정 작업을 지양한다.</ul>
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Inherited
@Target(ElementType.TYPE_USE)
public @interface ConstantEntity {
}
