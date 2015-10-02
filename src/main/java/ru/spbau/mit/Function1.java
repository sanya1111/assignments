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
    
    static private abstract class Function1Itself<R> extends Function1<Function1Itself<R>, R>{
        @Override
        public abstract R run(Function1Itself<R> input);
    };
    
    /*
     * static, because T, R must be functions, java isn't support partial specialization
     * T2, R2 - because java isn't support generic params in static method
     */
    
    public static <T2, R2> Function1<T2, R2> getYKomb(final Function1<Function1<T2, R2>, Function1<T2, R2> > wrapper) {
        Function1Itself<Function1<T2, R2> > func = new Function1Itself<Function1<T2, R2>>() {
            @Override
            public Function1<T2, R2> run(Function1Itself<Function1<T2, R2>> input) {
                return input.run(input);
            }
        };
        return func.run(new Function1Itself<Function1<T2, R2> >() {
            @Override
            public Function1<T2, R2> run(final Function1Itself<Function1<T2, R2>> input) {
                return wrapper.run(new Function1<T2, R2>() {
                  @Override
                  public R2 run(final T2 valueInput) {    
                      return input.run(input).run(valueInput);
                  }

              });
            }
        });
    }

    public static <T2, R2> R2 runRecursion(final Function1<Function1<T2, R2>, Function1<T2, R2> > wrapper, T2 input) {
        return getYKomb(wrapper).run(input);
    }
}