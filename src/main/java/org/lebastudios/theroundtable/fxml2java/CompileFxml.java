package org.lebastudios.theroundtable.fxml2java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface CompileFxml
{
    String[] fxmls() default {};
    String[] directories() default {};
}
