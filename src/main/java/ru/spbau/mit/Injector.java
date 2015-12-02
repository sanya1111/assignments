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
    private static Object process(Class<?> rootClass,  Map<Class<?>, Object> objects, Object[] usedClasses) throws Exception {
        if (objects.containsKey(rootClass)) {
            Object obj = objects.get(rootClass);
            if(obj != null){
                return obj;
            }
            throw new InjectionCycleException();
        }
        

        Constructor<?> constructor = rootClass.getDeclaredConstructors()[0];
        ArrayList<Object> parametersList = new ArrayList<>();
        for (Class<?> parameterType : constructor.getParameterTypes()) {
            ArrayList<Class<?>> candidates = new ArrayList<>();
            for (Object objCur : usedClasses) {
                Class<?> curClass = (Class<?>) objCur;
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
        List<Class<?>> converted = new ArrayList<Class<?>>();
        converted.add(Class.forName(rootClassName));
        for (String implementationClassName : implementationClassNames) {
            converted.add(Class.forName(implementationClassName));
        }
        return process(Class.forName(rootClassName),  objects, converted.toArray());
    }

}