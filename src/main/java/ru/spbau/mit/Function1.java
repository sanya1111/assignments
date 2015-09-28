package ru.spbau.mit;

//interface Function{
//    public abstract Object run(Object input);
//};

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

    @SuppressWarnings("unchecked")
    public Object getYKomb() {
        Function1<Object, Object> func = new Function1<Object, Object>() {
            @Override
            public Object run(Object input) {
                return ((Function1<Object, Object>) input).run(input);
            }
        };
        return func.run(new Function1<Object, Object>() {
            @Override
            public Object run(final Object input) {

                return Function1.this.run((T) (new Function1<Object, Object>() {
                    @Override
                    public Object run(final Object valueInput) {
                        return ((Function1<Object, Object>) (((Function1<Object, Object>) input)
                                .run(input))).run(valueInput);
                    }

                }));
            }
        });
    }

    @SuppressWarnings("unchecked")
    public <R2> R2 runRecursion(Object input) {
        return (R2) ((Function1<Object, Object>) getYKomb()).run(input);
    }
}