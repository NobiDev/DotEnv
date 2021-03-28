package com.nobidev.dotenv;

import com.nobidev.dotenv.internal.DotenvParser;
import com.nobidev.dotenv.internal.DotenvReader;

import java.util.*;

import static java.util.stream.Collectors.*;

public class DotenvBuilder {
    protected String filename = ".env";
    protected String directoryPath = "./";
    protected boolean systemProperties = false;
    protected boolean throwIfMissing = true;
    protected boolean throwIfMalformed = true;

    public DotenvBuilder directory(String path) {
        this.directoryPath = path;
        return this;
    }

    public DotenvBuilder filename(String name) {
        filename = name;
        return this;
    }

    public DotenvBuilder ignoreIfMissing() {
        throwIfMissing = false;
        return this;
    }

    public DotenvBuilder ignoreIfMalformed() {
        throwIfMalformed = false;
        return this;
    }

    public DotenvBuilder systemProperties() {
        systemProperties = true;
        return this;
    }

    public Dotenv load() throws DotenvException {
        DotenvParser reader = new DotenvParser(
                new DotenvReader(directoryPath, filename),
                throwIfMissing,
                throwIfMalformed);
        List<DotenvEntry> env = reader.parse();
        if (systemProperties) {
            env.forEach(it -> System.setProperty(it.getKey(), it.getValue()));
        }
        return new DotenvImpl(env);
    }

    static class DotenvImpl implements Dotenv {
        protected final Map<String, String> envVars;
        protected final Set<DotenvEntry> set;
        protected final Set<DotenvEntry> setInFile;
        protected final Map<String, String> envVarsInFile;

        public DotenvImpl(List<DotenvEntry> envVars) {
            this.envVarsInFile = envVars.stream().collect(toMap(DotenvEntry::getKey, DotenvEntry::getValue));
            this.envVars = new HashMap<>(this.envVarsInFile);
            System.getenv().forEach(this.envVars::put);

            this.set = this.envVars.entrySet().stream()
                    .map(it -> new DotenvEntry(it.getKey(), it.getValue()))
                    .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));

            this.setInFile = this.envVarsInFile.entrySet().stream()
                    .map(it -> new DotenvEntry(it.getKey(), it.getValue()))
                    .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
        }

        @Override
        public Set<DotenvEntry> entries() {
            return set;
        }

        @Override
        public Set<DotenvEntry> entries(Dotenv.Filter filter) {
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
