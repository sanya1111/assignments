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
        if(objects.containsKey(rootClassName)){
            Object obj = objects.get(rootClassName);
            if(obj == null){
                throw new InjectionCycleException();
            }
            return obj;
        }
        Class<?>[] args = getArgs(rootClassName);
        if(args == null){
            Object obj= rootClassName.newInstance();
            objects.put(rootClassName, obj);
            return obj;
        }
        
        List<Class<?>> togo = new ArrayList<Class<?>>();
        for(Class<?> arg: args){
            if(objects.containsKey(arg)){
                continue;
            }
            Integer with = find(arg, implementationClassNames);
            if(with == null){
                if(find(arg, parent) != null){
                    throw new InjectionCycleException();
                }
                throw new ImplementationNotFoundException();
            }
            togo.add(implementationClassNames.get(with));
            implementationClassNames.remove(arg);
        }
        if(togo.size() != args.length){
            throw new ImplementationNotFoundException();
        }
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < args.length; i++){
            parent.add(rootClassName);
            process(togo.get(i), implementationClassNames, parent, objects);
            list.add(objects.get(togo.get(i)));
            parent.remove(rootClassName);
        }
        Object obj = rootClassName.getConstructors()[0].newInstance(list.toArray());
        objects.put(rootClassName, obj);
        return obj;
    }
    
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        List<Class<?>> converted = new ArrayList<Class<?>>();
        for(String name : implementationClassNames){
            converted.add(Class.forName(name));
        }
        return process(Class.forName(rootClassName), converted, new ArrayList<Class<?>>(), new HashMap<Class<?>, Object>());
    }
}