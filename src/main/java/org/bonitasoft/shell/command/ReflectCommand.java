/**
 * Copyright (C) 2014 BonitaSoft S.A.
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
 ** 
 * @since 6.2
 */
package org.bonitasoft.shell.command;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jline.console.completer.Completer;

import org.bonitasoft.shell.ShellContext;
import org.bonitasoft.shell.color.PrintColor;
import org.bonitasoft.shell.completer.ReflectMethodCompleter;
import org.bonitasoft.shell.completer.ReflectMethodHelpCompleter;

/**
 * @author Baptiste Mesta
 * 
 */
public class ReflectCommand<T extends ShellContext> extends ShellCommand<T> {

    private final String apiName;

    private final Method[] methods;

    private final ArrayList<String> methodNames;

    private final Map<String, List<Method>> methodMap = new HashMap<String, List<Method>>();

    public ReflectCommand(final String apiName, final Class<?> apiClass) {
        this.apiName = apiName;
        methods = apiClass.getMethods();
        HashSet<String> hashSet = new HashSet<String>();
        for (Method m : methods) {
            String methodName = m.getName();
            hashSet.add(methodName);
            if (!methodMap.containsKey(methodName)) {
                methodMap.put(methodName, new ArrayList<Method>());
            }
            methodMap.get(methodName).add(m);
        }
        methodNames = new ArrayList<String>(hashSet);
        Collections.sort(methodNames);
    }

    @Override
    public String getName() {
        return apiName;
    }

    @Override
    public boolean execute(final List<String> args, final T context) throws Exception {
        Object api = context.getApi(apiName);
        String methodName = args.get(0);
        List<String> parameters = args.subList(1, args.size());
        Method method = getMethod(methodName, methods, parameters);
        if (method != null) {
            Object result = invokeMethod(api, method, parameters);
            System.out.println(result);
        }
        return false;
    }

    /**
     * @param api
     * @param method
     * @param parameters
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private Object invokeMethod(final Object api, final Method method, final List<String> parameters) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        return method.invoke(api, castParameters(method.getParameterTypes(), parameters));
    }

    /**
     * @param classes
     * @param parameters
     * @return
     */
    private Object[] castParameters(final Class<?>[] classes, final List<String> parameters) {
        List<Object> list = new ArrayList<Object>(parameters.size());
        for (int i = 0; i < classes.length; i++) {
            Class<?> clazz = classes[i];
            Object parameterClass = clazz.getName();
            String parameter = parameters.get(i);
            Serializable casted;
            if (parameter == null) {
                casted = null;
            } else if (Boolean.class.getName().equals(parameterClass) || "bool".equals(parameterClass)) {
                casted = Boolean.parseBoolean(parameter);
            } else if (Long.class.getName().equals(parameterClass) || "long".equals(parameterClass)) {
                casted = Long.parseLong(parameter);
            } else if (Double.class.getName().equals(parameterClass) || "double".equals(parameterClass)) {
                casted = Double.parseDouble(parameter);
            } else if (Float.class.getName().equals(parameterClass) || "float".equals(parameterClass)) {
                casted = Float.parseFloat(parameter);
            } else if (Integer.class.getName().equals(parameterClass) || "int".equals(parameterClass)) {
                casted = Integer.parseInt(parameter);
            } else if (String.class.getName().equals(parameterClass)) {
                casted = parameter;
            } else {
                throw new IllegalArgumentException("Parameter is not a primitive: " + parameter);
            }
            list.add(casted);
        }
        return list.toArray();
    }

    private Method getMethod(final String methodName, final Method[] methods, final List<String> parameters) {
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                if (method.getParameterTypes().length == parameters.size()) {
                    return method;
                }
            }
        }
        throw new IllegalArgumentException("method does not exists");
    }

    @Override
    public void printHelp() {
        PrintColor.printGreenBold(apiName + " <method name> <parameters>");

    }

    @Override
    public boolean validate(final List<String> args) {
        return true;
    }

    @Override
    public List<Completer> getCompleters() {
        return Arrays.<Completer> asList(new ReflectMethodCompleter(this), new ReflectMethodHelpCompleter(this));
    }

    /**
     * @return
     */
    public List<String> getMethodNames() {
        return methodNames;
    }

    /**
     * @param methodName
     */
    public String getMethodHelp(final String methodName) {
        List<Method> list = methodMap.get(methodName);
        if (list != null) {
            String help = "";
            for (Method method : list) {
                help += methodName + "(";
                Class<?>[] parameterTypes = method.getParameterTypes();
                for (Class<?> class1 : parameterTypes) {
                    help += class1.getSimpleName() + ", ";
                }
                help += ")\n";
            }
            return help;
        }
        return null;
    }
}
