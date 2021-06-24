package com.ibm.ws.wsfvt.test.framework;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface FvtTest {
	
	public enum Releases {WAS60, WAS602, WAS61, WSFP, WAS70, WAS7003, SAML10}
	
	String description();
	String expectedResult();
	
	Releases since();
}
