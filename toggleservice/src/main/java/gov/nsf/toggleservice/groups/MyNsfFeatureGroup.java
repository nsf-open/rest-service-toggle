package gov.nsf.toggleservice.groups;

import org.togglz.core.annotation.FeatureGroup;
import org.togglz.core.annotation.Label;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Feature group for MyNSF.
 */
@FeatureGroup
@Label("MyNSF")
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyNsfFeatureGroup {
}
