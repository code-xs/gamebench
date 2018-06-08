package mirror;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
//@Retention(RetentionPolicy.RUNTIME)
//@Target({java.lang.annotation.ElementType.FIELD})
public @interface MethodParams {
    Class<?>[] value();
}
