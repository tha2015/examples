package org.example;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FileDepsBuilderTest {

    final FileDepsBuilder builder = new FileDepsBuilder();
/*
    @Test
    public void testGetDuplicateNames() {

        final Path d1 = Paths.get("src/main/java/org/example/FileDeps.java");
        final Path d2 = Paths.get("src/main/java/org/example/FileDepsBuilder.java");
        final Path d3 = Paths.get("src/main/java/org/Filedeps.java");
        
        assertEquals(0, builder.getDuplicateNames(List.of(d1, d2)).size());

        final List<Path> duplicateNames = builder.getDuplicateNames(List.of(d1, d2, d3));

        assertEquals(2, duplicateNames.size());

        assertTrue(duplicateNames.contains(d1));

        assertTrue(duplicateNames.contains(d3));
    }
*/
    @Test
    public void testBuildDependencies() {
        final Path d1 = Paths.get("src/test/resources/file1.xml");
        final Path d2 = Paths.get("src/test/resources/file2.xml");
        
        final var deps = builder.buildDependencies(List.of(d1, d2));

        assertEquals(1, deps.size());

        assertEquals(1, deps.get(d1).size());

        assertEquals(d2, deps.get(d1).iterator().next());

        System.out.println(deps);
    }
    @Test
    public void testBuildDependencies2() {
        final Path d1 = Paths.get("src/test/resources/file1.xml");
        final Path d2 = Paths.get("src/test/resources/file2.xml");
        final Path d3 = Paths.get("src/test/resources/sub/file2.xml");

        final var deps = builder.buildDependencies(List.of(d1, d2, d3));

        assertEquals(1, deps.size());

        assertEquals(2, deps.get(d1).size());

        assertEquals(List.of(d2, d3), deps.get(d1).stream().sorted().collect(Collectors.toList()));

        System.out.println(deps);
    }
}