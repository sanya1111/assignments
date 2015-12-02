package ru.spbau.mit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Injector {

    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     */
    public static Class<?>[] getArgs(Class<?> className){
        if(className.getConstructors().length == 0){
            return null;
        }
        return className.getConstructors()[0].getParameterTypes();
    }
    
    static boolean isChild(Class<?> arg, Class<?> what){
        return arg.isAssignableFrom(what);
    }
    
    public static Integer  find(Class<?> arg, List<Class<?>> implementationClassNames) throws AmbiguousImplementationException{
        Integer result = null;
        for(int i = 0; i < implementationClassNames.size(); i++){
            Class<?> item = implementationClassNames.get(i);
            if(isChild(arg, item)){
                if(result != null){
                    throw new AmbiguousImplementationException();
                }
                result = i;
            }
        }
        return result;
    }
    
    public static Object process(Class<?> rootClassName, List<Class<?>> implementationClassNames, List<Class<?>> parent, Map<Class<?>, Object> objects) throws AmbiguousImplementationException, InjectionCycleException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ImplementationNotFoundException{
        Class<?>[] args = getArgs(rootClassName);
        if(args == null){
            return rootClassName.newInstance();
        }
        List<Class<?>> togo = new ArrayList<Class<?>>();
        for(Class<?> arg: args){
            Integer with = find(arg, implementationClassNames);
            if(with == null){
                if(find(arg, parent) != null){
                    throw new InjectionCycleException();
                }
                throw new AmbiguousImplementationException();
            }
            if(objects.containsKey(arg)){
                throw new AmbiguousImplementationException();
            }
            togo.add(implementationClassNames.get(with));
            implementationClassNames.remove(arg);
        }
        List<Object> okey = new ArrayList<Object>();
        for(int i = 0; i < args.length; i++){
            parent.add(rootClassName);
            okey.add(process(togo.get(i), implementationClassNames, parent, objects));
            parent.remove(rootClassName);
        }
        return rootClassName.getConstructors()[0].newInstance(okey.toArray());
    }
    
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        List<Class<?>> converted = new ArrayList<Class<?>>();
        for(String name : implementationClassNames){
            converted.add(Class.forName(name));
        }
        return process(Class.forName(rootClassName), converted, new ArrayList<Class<?>>(), new HashMap<Class<?>, Object>());
    }
}