/**
 * Copyright (C) 2012 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.shell.command;

import java.util.List;

import org.bonitasoft.shell.ShellContext;

/**
 * @author Baptiste Mesta
 */
public class LogoutCommand<T extends ShellContext> extends ShellCommand<T> {

    @Override
    public boolean execute(final List<String> args, final T context) throws Exception {
        if (context.isLogged()) {
            context.logout();
            System.out.println("Logged out");
            return true;
        } else {
            System.out.println("Not logged in");
            return false;
        }
    }

    @Override
    public void printHelp() {
        System.out.println("Usage: logout");
    }

    @Override
    public boolean validate(final List<String> args) {
        return args.isEmpty();
    }

    @Override
    public String getName() {
        return "logout";
    }

}
