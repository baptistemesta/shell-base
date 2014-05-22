/*
 * Copyright (c) 2002-2012, the original author or authors.
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 * http://www.opensource.org/licenses/bsd-license.php
 */
package org.bonitasoft.shell;

/**
 * @author Baptiste Mesta
 */
public interface ShellContext {

    boolean isLogged();

    void logout() throws Exception;

    void login(String username, String password) throws Exception;

    Object getApi(String apiName) throws Exception;

}
