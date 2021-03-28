package com.nobidev.dotenv;

import java.util.Set;

public interface Dotenv {

    static DotenvBuilder configure() {
        return new DotenvBuilder();
    }

    static Dotenv load() {
        return new DotenvBuilder().load();
    }

    Set<DotenvEntry> entries();

    Set<DotenvEntry> entries(Filter filter);

    String get(String key);

    String get(String key, String defaultValue);

    enum Filter {
        DECLARED_IN_ENV_FILE
    }
}
