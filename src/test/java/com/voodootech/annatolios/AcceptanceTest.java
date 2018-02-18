package com.voodootech.annatolios;

import com.voodootech.annatolios.common.AbstractContext;
import com.voodootech.annatolios.fixtures.SimpleDataProviderFixture;
import com.voodootech.annatolios.fixtures.SimpleUserFixture;
import com.voodootech.annatolios.structures.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@RunWith(value = Parameterized.class)
public class AcceptanceTest {

    @Parameterized.Parameter
    public int defaultValue;

    @Parameterized.Parameters(name = "{index}: testDataStructure - {0}")
    public static Collection<Object[]> data() {
        return new ArrayList<Object[]>() {{ add(new Object[] { 1 }); }};
    }

    @Test
    public void testMonadT() {
        MonadT<String> monad0   = () -> "Test string";
        MonadT<UUID> monad1     = monad0.<String, MonadT<UUID>>mapInternal(string -> () -> UUID.randomUUID());
        MonadT<UUID> monad2     = monad1.flatMap(uuid -> () -> UUID.randomUUID());

        Integer int0 = monad0.mapTo(string -> 9000);

        assert(monad0.ref().equals("Test string"));
        assert(int0 == 9000);
        assert(monad1.ref() instanceof UUID);
        assert(monad2.ref() instanceof UUID);
        assert(!monad2.ref().toString().equals(monad1.ref().toString()));
    }

    @Test
    public void testContainer() {
        Container<String> c0    = Container.apply("Test string");
        Container<Integer> c1   = c0.map(s -> 9000);

        assert(c0.ref() instanceof String);
        assert(c1.ref() instanceof Integer);
        assert(c0.ref().equals("Test string"));
        assert(c1.ref() == 9000);
    }

    @Test
    public void testMultiContainer() {
        MultiContainer<String> m0   = MultiContainer.apply("1", "2", "3", "4", "5");
        MultiContainer<Integer> m1  = m0.mapMulti(s -> Integer.valueOf(s));

        Integer sum     = m1.fold(0, ((acc, i) -> acc + i));
        String strSum0  = m0.reduce("", ((acc, s) -> String.format("%s%s", acc, s)));
        String strSum1  = m1.fold("", ((acc, i) -> String.format("%s%d", acc, i)));

        assert(m0.ref().size() == 5);
        assert(m1.ref().size() == 5);
        assert(sum == 15);
        assert(strSum0.equals("12345"));
        assert(strSum1.equals("12345"));
        assert(strSum0.equals(strSum1));
    }

    @Test
    public void testEitherMonad() {
        Either<String, Integer> e0 = Either.asLeft("Hello");
        Either<String, Integer> e1 = e0.flatMap(e -> Either.asRight(99));

        Tuple<String, Boolean> t0       = e0.mapTo(e -> e.isLeft() ? Tuple.from(e.getLeft(), true) : Tuple.from("ERROR", false));
        Tuple<Either.STATE, String> t1  = e1.mapTo(e -> {
            switch (e.state()) {
                case LEFT:
                    return Tuple.from(e.state(), e.getLeft());
                case RIGHT:
                    return Tuple.from(e.state(), e.getRight().toString());
                default:
                    return Tuple.<Either.STATE, String>empty();
            }
        });

        Optional<Integer> res2  = e1.mapRight(i -> i * 100);
        Optional<String> res3   = e1.mapLeft(s -> s);

        assert(e0.state().equals(Either.STATE.LEFT));
        assert(t0.getB());
        assert(e1.state().equals(Either.STATE.RIGHT));
        assert(e1.isRight());
        assert(e1.getRight() == 99);
        assert(t1.getA().equals(Either.STATE.RIGHT));
        assert(t1.getB().equals("99"));
        assert(res2.isPresent());
        assert(res2.get() == 9900);
        assert(!res3.isPresent());
    }

    @Test
    public void testTupleMonad() {
        Tuple<Boolean, Boolean> t0 = Tuple.from(false, false);
        Tuple<Boolean, Boolean> t1 = t0.mapTo(t -> Tuple.from(true, true));
        Tuple<Boolean, Boolean> t2 = t1.flatMap(t -> Tuple.from(false, false));
        Tuple<Integer, Integer> t3 = Tuple.from(2, 2);

        Integer int0 = t1.mapTo(t -> 500);
        Integer int1 = t3.map2((a, b) -> a + b);

        String str0 = t3.map2((a, b) -> String.valueOf(a + b));

        assert(!t0.getA() && !t0.getB());
        assert(t1.getA() && t1.getB());
        assert(int0 == 500);
        assert(!t2.getA() && !t2.getB());
        assert(int1 == 4);
        assert(str0.equals("4"));
    }

    @Test
    public void testIOContainer() {
        IOContainer<AbstractContext, SimpleUserFixture.SimpleUser> c0                           = IOContainer.apply(SimpleDataProviderFixture.newInstance(), new AbstractContext(UUID.randomUUID().toString()));
        IOContainer<AbstractContext, SimpleUserFixture.SimpleUser> c1                           = IOContainer.apply(SimpleDataProviderFixture.newExplodingInstance(), new AbstractContext(UUID.randomUUID().toString()));
        IOContainer<SimpleDataProviderFixture.SimpleContext, SimpleUserFixture.SimpleUser> c2   = IOContainer.apply(SimpleDataProviderFixture.newMockedDataProvider(), new SimpleDataProviderFixture.SimpleContext("123", "FIRST_NAME", "LAST_NAME", "testuser1@email.com"));
        IOContainer<SimpleDataProviderFixture.SimpleContext, SimpleUserFixture.SimpleUser> c3   = IOContainer.apply(SimpleDataProviderFixture.newMockedDataProvider(), new SimpleDataProviderFixture.SimpleContext("bad_user_id", "", "", "bad_email@email.com"));
        IOContainer<SimpleDataProviderFixture.SimpleContext, SimpleUserFixture.SimpleUser> c4   = IOContainer.apply(SimpleDataProviderFixture.newMockedDataProvider());
        IOContainer<SimpleDataProviderFixture.SimpleContext, SimpleUserFixture.SimpleUser> r0   = c4.flatMap(new SimpleDataProviderFixture.SimpleContext("bad_user_id", "", "", "bad_email@email.com"), either -> either.isRight() ? either.getRight() : null);
        IOContainer<SimpleDataProviderFixture.SimpleContext, SimpleUserFixture.SimpleUser> r1   = c4.flatMap(new SimpleDataProviderFixture.SimpleContext("123", "FIRST_NAME", "LAST_NAME", "testuser1@email.com"), either -> either.getRight());

        String userId                                               = c0.mapTo(userE -> userE.getRight().getId());
        Either<Exception, SimpleUserFixture.SimpleUser> optUserE    = c1.ref();
        SimpleUserFixture.SimpleUser user                           = c2.mapTo(u -> u.getRight());
        Either<Exception, SimpleUserFixture.SimpleUser> resultE0    = c2.ref(new SimpleDataProviderFixture.SimpleContext("bad_user_id", "", "", "bad_email@email.com"));

        Optional<SimpleUserFixture.SimpleUser> optUser              = c3.mapTo(r -> {
            switch (r.state()) {
                case LEFT:
                    return Optional.<SimpleUserFixture.SimpleUser>empty();
                case RIGHT:
                    throw new IllegalArgumentException("Illegal state");
            }
            return Optional.ofNullable(r.getRight());
        });

        Either<Exception, SimpleUserFixture.SimpleUser> resultE2    = r0.ref();
        Either<Exception, SimpleUserFixture.SimpleUser> resultE3    = r1.ref();
        Either<Exception, SimpleUserFixture.SimpleUser> resultE1    = c3.ref();

        assert(userId.equals(c0.ref().getRight().getId()));
        assert(optUserE.isLeft());
        assert(optUserE.state().equals(Either.STATE.LEFT));
        assert(user != null);
        assert(user.getId().equals("123"));
        assert(user.getEmail().equals("testuser1@email.com"));
        assert(resultE0.isLeft());
        assert(resultE0.state().equals(Either.STATE.LEFT));
        assert(resultE0.getLeft() != null);
        assert(!optUser.isPresent());
        assert(resultE1 != null);
        assert(resultE1.state().equals(Either.STATE.LEFT));
        assert(resultE1.getLeft() != null);
        assert(resultE2 != null);
        assert(resultE2.state().equals(Either.STATE.LEFT));
        assert(resultE2.getLeft() != null);
        assert(resultE3 != null);
        assert(resultE3.state().equals(Either.STATE.RIGHT));
        assert(resultE3.getRight() != null);
    }
}
