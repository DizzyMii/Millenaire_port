/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.annotedparameters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.millenaire.common.annotedparameters.AnnotedParameter;

public class ConfigAnnotations {

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.FIELD})
    public static @interface FieldDocumentation {
        public String explanation();

        public String explanationCategory() default "";
    }

    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.FIELD})
    public static @interface ConfigField {
        public String defaultValue() default "";

        public String fieldCategory() default "";

        public String paramName() default "";

        public AnnotedParameter.ParameterType type();
    }
}

