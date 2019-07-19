package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

public class FileDepsBuilder {

    public Map<Path, Set<Path>> buildDependencies(final List<Path> files) {

        // Validate input

        final List<Path> duplicateNames = getDuplicateNames(files);
        if (!duplicateNames.isEmpty()) {
            System.err.println("ERROR: Duplicate file names:" + duplicateNames);
        }


        // map name to file content
        final Map<String, String> name2Content = files.stream().collect(toMap(p -> getName(p), p -> this.read(p)));

        // map file to name
        final Map<String, Path> name2File = files.stream().collect(toMap(p -> getName(p), identity()));

        Map<Path, Set<Path>> deps = new HashMap<>();
        files.forEach(p -> {
            final String name = getName(p);

            name2Content.entrySet().stream()
                    .filter(e -> !e.getKey().equals(name))
                    .filter(e -> isContains(name, e))
                    .forEach(e -> {

                        Path parent = name2File.get(e.getKey());

                        deps.merge(parent, new HashSet<>(Arrays.asList(p)), (v1,v2) -> { v1.addAll(v2); return v1;});


                    });



        });

        return deps;

    }

    public boolean isContains(String name, Map.Entry<String, String> e) {
        return e.getValue().toUpperCase().contains(name.toUpperCase());
    }

    public String getName(Path p) {
        return p.getFileName().toString();
    }

    public List<Path> getDuplicateNames(List<Path> files) {
        return files.stream().collect(groupingBy(p -> getName(p).toUpperCase())).entrySet().stream().filter(e -> e.getValue().size() > 1).flatMap(
                e -> e.getValue().stream()).distinct().collect(toList());
    }

    private String read(Path f) {
        try {
            return Files.readString(f);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
