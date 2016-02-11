package com.skeds.android.phone.business.Aspects;

/**
 * Created by Mikhail on 31.07.2014.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //can use in method only.
public @interface Cacheable {
//
//    //should ignore this test?
//    public boolean enabled() default true;

}
