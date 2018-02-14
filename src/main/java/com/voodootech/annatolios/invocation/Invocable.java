package com.voodootech.annatolios.invocation;

import com.voodootech.annatolios.common.AbstractContext;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Invocable<T extends AbstractContext, E extends Exception> {

    E buildErrorEntity(final String errorMessage);

    <A extends Exception> E buildErrorEntity(final A exception);

    default <A, B> B invokeWithTryCatch(T c, A a, Function<E, B> errorFunc, BiFunction<T, A, B> func) {
        return invokeWithTryCatchInternal(c, Optional.ofNullable(a), Optional.of(func), Optional.empty(), Optional.empty(), errorFunc);
    }

    default <A> A invokeWithTryCatch(T c, Function<E, A> errorFunc, Function<T, A> func) {
        return invokeWithTryCatchInternal(c, Optional.empty(), Optional.empty(), Optional.of(func), Optional.empty(), errorFunc);
    }

    default <A> A invokeWithTryCatch(Function<E, A> errorFunc, Supplier<A> func) {
        return invokeWithTryCatchInternal(AbstractContext.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(func), errorFunc);
    }

    default <A, B> B invokeWithTryCatchInternal(AbstractContext c,
                                                Optional<A> optA,
                                                Optional<BiFunction<T, A, B>> biFunc,
                                                Optional<Function<T, B>> func,
                                                Optional<Supplier<B>> supplierFunc,
                                                Function<E, B> errorFunc) {
        boolean optBiFunc       = biFunc.isPresent();
        boolean optFunc         = func.isPresent();
        boolean optSupplierFunc = supplierFunc.isPresent();
        boolean elementA        = optA.isPresent();

        if((optBiFunc && optFunc && optSupplierFunc) || (optBiFunc && optFunc) || (optBiFunc && optSupplierFunc) || (optFunc && optSupplierFunc)) {
            return errorFunc.apply(buildErrorEntity("Multiple functions defined"));
        }
        else if(!optBiFunc && !optFunc && !optSupplierFunc) {
            return errorFunc.apply(buildErrorEntity("No function defined"));
        }
        if(optBiFunc && !elementA) {
            return errorFunc.apply(buildErrorEntity("Undefined argument for BiFunction"));
        }

        try {
            if(optBiFunc) {
                return biFunc.get().apply((T) c, optA.get()); // TODO - don't cast
            } else if(optFunc) {
                return func.get().apply((T) c); // TODO - don't cast
            } else {
                return supplierFunc.get().get();
            }
        } catch (Exception e) {
            return errorFunc.apply(buildErrorEntity(e));
        }
    }
}
