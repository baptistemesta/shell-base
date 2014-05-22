/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package org.bonitasoft.shell.completer;

import java.util.List;

import jline.console.completer.Completer;

import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.command.ReflectCommand;

/**
 * @author Baptiste Mesta
 */
public class ReflectMethodHelpCompleter implements Completer {

    private final ReflectCommand<? extends ShellContext> reflectCommand;

    /**
     * @param reflectCommand
     */
    public ReflectMethodHelpCompleter(final ReflectCommand<? extends ShellContext> reflectCommand) {
        this.reflectCommand = reflectCommand;
    }

    @Override
    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        String methodHelp = reflectCommand.getMethodHelp(buffer);
        if (methodHelp != null) {
            candidates.add("**HELP" + methodHelp);
        }
        return cursor;
    }

}
