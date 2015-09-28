package ru.spbau.mit;

public abstract class Function2<T, T2, R> {
    public abstract R run(T inputFirst, T2 inputSecond);

    public <R2> Function2<T, T2, R2> compose(final Function1<? super R, R2> g) {
        return new Function2<T, T2, R2>() {
            @Override
            public R2 run(T inputFirst, T2 inputSecond) {
                return g.run(Function2.this.run(inputFirst, inputSecond));
            }
        };
    }

    public Function1<T2, R> bind1(final T arg1) {
        return new Function1<T2, R>() {
            @Override
            public R run(T2 input) {
                return Function2.this.run(arg1, input);
            }
        };
    }

    public Function1<T, R> bind2(final T2 arg2) {
        return new Function1<T, R>() {
            @Override
            public R run(T input) {
                return Function2.this.run(input, arg2);
            }
        };
    }

    public Function1<T, Function1<T2, R>> curry() {
        return new Function1<T, Function1<T2, R>>() {
            @Override
            public Function1<T2, R> run(T input) {
                return Function2.this.bind1(input);
            }
        };
    }    
}
