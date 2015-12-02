package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Injector {

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    private static Object process(Class<?> rootClass, Map<Class<?>, Object> objects, Class<?>[] usedClasses) throws Exception {
        if (objects.containsKey(rootClass)) {
            return objects.get(rootClass);
        }

        Constructor<?> constructor = rootClass.getDeclaredConstructors()[0];
        ArrayList<Object> parametersList = new ArrayList<>();
        for (Class<?> parameterType : constructor.getParameterTypes()) {
            ArrayList<Class<?>> candidates = new ArrayList<>();
            for (Class<?> curClass : usedClasses) {
                if (parameterType.isAssignableFrom(curClass)) {
                    candidates.add(curClass);
                }
            }
            if (candidates.isEmpty()) {
                throw new ImplementationNotFoundException();
            }
            if (candidates.size() > 1) {
                throw new AmbiguousImplementationException();
            }
            parametersList.add(process(candidates.get(0), objects, usedClasses));
        }

        Object newObject = constructor.newInstance(parametersList.toArray());
        objects.put(rootClass, newObject);
        return newObject;
    }
    
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        HashMap<Class<?>, Object> objects = new HashMap<>();
        Class<?>[] usedClasses = new Class<?>[implementationClassNames.size() + 1];
        usedClasses[0] = Class.forName(rootClassName);
        int i = 1;
        for (String implementationClassName : implementationClassNames) {
            usedClasses[i++] = Class.forName(implementationClassName);
        }
        return process(Class.forName(rootClassName), objects, usedClasses);
    }

}