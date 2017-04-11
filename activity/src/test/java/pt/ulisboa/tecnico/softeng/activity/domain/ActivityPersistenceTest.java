package pt.ulisboa.tecnico.softeng.activity.domain;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class ActivityPersistenceTest {

    private static final String PROVIDER_CODE = "APR001";
    private static final String PROVIDER_NAME = "Best Provider";

    @Test
    public void success() {
        atomicProcess();
        atomicAssert();
    }

    @Atomic(mode = TxMode.WRITE)
    private void atomicProcess() {
        new ActivityProvider(PROVIDER_CODE, PROVIDER_NAME);
    }

    @Atomic(mode = TxMode.READ)
    private void atomicAssert() {
        Set<ActivityProvider> providers =
                FenixFramework.getDomainRoot().getProviderSet();
        assertEquals(1, providers.size());

        ActivityProvider provider = new ArrayList<>(providers).get(0);

        assertEquals(PROVIDER_CODE, provider.getCode());
        assertEquals(PROVIDER_NAME, provider.getName());
    }

    @After
    @Atomic(mode = TxMode.WRITE)
    public void tearDown() {
        Set<ActivityProvider> providers = FenixFramework.getDomainRoot().getProviderSet();

        for (ActivityProvider provider : providers) {
            provider.delete();
        }
    }
}
