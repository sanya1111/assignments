package ru.spbau.mit;

public abstract class Function1<T, R> {
    public abstract R run(T input);

    public <R2> Function1<T, R2> compose(final Function1<? super R, R2> g) {
        return new Function1<T, R2>() {
            @Override
            public R2 run(T input) {
                return g.run(Function1.this.run(input));
            }
        };
    }
}