package com.cflint.config;

import java.util.ArrayList;
import java.util.List;

import com.cflint.config.CFLintPluginInfo.PluginInfoRule;
import com.cflint.config.CFLintPluginInfo.PluginInfoRule.PluginMessage;
import com.cflint.plugins.CFLintScanner;

/**
 * Combines information from the config.xml and the core CFLint information and
 * provides utility functions
 *
 * It only includes the Rules that are matched by the include/exclude section if
 * one of them is present.
 */
public class ConfigRuntime extends CFLintConfig {

	public ConfigRuntime() {

	}

	public ConfigRuntime(final CFLintConfig config, final CFLintPluginInfo pluginInfo) {
		final List<PluginInfoRule> rules = new ArrayList<PluginInfoRule>();

		if (config != null) {
			includes.addAll(config.getIncludes());
			excludes.addAll(config.getExcludes());
			rules.addAll(config.getRules());
		}
		for (final PluginInfoRule rule : pluginInfo.getRules()) {
			if (!rules.contains(rule)) {
				rules.add(rule);
			}
		}

		if (config != null) {
			// If includes is specified, load *only* those messages
			if (!config.getIncludes().isEmpty()) {
				for (final PluginInfoRule rule : rules) {
					// Include the rule if at least one of the messages is
					// included.
					for (final PluginMessage msg : rule.getMessages()) {
						if (config.getIncludes().contains(msg)) {
							for (final PluginMessage cfgMsg : config.getIncludes()) {
								if (cfgMsg.equals(msg)) {
									merge(cfgMsg, msg);
								}
							}

							getRules().add(rule);
							break;
						}
					}
				}
			} else {// Otherwise load all considering the excludes.
				for (final PluginInfoRule rule : rules) {
					// Exclude the rule if ALL of the messages are excluded.
					boolean excluded = true;
					for (final PluginMessage msg : rule.getMessages()) {
						excluded = excluded && config.getExcludes().contains(msg);
					}
					if (!excluded) {
						getRules().add(rule);
					}
				}
			}
		} else {
			getRules().addAll(rules);
		}
	}

	/*
	 * Apply the configuration to the existing rule. Overlay it.
	 */
	private void merge(final PluginMessage cfgMsg, final PluginMessage msg) {
		if (!isEmpty(cfgMsg.getMessageText())) {
			msg.setMessageText(cfgMsg.getMessageText());
		}
		if (!isEmpty(cfgMsg.getSeverity())) {
			msg.setSeverity(cfgMsg.getSeverity());
		}
	}

	private boolean isEmpty(final String messageText) {
		return messageText == null || messageText.trim().length() == 0;
	}

	public boolean isIncludeMessage(final String messageCode) {
		return isIncludeMessage(new PluginMessage(messageCode));
	}

	public boolean isIncludeMessage(final PluginMessage message) {
		if (!includes.isEmpty()) {
			return includes.contains(message);
		} else {
			return !excludes.contains(message);
		}
	}

	public PluginInfoRule getRuleByClass(final Class<?> clazz) {
		final String className = clazz.getSimpleName();
		for (final PluginInfoRule rule : getRules()) {
			if (rule.getName().equals(className) || className.equals(rule.getClassName())) {
				return rule;
			}
		}
		return null;
	}

	public PluginInfoRule getRuleForPlugin(final CFLintScanner plugin) {
		for (final PluginInfoRule rule : getRules()) {
			if (rule.getPluginInstance() == plugin) {
				return rule;
			}
		}
		return getRuleByClass(plugin.getClass());
	}
}
