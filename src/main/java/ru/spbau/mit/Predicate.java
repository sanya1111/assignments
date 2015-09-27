package ru.spbau.mit;

public abstract class Predicate<T> extends Function1<T, Boolean> {
    public abstract Boolean run(T input);

    public Predicate<T> or(final Predicate<? super T> with) {
        return new Predicate<T>() {
            @Override
            public Boolean run(T input) {
                return Predicate.this.run(input) || with.run(input);
            }
        };
    }

    public Predicate<T> and(final Predicate<? super T> with) {
        return new Predicate<T>() {
            @Override
            public Boolean run(T input) {
                return Predicate.this.run(input) && with.run(input);
            }
        };
    }

    public Predicate<T> not() {
        return new Predicate<T>() {
            @Override
            public Boolean run(T input) {
                return !Predicate.this.run(input);
            }
        };
    }

    public static final Predicate<Object> ALWAYS_TRUE = new Predicate<Object>() {
        @Override
        public Boolean run(Object input) {
            return true;
        }
    };

    public static final Predicate<Object> ALWAYS_FALSE = new Predicate<Object>() {
        @Override
        public Boolean run(Object input) {
            return false;
        }
    };
}
