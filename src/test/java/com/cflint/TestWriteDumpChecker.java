package com.cflint;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cflint.config.CFLintConfig;
import com.cflint.config.CFLintPluginInfo.PluginInfoRule;
import com.cflint.config.CFLintPluginInfo.PluginInfoRule.PluginMessage;
import com.cflint.exception.CFLintScanException;
import com.cflint.plugins.core.FunctionXChecker;

public class TestWriteDumpChecker {

    private CFLint cfBugs;

    @Before
    public void setUp() throws Exception {
        final CFLintConfig conf = new CFLintConfig();
        final PluginInfoRule pluginRule = new PluginInfoRule();
        pluginRule.setName("WriteDumpChecker");
        pluginRule.setClassName("FunctionXChecker");
        pluginRule.addParameter("functionName", "writedump");
        conf.getRules().add(pluginRule);
        final PluginMessage pluginMessage = new PluginMessage("AVOID_USING_WRITEDUMP");
        pluginMessage.setSeverity(Levels.INFO);
        pluginMessage.setMessageText("Avoid using the ${functionName} function in production code.");
        pluginRule.getMessages().add(pluginMessage);

        FunctionXChecker checker = new FunctionXChecker();
        checker.setParameter("functionName", "writedump");
        cfBugs = new CFLint(conf, checker);

    }

    @Test
    public void testWriteDumpInScript() throws CFLintScanException {
        final String scriptSrc = "<cfscript>\r\n" + "var a = 23;\r\n" + "writeDump(a);\r\n" + "</cfscript>";

        cfBugs.process(scriptSrc, "test");
        final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
        assertEquals(1, result.size());
        assertEquals("AVOID_USING_WRITEDUMP", result.get(0).getMessageCode());
        assertEquals(3, result.get(0).getLine());
    }

    @Test
    public void testWriteDumpMixedCaseScript() throws CFLintScanException {
        final String scriptSrc = "<cfscript>\r\n" + "var a = 23;\r\n" + "WriteDUMP(a);\r\n" + "</cfscript>";

        cfBugs.process(scriptSrc, "test");
        final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
        assertEquals(1, result.size());
        assertEquals("AVOID_USING_WRITEDUMP", result.get(0).getMessageCode());
        assertEquals(3, result.get(0).getLine());
    }

    @Test
    public void testWriteDumpInTag() throws CFLintScanException {
        final String tagSrc = "<cfset a = 23>\r\n" + "<cfset writeDump(a)>";

        cfBugs.process(tagSrc, "test");
        final List<BugInfo> result = cfBugs.getBugs().getBugList().values().iterator().next();
        assertEquals(1, result.size());
        assertEquals("AVOID_USING_WRITEDUMP", result.get(0).getMessageCode());
        assertEquals(2, result.get(0).getLine());
    }

}
