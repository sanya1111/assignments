package ru.spbau.mit;

public abstract class Predicate<T> extends Function1<T, Boolean>{    
    public abstract Boolean run(T input);

    public Predicate<T> or(final Predicate<T> with){
        return new Predicate<T>() {
            @Override
            public Boolean run(T input) {
                return with.run(input) || Predicate.this.run(input);
            }
        };
    }
    
    public Predicate<T> and(final Predicate<T> with){
        return new Predicate<T>() {
            @Override
            public Boolean run(T input) {
                return with.run(input) && Predicate.this.run(input);
            }
        };
    }
    
    public Predicate<T> not(){
        return new Predicate<T>() {
            @Override
            public Boolean run(T input) {
                return !Predicate.this.run(input);
            }
        };
    }
    
    public static final class ALWAYS_TRUE<T> extends Predicate<T>{
        @Override
        public Boolean run(T input) {
            return true;
        }
    };
    
    public static final class ALWAYS_FALSE<T> extends Predicate<T>{
        @Override
        public Boolean run(T input) {
            return false;
        }
    };
}   
