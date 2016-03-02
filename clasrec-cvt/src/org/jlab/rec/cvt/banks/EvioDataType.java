package org.jlab.rec.cvt.banks;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Data Types descriptions for EVIO Objects.
 * @author gavalian
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EvioDataType {

	int tag();

	String type();

	int num();

	int parent();

}
