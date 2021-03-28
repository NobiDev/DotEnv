package com.nobidev.dotenv.internal;

import com.nobidev.dotenv.DotenvEntry;
import com.nobidev.dotenv.DotenvException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DotenvParser extends DotenvParserAbstract {
    protected final Function<String, Boolean> isWhiteSpace = s -> matches("^\\s*$", s);
    protected final Function<String, Boolean> isComment = s -> s.startsWith("#") || s.startsWith("////");
    protected final Function<String, Boolean> isQuoted = s -> s.startsWith("\"") && s.endsWith("\"");
    protected final Function<String, DotenvEntry> parseLine = s -> matchEntry("^\\s*([\\w.\\-]+)\\s*(=)\\s*(.*)?\\s*$", s); // ^\s*([\w.\-]+)\s*(=)\s*(.*)?\s*$

    public DotenvParser(DotenvReader reader, boolean throwIfMissing, boolean throwIfMalformed) {
        this.reader = reader;
        this.throwIfMissing = throwIfMissing;
        this.throwIfMalformed = throwIfMalformed;
    }

    public List<DotenvEntry> parse() throws DotenvException {
        List<DotenvEntry> entries = new ArrayList<>();
        for (String line : lines()) {
            String l = line.trim();
            if (isWhiteSpace.apply(l) || isComment.apply(l) || isBlank(l)) continue;

            DotenvEntry entry = parseLine.apply(l);
            if (entry == null) {
                if (throwIfMalformed) throw new DotenvException("Malformed entry " + l);
                continue;
            }
            String key = entry.getKey();
            String value = normalizeValue(entry.getValue());
            entries.add(new DotenvEntry(key, value));
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
