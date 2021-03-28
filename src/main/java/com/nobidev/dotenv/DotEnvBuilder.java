package com.nobidev.dotenv;

import com.nobidev.dotenv.internal.DotEnvParser;
import com.nobidev.dotenv.internal.DotEnvReader;

import java.util.*;

import static java.util.stream.Collectors.*;

public class DotEnvBuilder {
    protected String filename = ".env";
    protected String directoryPath = "./";
    protected boolean systemProperties = false;
    protected boolean throwIfMissing = true;
    protected boolean throwIfMalformed = true;

    public DotEnvBuilder directory(String path) {
        this.directoryPath = path;
        return this;
    }

    public DotEnvBuilder filename(String name) {
        filename = name;
        return this;
    }

    public DotEnvBuilder ignoreIfMissing() {
        throwIfMissing = false;
        return this;
    }

    public DotEnvBuilder ignoreIfMalformed() {
        throwIfMalformed = false;
        return this;
    }

    public DotEnvBuilder systemProperties() {
        systemProperties = true;
        return this;
    }

    public DotEnv load() throws DotEnvException {
        DotEnvParser reader = new DotEnvParser(
                new DotEnvReader(directoryPath, filename),
                throwIfMissing,
                throwIfMalformed);
        List<DotEnvEntry> env = reader.parse();
        if (systemProperties) {
            env.forEach(it -> System.setProperty(it.getKey(), it.getValue()));
        }
        return new DotEnvImpl(env);
    }

    static class DotEnvImpl implements DotEnv {
        protected final Map<String, String> envVars;
        protected final Set<DotEnvEntry> set;
        protected final Set<DotEnvEntry> setInFile;
        protected final Map<String, String> envVarsInFile;

        public DotEnvImpl(List<DotEnvEntry> envVars) {
            this.envVarsInFile = envVars.stream().collect(toMap(DotEnvEntry::getKey, DotEnvEntry::getValue));
            this.envVars = new HashMap<>(this.envVarsInFile);
            System.getenv().forEach(this.envVars::put);

            this.set = this.envVars.entrySet().stream()
                    .map(it -> new DotEnvEntry(it.getKey(), it.getValue()))
                    .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));

            this.setInFile = this.envVarsInFile.entrySet().stream()
                    .map(it -> new DotEnvEntry(it.getKey(), it.getValue()))
                    .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
        }

        @Override
        public Set<DotEnvEntry> entries() {
            return set;
        }

        @Override
        public Set<DotEnvEntry> entries(DotEnv.Filter filter) {
            if (filter != null) return setInFile;
            return entries();
        }

        @Override
        public String get(String key) {
            String value = System.getenv(key);
            return value != null ? value : envVars.get(key);
        }

        @Override
        public String get(String key, String defaultValue) {
            String value = this.get(key);
            return value != null ? value : defaultValue;
        }
    }
}
