/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package org.bonitasoft.shell.completer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An utility class to parse arguments for the {@link CommandArgumentsCompleter}
 * 
 * @author Baptiste Mesta
 */
public class ArgumentParser {

    private final String original;

    private List<String> arguments;

    private String command;

    private String lastArgument;

    private int offset;

    private String previousArgument;

    /**
     * 
     */
    public ArgumentParser(final String string) {
        original = string;
        final List<String> list = new ArrayList<String>(Arrays.asList(string.split("(\\s)+")));
        if (" ".equals(original.substring(original.length() - 1, original.length()))) {
            list.add("");
        }
        if (list.size() > 0) {
            command = list.get(0);
            if (list.size() > 1) {
                arguments = list.subList(1, list.size());
            } else {
                arguments = new ArrayList<String>();
            }
        } else {
            arguments = new ArrayList<String>();
        }
        if (arguments.size() > 0) {
            lastArgument = arguments.get(arguments.size() - 1);
            if (arguments.size() > 1) {
                previousArgument = arguments.get(arguments.size() - 2);
            }
            offset = original.lastIndexOf(lastArgument);
        } else {
            offset = original.length();
        }
    }

    public String getCommand() {
        return command;
    }

    public String getLastArgument() {
        return lastArgument;
    }

    public int getLastArgumentIndex() {
        return arguments.size() - 1;
    }

    public int getOffset() {
        return offset;
    }

    public String getPreviousArgument() {
        return previousArgument;
    }
}
