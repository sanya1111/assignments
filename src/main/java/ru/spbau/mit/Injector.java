package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Injector {

    private static final Map<Class<?>, Object> objects = new HashMap<Class<?>, Object>();
    private static final Set<Class<?>> pendingToObjects = new HashSet<Class<?>>();
    private static List<Class<?>> pendingClasses = new ArrayList<Class<?>>();
    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    private static Constructor<?> getConstructor(Class<?> clazz){
        return clazz.getDeclaredConstructors()[0];
    }
    private static Object process(Class<?> rootClass) throws Exception {
        if(pendingToObjects.contains(rootClass)){
            throw new InjectionCycleException();
        }
        if (objects.containsKey(rootClass)) {
            return objects.get(rootClass);
        }
        
        
        pendingToObjects.add(rootClass);
        Constructor<?> constructor = getConstructor(rootClass);
        List<Object> paramsObjects = new ArrayList<>();
        for (Class<?> param : constructor.getParameterTypes()) {
            ArrayList<Class<?>> candidates = new ArrayList<>();
            for (Class<?> headClass : pendingClasses) {
                if (param.isAssignableFrom(headClass)) {
                    candidates.add(headClass);
                }
            }
            if (candidates.isEmpty()) {
                throw new ImplementationNotFoundException();
            }
            if (candidates.size() > 1) {
                throw new AmbiguousImplementationException();
            }
            paramsObjects.add(process(candidates.get(0)));
        }
        
        
        Object newObject = constructor.newInstance(paramsObjects.toArray());
        objects.put(rootClass, newObject);
        pendingToObjects.remove(rootClass);
        return newObject;
    }
    
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        objects.clear();
        pendingToObjects.clear();
        pendingClasses.clear();
        Class<?> rootClass = Class.forName(rootClassName);
        pendingClasses.add(rootClass);
        for (String implementationClassName : implementationClassNames) {
            pendingClasses.add(Class.forName(implementationClassName));
        }
        return process(rootClass);
    }

}