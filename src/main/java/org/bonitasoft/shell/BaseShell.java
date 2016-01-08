/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package org.bonitasoft.shell;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jline.console.ConsoleReader;
import org.bonitasoft.shell.color.PrintColor;
import org.bonitasoft.shell.command.HelpCommand;
import org.bonitasoft.shell.command.ShellCommand;
import org.bonitasoft.shell.completer.CommandArgumentsCompleter;
import org.bonitasoft.shell.completer.ReflectCandidateListCompletionHandler;

/**
 * A basic shell
 * Implement abstract methods
 * to run it just execute (e.g. in a main)
 * shell.run();
 * 
 * @author Baptiste Mesta
 */
public abstract class BaseShell<T extends ShellContext> {

    private static final String PROMPT = "bonita> ";

    static final String HELP = "help";

    private HashMap<String, ShellCommand<T>> commands;

    private HelpCommand<T> helpCommand;


    public void init() throws Exception {
        final List<ShellCommand<T>> commandList = initShellCommands();
        commands = new HashMap<String, ShellCommand<T>>();
        for (final ShellCommand<T> shellCommand : commandList) {
            commands.put(shellCommand.getName(), shellCommand);
        }
        helpCommand = getHelpCommand();
        if (helpCommand != null) {
            commands.put(helpCommand.getName(), helpCommand);
        }
        PrintColor.init();

    }

    /**
     * return the help command used
     * Can be overridden
     */
    protected HelpCommand<T> getHelpCommand() {
        return new HelpCommand<T>(commands);
    }

    /**
     * @return list of commands contributed to the shell
     * @throws Exception
     */
    protected abstract List<ShellCommand<T>> initShellCommands() throws Exception;

    /**
     * called by {@link BaseShell} when the shell is exited
     * 
     * @throws Exception
     */
    public void destroy() throws Exception {
        PrintColor.clean();
    }

    public void run(final InputStream in, final OutputStream out) throws Exception {
        init();
        printWelcomeMessage();
        final ConsoleReader reader = new ConsoleReader(in, out);
        reader.setBellEnabled(false);
        final CommandArgumentsCompleter<T> commandArgumentsCompleter = new CommandArgumentsCompleter<T>(commands);

        reader.setCompletionHandler(new ReflectCandidateListCompletionHandler());
        reader.addCompleter(commandArgumentsCompleter);

        String line;
        while ((line = reader.readLine("\n" + getPrompt())) != null) {
            final List<String> args = parse(line);
            final String command = args.remove(0);
            if (commands.containsKey(command)) {
                final ShellCommand<T> clientCommand = commands.get(command);
                if (clientCommand.validate(args)) {
                    try {
                        clientCommand.execute(args, getContext());
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    clientCommand.printHelp();
                }
            } else if ("exit".equals(line)) {
                System.out.println("Exiting application");
                destroy();
                return;
            } else {
                System.out.println("Wrong argument");
                helpCommand.printHelp();
            }
        }
        destroy();
    }

    /**
     * @return
     */
    protected abstract T getContext();

    /**
     * used to parse arguments of the line
     * 
     * @param line
     * @return
     */
    protected List<String> parse(final String line) {
        final List<String> asList = Arrays.asList(line.trim()
                .replaceAll("\\\\ ", "%SPACE%").split("(\\s)+"));
        for (int i = 0; i < asList.size(); i++) {
            final String string = asList.get(i);
            asList.set(i, string.replaceAll("%SPACE%", " "));
        }
        return new ArrayList<String>(asList);
    }


    protected String getPrompt() {
        return PROMPT;
    }

    protected void printWelcomeMessage() {
        System.out.println("Welcome to Bonita Shell.\n For assistance press TAB or type \"help\" then hit ENTER.");
        PrintColor.printRedBold("______             _ _        _____ _          _ _ ");
        PrintColor.printRedBold("| ___ \\           (_) |      /  ___| |        | | |");
        PrintColor.printRedBold("| |_/ / ___  _ __  _| |_ __ _\\ `--.| |__   ___| | |");
        PrintColor.printRedBold("| ___ \\/ _ \\| '_ \\| | __/ _` |`--. \\ '_ \\ / _ \\ | |");
        PrintColor.printRedBold("| |_/ / (_) | | | | | || (_| /\\__/ / | | |  __/ | |");
        PrintColor.printRedBold("\\____/ \\___/|_| |_|_|\\__\\__,_\\____/|_| |_|\\___|_|_|");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
    }
}
