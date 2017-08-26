package com.cflint;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cflint.config.CFLintConfig;
import com.cflint.exception.CFLintScanException;

public class TestCFBugs_TooManyArguments {

    private CFLint cfBugs;

    @Before
    public void setUp() throws Exception {
        final com.cflint.config.CFLintConfiguration conf = CFLintConfig.createDefaultLimited("TooManyArgumentsChecker");
        cfBugs = new CFLint(conf);
    }

    @Test
    public void testSmallNumberTagBased() throws CFLintScanException {
        final String cfcSrc = "<cfcomponent>\r\n" + "<cffunction name=\"test\">\r\n"
                + "	<cfargument name=\"argumentOne\">\r\n" + "	<cfargument name=\"argumentTwo\">\r\n"
                + "	<cfargument name=\"argumentThree\">\r\n" + "	<cfset var a = 1>\r\n" + "</cffunction>\r\n"
                + "</cfcomponent>";
        cfBugs.process(cfcSrc, "test");
        Collection<List<BugInfo>> result = cfBugs.getBugs().getBugList().values();
        assertEquals(0, result.size());
    }

    @Test
    public void testSmallNumberScriptBased() throws CFLintScanException {
        final String cfcSrc = "component {\r\n"
                + "public void function someFunction(argumentOne, argumentTwo, argumentThree) {\r\n"
                + "	var a = 1;\r\n" + "}\r\n" + "}";
        cfBugs.process(cfcSrc, "test");
        Collection<List<BugInfo>> result = cfBugs.getBugs().getBugList().values();
        assertEquals(0, result.size());
    }

    @Test
    public void testLargeNumberTagBased() throws CFLintScanException {
        final String cfcSrc = "<cfcomponent>\r\n" + "<cffunction name=\"test\">\r\n"
                + "	<cfargument name=\"argumentOne\">\r\n" + "	<cfargument name=\"argumentTwo\">\r\n"
                + "	<cfargument name=\"argumentThree\">\r\n" + "	<cfargument name=\"argumentFour\">\r\n"
                + "	<cfargument name=\"argumentFive\">\r\n" + "	<cfargument name=\"argumentSiX\">\r\n"
                + "	<cfargument name=\"argumentSeven\">\r\n" + "	<cfargument name=\"argumentEight\">\r\n"
                + "	<cfargument name=\"argumentNone\">\r\n" + "	<cfargument name=\"argumentTen\">\r\n"
                + "	<cfargument name=\"argumentEleven\">\r\n" + "	<cfargument name=\"argumentTwelve\">\r\n"
                + "	<cfset var a = 1>\r\n" + "</cffunction>\r\n" + "</cfcomponent>";
        cfBugs.process(cfcSrc, "test");
        final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
        assertEquals(1, result.size());
        assertEquals("EXCESSIVE_ARGUMENTS", result.get(0).getMessageCode());
        assertEquals(2, result.get(0).getLine());
    }

    @Test
    public void testLargeNumberScriptBased() throws CFLintScanException {
        final String cfcSrc = "component {\r\n"
                + "public void function someFunction(argumentOne, argumentTwo, argumentThree,"
                + "argumentFour, argumentFive, argumentSix, argumentSeven,"
                + "argumentEight, argumentNine, intargumentTen, argumentEleven) {\r\n" + "	var a = 1;\r\n"
                + "}\r\n" + "}";
        cfBugs.process(cfcSrc, "test");
        final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
        assertEquals(1, result.size());
        assertEquals("EXCESSIVE_ARGUMENTS", result.get(0).getMessageCode());
        assertEquals(2, result.get(0).getLine());
    }

}
