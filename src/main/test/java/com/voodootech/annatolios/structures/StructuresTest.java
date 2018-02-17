package com.voodootech.annatolios.structures;

import com.voodootech.annatolios.common.AbstractContext;
import com.voodootech.annatolios.fixtures.SimpleDataProviderFixture;
import com.voodootech.annatolios.fixtures.SimpleUserFixture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

@RunWith(value = Parameterized.class)
public class StructuresTest {

    @Parameterized.Parameter
    public int defaultValue;

    @Parameterized.Parameters(name = "{index}: testDataStructure - {0}")
    public static Collection<Object[]> data() {
        return new ArrayList<Object[]>() {{ add(new Object[] { 1 }); add(new Object[] { 2 }); }};
    }

    @Test
    public void testIOContainer() {
        IOContainer<AbstractContext, SimpleUserFixture.SimpleUser> c0 = IOContainer.apply(SimpleDataProviderFixture.newInstance(), new AbstractContext(UUID.randomUUID().toString()));
        String userId = c0.mapTo(userE -> userE.getRight().getId());

        assert(userId.equals(c0.ref().getRight().getId()));

        IOContainer<AbstractContext, SimpleUserFixture.SimpleUser> c1 = IOContainer.apply(SimpleDataProviderFixture.newExplodingInstance(), new AbstractContext(UUID.randomUUID().toString()));

        Either<Exception, SimpleUserFixture.SimpleUser> optUserE = c1.ref();

        assert(optUserE.isLeft());
        assert(optUserE.state().equals(Either.STATE.LEFT));

        IOContainer<SimpleDataProviderFixture.SimpleContext, SimpleUserFixture.SimpleUser> c2 = IOContainer.apply(SimpleDataProviderFixture.newMockedDataProvider(), new SimpleDataProviderFixture.SimpleContext("123", "FIRST_NAME", "LAST_NAME", "testuser1@email.com"));

        SimpleUserFixture.SimpleUser user = c2.mapTo(u -> u.getRight());

        assert(user != null);
        assert(user.getId().equals("123"));
        assert(user.getEmail().equals("testuser1@email.com"));

        Either<Exception, SimpleUserFixture.SimpleUser> resultE = c2.ref(new SimpleDataProviderFixture.SimpleContext("bad_user_id", "", "", "bad_email@email.com"));

        assert(resultE.isLeft());
        assert(resultE.state().equals(Either.STATE.LEFT));
        assert(resultE.getLeft() != null);

        IOContainer<SimpleDataProviderFixture.SimpleContext, SimpleUserFixture.SimpleUser> c3 = IOContainer.apply(SimpleDataProviderFixture.newMockedDataProvider(), new SimpleDataProviderFixture.SimpleContext("bad_user_id", "", "", "bad_email@email.com"));

        Optional<SimpleUserFixture.SimpleUser> optUser = c3.mapTo(r -> {
            switch (r.state()) {
                case LEFT:
                    return Optional.<SimpleUserFixture.SimpleUser>empty();
                case RIGHT:
                    throw new IllegalArgumentException("Illegal state");
            }
            return Optional.ofNullable(r.getRight());
        });

        assert(!optUser.isPresent());

        resultE = null;
        resultE = c3.ref();

        assert(resultE != null);
        assert(resultE.state().equals(Either.STATE.LEFT));
        assert(resultE.getLeft() != null);

        IOContainer<SimpleDataProviderFixture.SimpleContext, SimpleUserFixture.SimpleUser> c4 = IOContainer.apply(SimpleDataProviderFixture.newMockedDataProvider());

        IOContainer<SimpleDataProviderFixture.SimpleContext, SimpleUserFixture.SimpleUser> r = c4.flatMap(new SimpleDataProviderFixture.SimpleContext("bad_user_id", "", "", "bad_email@email.com"), either -> either.isRight() ? either.getRight() : null);

        Either<Exception, SimpleUserFixture.SimpleUser> resultE2 = r.ref();

        assert(resultE2 != null);
        assert(resultE2.state().equals(Either.STATE.LEFT));
        assert(resultE2.getLeft() != null);

        IOContainer<SimpleDataProviderFixture.SimpleContext, SimpleUserFixture.SimpleUser> r2 = c4.flatMap(new SimpleDataProviderFixture.SimpleContext("123", "FIRST_NAME", "LAST_NAME", "testuser1@email.com"), either -> either.getRight());

        Either<Exception, SimpleUserFixture.SimpleUser> resultE3 = r2.ref();

        assert(resultE3 != null);
        assert(resultE3.state().equals(Either.STATE.RIGHT));
        assert(resultE3.getRight() != null);
    }

    @Test
    public void testEitherMonad() {
        int val = defaultValue;
        Either<String, Integer> e0 = Either.asLeft("Hello");

        assert(e0.state().equals(Either.STATE.LEFT));

        Tuple<String, Boolean> res0 = e0.mapTo(e -> {
            assert(e.isLeft());
            return e.isLeft() ? Tuple.from(e.getLeft(), true) : Tuple.from("ERROR", false);
        });

        assert(res0.getB());

        Either<String, Integer> res1 = e0.flatMap(e -> Either.asRight(99));

        assert(res1.state().equals(Either.STATE.RIGHT));
        assert(res1.isRight());
        assert(res1.getRight() == 99);

        Tuple<Either.STATE, String> t1 = res1.mapTo(e -> {
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

        Tuple<Boolean, Boolean> t1 = t0.mapTo(t -> Tuple.from(true, true));

        assert(t1.getA() && t1.getB());

        Integer int0 = t1.mapTo(t -> 500);

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

        Container<String> c42 = c0.map(i -> i.toString());

        assert(c0.ref() == 500);

        Container<String> c1 = c0.mapTo(c -> Container.apply("Hello, world!"));

        assert(c1.ref().equals("Hello, world!"));

        Container<String> c2 = c1.flatMap(c -> Container.apply("String change!"));

        assert(c2.ref().equals("String change!"));

        Integer c3 = c2.flatMap(c -> Container.apply("9000")).mapTo(s -> Integer.valueOf(s));

        assert(c3 == 9000);

        Container<Container<String>> result = c0.map(i -> Container.apply(i.toString()));
    }

    @Test
    public void testMultiContainerMonad() {
        MultiContainer<Integer> m0 = MultiContainer.apply(0, 1, 2, 3, 4);

        assert(m0.ref().size() == 5);

        Integer totalVal = m0.reduce(0, (i, acc) -> acc + i);

        assert(totalVal == 10);

        MultiContainer<String> m1 = MultiContainer.apply("0", "1", "2", "3", "4");

        assert(m1.ref().size() == 5);

        Integer int0 = m1.mapTo(c -> 100);

        assert(int0 == 100);

        Container<String> c0 = m1.map(i -> "0");

        MultiContainer<Integer> m42 = m1.mapMulti(s -> Integer.valueOf(s));

        List<Integer> integers = m42.ref();

        assert(integers.size() == 5);

        Integer total = integers.stream().reduce(0, (i, acc) -> acc + i);

        assert(total == totalVal);

        Integer stringsToTotalInteger = m1.fold(0, ((i, s) -> i + Integer.valueOf(s)));

        assert(stringsToTotalInteger == total && stringsToTotalInteger == totalVal);
    }
}
