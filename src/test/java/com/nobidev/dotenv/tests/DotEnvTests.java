package com.nobidev.dotenv.tests;

import com.nobidev.dotenv.DotEnv;
import com.nobidev.dotenv.DotEnvEntry;
import com.nobidev.dotenv.DotEnvException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class DotEnvTests {
    private Map<String, String> envVars;

    @Before
    public void setUp() {
        envVars = new HashMap<>();
        envVars.put("MY_TEST_EV1", "my test ev 1");
        envVars.put("MY_TEST_EV2", "my test ev 2");
        envVars.put("WITHOUT_VALUE", "");
    }

    @Test(expected = DotEnvException.class)
    public void throwIfMalformedConfigured() {
        DotEnv.configure().load();
    }

    @Test(expected = DotEnvException.class)
    public void load() {
        DotEnv dotenv = DotEnv.load();

        for (String envName : envVars.keySet()) {
            assertEquals(envVars.get(envName), dotenv.get(envName));
        }
    }

    @Test
    public void iteratorOverDotEnv() {
        DotEnv dotenv = DotEnv.configure()
                .ignoreIfMalformed()
                .load();

        dotenv
                .entries()
                .forEach(e -> assertEquals(dotenv.get(e.getKey()), e.getValue()));

        for (DotEnvEntry e : dotenv.entries()) {
            assertEquals(dotenv.get(e.getKey()), e.getValue());
        }
    }

    @Test
    public void iteratorOverDotEnvWithFilter() {
        DotEnv dotenv = DotEnv.configure()
                .ignoreIfMalformed()
                .load();

        Set<DotEnvEntry> entriesInFile = dotenv.entries(DotEnv.Filter.DECLARED_IN_ENV_FILE);
        Set<DotEnvEntry> entriesAll = dotenv.entries();
        assertTrue(entriesInFile.size() < entriesAll.size());

        for (Map.Entry<String, String> e : envVars.entrySet()) {
            assertEquals(dotenv.get(e.getKey()), e.getValue());
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void failToRemoveFromDotEnv() {
        DotEnv dotenv = DotEnv.configure()
                .ignoreIfMalformed()
                .load();

        Iterator<DotEnvEntry> iter = dotenv.entries().iterator();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void failToAddToDotEnv() {

        DotEnv dotenv = DotEnv.configure()
                .ignoreIfMalformed()
                .load();

        dotenv.entries().add(new DotEnvEntry("new", "value"));
    }

    @Test
    public void configureWithIgnoreMalformed() {
        DotEnv dotenv = DotEnv.configure()
                .ignoreIfMalformed()
                .load();

        for (String envName : envVars.keySet()) {
            assertEquals(envVars.get(envName), dotenv.get(envName));
        }
    }

    @Test
    public void configureWithIgnoreMissingAndMalformed() {
        DotEnv dotenv = DotEnv.configure()
                .directory("/missing/dir")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        assertNotNull(dotenv.get("PATH"));
    }
}
