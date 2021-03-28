package com.nobidev.dotenv;

import java.util.Set;

public interface DotEnv {

    static DotEnvBuilder configure() {
        return new DotEnvBuilder();
    }

    static DotEnv load() {
        return new DotEnvBuilder().load();
    }

    Set<DotEnvEntry> entries();

    Set<DotEnvEntry> entries(Filter filter);

    String get(String key);

    String get(String key, String defaultValue);

    enum Filter {
        DECLARED_IN_ENV_FILE
    }
}
