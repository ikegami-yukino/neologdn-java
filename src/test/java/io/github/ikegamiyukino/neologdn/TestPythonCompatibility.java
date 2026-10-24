package io.github.ikegamiyukino.neologdn;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.junit.Test;

/** Checks stored expectations from the actual Python extension, without requiring Python at test time. */
public class TestPythonCompatibility {
    @Test
    public void matchesPython056() throws Exception {
        String externalFixture = System.getProperty("neologdn.compatibilityFixture");
        try (InputStream input = externalFixture == null
                ? getClass().getResourceAsStream("/python-0.5.6.tsv")
                : Files.newInputStream(Paths.get(externalFixture));
             BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            NeologdNormalizer normalizer = new NeologdNormalizer();
            String line;
            int lineNumber = 0;
            int cases = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.startsWith("#")) {
                    continue;
                }
                String[] fields = line.split("\t", -1);
                assertEquals("Fixture columns on line " + lineNumber, 7, fields.length);
                String text = decode(fields[1]);
                int repeat = Integer.parseInt(fields[2]);
                int maximum = Integer.parseInt(fields[4]);
                String actual;
                if ("shorten".equals(fields[0])) {
                    actual = normalizer.shortenRepeat(text, repeat, maximum);
                } else {
                    assertEquals("normalize", fields[0]);
                    actual = normalizer.normalize(text, repeat, Boolean.parseBoolean(fields[3]), maximum, fields[5]);
                }
                assertEquals("Python fixture line " + lineNumber, decode(fields[6]), actual);
                cases++;
            }
            org.junit.Assert.assertTrue("Fixture must contain cases", cases > 0);
        }
    }

    private static String decode(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }
}
