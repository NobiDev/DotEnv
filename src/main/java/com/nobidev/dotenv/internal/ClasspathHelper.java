package com.nobidev.dotenv.internal;

import com.nobidev.dotenv.DotEnvException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class ClasspathHelper {
    static Stream<String> loadFileFromClasspath(String location) {
        Class<ClasspathHelper> loader = ClasspathHelper.class;
        InputStream inputStream = loader.getResourceAsStream(location);
        if (inputStream == null) {
            inputStream = loader.getResourceAsStream(location);
        }
        if (inputStream == null) {
            inputStream = ClassLoader.getSystemResourceAsStream(location);
        }

        if (inputStream == null) {
            throw new DotEnvException("Could not find " + location + " on the classpath");
        }
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
        List<String> lines = new ArrayList<>();
        while (scanner.hasNext()) {
            lines.add(scanner.nextLine());
        }
        return lines.stream();
    }
}
