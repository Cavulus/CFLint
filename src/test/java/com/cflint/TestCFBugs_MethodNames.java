package com.cflint;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cflint.config.CFLintConfig;

import cfml.parsing.reporting.ParseException;

public class TestCFBugs_MethodNames {

	private CFLint cfBugs;

	@Before
	public void setUp() throws Exception{
		final com.cflint.config.CFLintConfiguration conf = CFLintConfig.createDefaultLimited("MethodNameChecker");
		cfBugs = new CFLint(conf);
	}

	@Test
	public void testValidNamesTag() throws ParseException, IOException {
		final String tagSrc = "<cfcomponent>\r\n"
		 + "<cffunction name=\"niceMethodName\">\r\n"
		 + "</cffunction>\r\n"
		 + "</cfcomponent>";
		cfBugs.process(tagSrc, "test");
		Collection<List<BugInfo>> result = cfBugs.getBugs().getBugList().values();
		
		assertEquals(0, result.size());
	}

	@Test
	public void testUpercaseNameTag() throws ParseException, IOException {
		final String tagSrc = "<cfcomponent>\r\n"
		 + "<cffunction name=\"UGLYNAME\">\r\n"
		 + "</cffunction>\r\n"
		 + "</cfcomponent>";
		cfBugs.process(tagSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("METHOD_ALLCAPS_NAME", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

	@Test
	public void invalidCharsInNameTag() throws ParseException, IOException {
		final String tagSrc = "<cfcomponent>\r\n"
		 + "<cffunction name=\"last$name\">\r\n"
		 + "</cffunction>\r\n"
		 + "</cfcomponent>";
		cfBugs.process(tagSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("METHOD_INVALID_NAME", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

	@Test
	public void nameEndsInNumberTag() throws ParseException, IOException {
		final String tagSrc = "<cfcomponent>\r\n"
		 + "<cffunction name=\"endInNumber23\">\r\n"
		 + "</cffunction>\r\n"
		 + "</cfcomponent>";
		cfBugs.process(tagSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("METHOD_INVALID_NAME", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

	@Test
	public void nameTooShortTag() throws ParseException, IOException {
		final String tagSrc = "<cfcomponent>\r\n"
		 + "<cffunction name=\"a\">\r\n"
		 + "</cffunction>\r\n"
		 + "</cfcomponent>";
		cfBugs.process(tagSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("METHOD_TOO_SHORT", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

	@Test
	public void nameTooLongTag() throws ParseException, IOException {
		final String tagSrc = "<cfcomponent>\r\n"
		 + "<cffunction name=\"isaveryveryverylongmethodname\">\r\n"
		 + "</cffunction>\r\n"
		 + "</cfcomponent>";
		cfBugs.process(tagSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("METHOD_TOO_LONG", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

	@Test
	public void nameTooWordyTag() throws ParseException, IOException {
		final String tagSrc = "<cfcomponent>\r\n"
		 + "<cffunction name=\"methodNameIsFarTooWordy\">\r\n"
		 + "</cffunction>\r\n"
		 + "</cfcomponent>";
		cfBugs.process(tagSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("METHOD_TOO_WORDY", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

	@Test
	public void nameIsTemporyTag() throws ParseException, IOException {
		final String tagSrc = "<cfcomponent>\r\n"
		 + "<cffunction name=\"temp\">\r\n"
		 + "</cffunction>\r\n"
		 + "</cfcomponent>";
		cfBugs.process(tagSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().get("METHOD_IS_TEMPORARY");
		assertEquals(1, result.size());
		assertEquals("METHOD_IS_TEMPORARY", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

	@Test
	public void nameHasPrefixOrPostfixTag() throws ParseException, IOException {
		final String tagSrc = "<cfcomponent>\r\n"
		 + "<cffunction name=\"methodNameArr\">\r\n"
		 + "</cffunction>\r\n"
		 + "</cfcomponent>";
		cfBugs.process(tagSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("METHOD_HAS_PREFIX_OR_POSTFIX", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}


	@Test
	public void testValidNamesScript() throws ParseException, IOException {
		final String scriptSrc = "component {\r\n"
		 + "function test() {\r\n"
		 + "}\r\n"
		 + "}";
		cfBugs.process(scriptSrc, "test");
		Collection<List<BugInfo>> result = cfBugs.getBugs().getBugList().values();
		assertEquals(0, result.size());
	}

	@Test
	public void testUpercaseNameScript() throws ParseException, IOException {
		final String scriptSrc = "component {\r\n"
		 + "function UPPERCASE() {\r\n"
		 + "}\r\n"
		 + "}";
		cfBugs.process(scriptSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("METHOD_ALLCAPS_NAME", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

	@Test
	public void invalidCharsInNameScript() throws ParseException, IOException {
		final String scriptSrc = "component {\r\n"
		 + "function method$name() {\r\n"
		 + "}\r\n"
		 + "}";
		cfBugs.process(scriptSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("METHOD_INVALID_NAME", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

	@Test
	public void nameEndsInNumberScript() throws ParseException, IOException {
		final String scriptSrc = "component {\r\n"
		 + "function method23() {\r\n"
		 + "}\r\n"
		 + "}";
		cfBugs.process(scriptSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("METHOD_INVALID_NAME", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

	@Test
	public void nameTooShortScript() throws ParseException, IOException {
		final String scriptSrc = "component {\r\n"
		 + "function a() {\r\n"
		 + "}\r\n"
		 + "}";
		cfBugs.process(scriptSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("METHOD_TOO_SHORT", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

	@Test
	public void nameTooLongScript() throws ParseException, IOException {
		final String scriptSrc = "component {\r\n"
		 + "function isaveryveryverylongmethodname() {\r\n"
		 + "}\r\n"
		 + "}";
		cfBugs.process(scriptSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("METHOD_TOO_LONG", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

	@Test
	public void nameTooWordyScript() throws ParseException, IOException {
		final String scriptSrc = "component {\r\n"
		 + "function isFarTooWordyMethodName() {\r\n"
		 + "}\r\n"
		 + "}";
		cfBugs.process(scriptSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("METHOD_TOO_WORDY", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

	@Test
	public void nameIsTemporyScript() throws ParseException, IOException {
		final String scriptSrc = "component {\r\n"
		 + "function temp() {\r\n"
		 + "}\r\n"
		 + "}";
		cfBugs.process(scriptSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().get("METHOD_IS_TEMPORARY");
		assertEquals(1, result.size());
		assertEquals("METHOD_IS_TEMPORARY", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

	@Test
	public void nameHasPrefixOrPostfixScript() throws ParseException, IOException {
		final String scriptSrc = "component {\r\n"
		 + "function thisMethod() {\r\n"
		 + "}\r\n"
		 + "}";
		cfBugs.process(scriptSrc, "test");
		final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
		assertEquals(1, result.size());
		assertEquals("METHOD_HAS_PREFIX_OR_POSTFIX", result.get(0).getMessageCode());
		assertEquals(2, result.get(0).getLine());
	}

}
