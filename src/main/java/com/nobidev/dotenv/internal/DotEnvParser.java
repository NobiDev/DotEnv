package com.nobidev.dotenv.internal;

import com.nobidev.dotenv.DotEnvEntry;
import com.nobidev.dotenv.DotEnvException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DotEnvParser extends DotEnvParserAbstract {
    protected final Function<String, Boolean> isWhiteSpace = s -> matches("^\\s*$", s);
    protected final Function<String, Boolean> isComment = s -> s.startsWith("#") || s.startsWith("////");
    protected final Function<String, Boolean> isQuoted = s -> s.startsWith("\"") && s.endsWith("\"");
    protected final Function<String, DotEnvEntry> parseLine = s -> matchEntry("^\\s*([\\w.\\-]+)\\s*(=)\\s*(.*)?\\s*$", s); // ^\s*([\w.\-]+)\s*(=)\s*(.*)?\s*$

    public DotEnvParser(DotEnvReader reader, boolean throwIfMissing, boolean throwIfMalformed) {
        this.reader = reader;
        this.throwIfMissing = throwIfMissing;
        this.throwIfMalformed = throwIfMalformed;
    }

    public List<DotEnvEntry> parse() throws DotEnvException {
        List<DotEnvEntry> entries = new ArrayList<>();
        for (String line : lines()) {
            String l = line.trim();
            if (isWhiteSpace.apply(l) || isComment.apply(l) || isBlank(l)) continue;

            DotEnvEntry entry = parseLine.apply(l);
            if (entry == null) {
                if (throwIfMalformed) throw new DotEnvException("Malformed entry " + l);
                continue;
            }
            String key = entry.getKey();
            String value = normalizeValue(entry.getValue());
            entries.add(new DotEnvEntry(key, value));
        }
        return entries;
    }

    protected String normalizeValue(String value) {
        String tr = value.trim();
        return isQuoted.apply(tr)
                ? tr.substring(1, value.length() - 1)
                : tr;
    }
}
