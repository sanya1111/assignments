package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Injector {

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    private static Object process(Class<?> rootClass, Set<Class<?> > parents, Map<Class<?>, Object> objects, Class<?>[] usedClasses) throws Exception {
//        if(parents.contains(rootClass)){
//            throw new InjectionCycleException();
//        }
        if (objects.containsKey(rootClass)) {
            Object obj = objects.get(rootClass);
            if(obj == null){
                throw new InjectionCycleException();
            } else {
                return obj;
            }
        }
        objects.put(rootClass, null);
        
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
            parametersList.add(process(candidates.get(0), parents, objects, usedClasses));
        }

        parents.add(rootClass);
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
        return process(Class.forName(rootClassName),  new HashSet<Class<?>>(), objects, usedClasses);
    }

}