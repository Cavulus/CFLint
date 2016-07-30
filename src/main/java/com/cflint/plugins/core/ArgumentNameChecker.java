package com.cflint.plugins.core;

import com.cflint.BugInfo;
import com.cflint.BugList;
import com.cflint.plugins.CFLintScannerAdapter;
import com.cflint.plugins.Context;

import cfml.parsing.cfscript.script.CFFuncDeclStatement;
import cfml.parsing.cfscript.script.CFFunctionParameter;
import cfml.parsing.cfscript.script.CFScriptStatement;
import net.htmlparser.jericho.Element;
import ro.fortsoft.pf4j.Extension;

@Extension
public class ArgumentNameChecker extends CFLintScannerAdapter {
	public static final String ARGUMENT = "Argument ";
	final String severity = "INFO";

	@Override
	public void expression(final CFScriptStatement expression, final Context context, final BugList bugs) {
		if (expression instanceof CFFuncDeclStatement) {
			final CFFuncDeclStatement function = (CFFuncDeclStatement) expression;
			final int lineNo = function.getLine() + context.startLine() - 1;

			for (final CFFunctionParameter argument : function.getFormals()) {
				checkNameForBugs(argument.getName(), context.getFilename(), context.getFunctionName(), lineNo, bugs);
			}
		}
	}

	@Override
	public void element(final Element element, final Context context, final BugList bugs) {
		if (element.getName().equals("cfargument")) {
			final int lineNo = context.startLine();
			final String name = element.getAttributeValue("name");
			if (name != null && name.length() > 0) {
				checkNameForBugs(name, context.getFilename(), context.getFunctionName(), lineNo, bugs);
			} else {
				bugs.add(new BugInfo.BugInfoBuilder().setLine(lineNo).setMessageCode("ARGUMENT_INVALID_NAME")
						.setSeverity("ERROR").setFilename(context.getFilename()).setFunction(context.getFunctionName())
						.setMessage(ARGUMENT + " is missing a name.").setVariable("").build());
			}
		}
	}

	public void checkNameForBugs(final String argument, final String filename, final String functionName,
			final int line, final BugList bugs) {
		int minArgLength = ValidName.MIN_ARGUMENT_LENGTH;
		int maxArgLength = ValidName.MAX_ARGUMENT_LENGTH;
		int maxArgWords = ValidName.MAX_ARGUMENT_WORDS;

		if (getParameter("MinLength") != null) {
			try {
				minArgLength = Integer.parseInt(getParameter("MinLength"));
			} catch (final Exception e) {
			}
		}

		if (getParameter("MaxLength") != null) {
			try {
				maxArgLength = Integer.parseInt(getParameter("MaxLength"));
			} catch (final Exception e) {
			}
		}

		if (getParameter("MaxWords") != null) {
			try {
				maxArgWords = Integer.parseInt(getParameter("MaxWords"));
			} catch (final Exception e) {
			}
		}

		final ValidName name = new ValidName(minArgLength, maxArgLength, maxArgWords);

		if (name.isInvalid(argument)) {
			bugs.add(
					new BugInfo.BugInfoBuilder().setLine(line).setMessageCode("ARGUMENT_INVALID_NAME")
							.setSeverity(severity).setFilename(filename).setFunction(functionName)
							.setMessage(
									ARGUMENT + argument + " is not a valid name. Please use CamelCase or underscores.")
							.setVariable(argument).build());
		}
		if (name.isUpperCase(argument)) {
			bugs.add(new BugInfo.BugInfoBuilder().setLine(line).setMessageCode("ARGUMENT_ALLCAPS_NAME")
					.setSeverity(severity).setFilename(filename).setFunction(functionName)
					.setMessage(ARGUMENT + argument + " should not be upper case.").setVariable(argument).build());
		}
		if (name.tooShort(argument)) {
			bugs.add(new BugInfo.BugInfoBuilder().setLine(line).setMessageCode("ARGUMENT_TOO_SHORT")
					.setSeverity(severity).setFilename(filename).setFunction(functionName)
					.setMessage(ARGUMENT + argument + " should be longer than " + minArgLength + " characters.")
					.setVariable(argument).build());
		}
		if (name.tooLong(argument)) {
			bugs.add(
					new BugInfo.BugInfoBuilder().setLine(line).setMessageCode("ARGUMENT_TOO_LONG").setSeverity(severity)
							.setFilename(filename).setFunction(functionName)
							.setMessage(
									ARGUMENT + argument + " should be shorter than " + maxArgLength + " characters.")
							.setVariable(argument).build());
		}
		if (!name.isUpperCase(argument) && name.tooWordy(argument)) {
			bugs.add(new BugInfo.BugInfoBuilder().setLine(line).setMessageCode("ARGUMENT_TOO_WORDY")
					.setSeverity(severity).setFilename(filename).setFunction(functionName)
					.setMessage(ARGUMENT + argument + " is too wordy, can you think of a more concise name?")
					.setVariable(argument).build());
		}
		if (name.isTemporary(argument)) {
			bugs.add(new BugInfo.BugInfoBuilder().setLine(line).setMessageCode("ARGUMENT_IS_TEMPORARY")
					.setSeverity(severity).setFilename(filename).setFunction(functionName)
					.setMessage("Temporary argument " + argument + " could be named better.").setVariable(argument)
					.build());
		}
		if (name.hasPrefixOrPostfix(argument)) {
			bugs.add(new BugInfo.BugInfoBuilder().setLine(line).setMessageCode("ARGUMENT_HAS_PREFIX_OR_POSTFIX")
					.setSeverity(severity).setFilename(filename).setFunction(functionName)
					.setMessage("Argument has prefix or postfix " + argument + " and could be named better.")
					.setVariable(argument).build());
		}
	}
}