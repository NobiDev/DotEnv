package com.nobidev.dotenv.internal;

import com.nobidev.dotenv.DotEnvEntry;
import com.nobidev.dotenv.DotEnvException;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

public abstract class DotEnvParserAbstract {
    protected DotEnvReader reader;
    protected boolean throwIfMissing;
    protected boolean throwIfMalformed;

    @SuppressWarnings("SameParameterValue")
    protected static boolean matches(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    @SuppressWarnings("SameParameterValue")
    protected static DotEnvEntry matchEntry(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        boolean result = matcher.matches();
        if (!result || matcher.groupCount() < 3) return null;
        return new DotEnvEntry(matcher.group(1), matcher.group(3));
    }

    protected List<String> lines() throws DotEnvException {
        try {
            return reader.read();
        } catch (DotEnvException e) {
            if (throwIfMissing) throw e;
            return emptyList();
        } catch (IOException e) {
            throw new DotEnvException(e);
        }
    }

    protected boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
