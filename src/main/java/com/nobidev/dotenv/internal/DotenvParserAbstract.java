package com.nobidev.dotenv.internal;

import com.nobidev.dotenv.DotenvEntry;
import com.nobidev.dotenv.DotenvException;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;

public abstract class DotenvParserAbstract {
    protected DotenvReader reader;
    protected boolean throwIfMissing;
    protected boolean throwIfMalformed;

    @SuppressWarnings("SameParameterValue")
    protected static boolean matches(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    @SuppressWarnings("SameParameterValue")
    protected static DotenvEntry matchEntry(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        boolean result = matcher.matches();
        if (!result || matcher.groupCount() < 3) return null;
        return new DotenvEntry(matcher.group(1), matcher.group(3));
    }

    protected List<String> lines() throws DotenvException {
        try {
            return reader.read();
        } catch (DotenvException e) {
            if (throwIfMissing) throw e;
            return emptyList();
        } catch (IOException e) {
            throw new DotenvException(e);
        }
    }

    protected boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
