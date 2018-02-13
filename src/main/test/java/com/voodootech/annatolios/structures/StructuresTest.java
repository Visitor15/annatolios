package com.voodootech.annatolios.structures;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.Optional;

@RunWith(Parameterized.class)
public class StructuresTest {

    @Test
    public void testEitherMonad() {
        Either<String, Integer> e0 = Either.asLeft("Hello");

        assert(e0.state().equals(Either.STATE.LEFT));

        Tuple<String, Boolean> res0 = e0.map(e -> {
            assert(e.isLeft());
            return e.isLeft() ? Tuple.from(e.getLeft(), true) : Tuple.from("ERROR", false);
        });

        assert(res0.getB());

        Either<String, Integer> res1 = e0.flatMap(e -> Either.asRight(99));

        assert(res1.state().equals(Either.STATE.RIGHT));
        assert(res1.isRight());
        assert(res1.getRight() == 99);

        Tuple<Either.STATE, String> t1 = res1.map(e -> {
            switch (e.state()) {
                case LEFT:
                    return Tuple.from(e.state(), e.getLeft());
                case RIGHT:
                    return Tuple.from(e.state(), e.getRight().toString());
                default:
                    return Tuple.<Either.STATE, String>empty();
            }
        });

        assert(t1.getA().equals(Either.STATE.RIGHT));
        assert(t1.getB().equals("99"));

        Optional<Integer> res2 = res1.mapRight(i -> i * 100);

        assert(res2.isPresent());
        assert(res2.get() == 9900);

        Optional<String> res3 = res1.mapLeft(s -> s);

        assert(!res3.isPresent());
    }

    @Test
    public void testTupleMonad() {
        Tuple<Boolean, Boolean> t0 = Tuple.from(false, false);

        assert(!t0.getA() && !t0.getB());

        Tuple<Boolean, Boolean> t1 = t0.map(t -> Tuple.from(true, true));

        assert(t1.getA() && t1.getB());

        Integer int0 = t1.map(t -> 500);

        assert(int0 == 500);

        Tuple<Boolean, Boolean> t2 = t1.flatMap(t -> Tuple.from(false, false));

        assert(!t2.getA() && !t2.getB());

        Tuple<Integer, Integer> t3 = Tuple.from(2, 2);

        Integer res = t3.map2((a, b) -> a + b);

        assert(res == 4);

        String res2 = t3.map2((a, b) -> String.valueOf(a + b));

        assert(res2.equals("4"));
    }

    @Test
    public void testContainerMonad() {
        Container<Integer> c0 = Container.apply(500);

        assert(c0.getRef() == 500);

        Container<String> c1 = c0.map(c -> Container.apply("Hello, world!"));

        assert(c1.getRef().equals("Hello, world!"));

        Container<String> c2 = c1.flatMap(c -> Container.apply("String change!"));

        assert(c2.getRef().equals("String change!"));

        Integer c3 = c2.flatMap(c -> Container.apply("9000")).map(s -> Integer.valueOf(s));

        assert(c3 == 9000);
    }

    @Test
    public void testMultiContainerMonad() {
        MultiContainer<Integer> m0 = MultiContainer.apply(0, 1, 2, 3, 4);

        assert(m0.getRef().size() == 5);

        Integer totalVal = m0.reduce(0, (i, acc) -> acc + i);

        assert(totalVal == 10);

        MultiContainer<String> m1 = MultiContainer.apply("0", "1", "2", "3", "4");

        assert(m1.getRef().size() == 5);

        Integer int0 = m1.map(c -> 100);

        assert(int0 == 100);

        List<Integer> integers = m1.mapMulti(s -> Integer.valueOf(s));

        assert(integers.size() == 5);

        Integer total = integers.stream().reduce(0, (i, acc) -> acc + i);

        assert(total == totalVal);

        Integer stringsToTotalInteger = m1.fold(0, ((i, s) -> i + Integer.valueOf(s)));

        assert(stringsToTotalInteger == total && stringsToTotalInteger == totalVal);
    }
}
