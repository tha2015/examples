package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

public class FileDepsBuilder {

    public Map<Path, Set<Path>> buildDependencies(final List<Path> files) {

        // Validate input

        final List<Path> duplicateNames = getDuplicateNames(files);
        if (!duplicateNames.isEmpty()) {
            System.err.println("ERROR: Duplicate file names:" + duplicateNames);

            final var duplicateWithSameContent = getDuplicateContents(duplicateNames);

            System.err.println("ERROR: Duplicate content:" + duplicateWithSameContent);

            System.err.println("ERROR: Duplicate name but content:" +
                    duplicateNames.stream().filter(f-> duplicateWithSameContent.stream().flatMap(c->c.stream()).noneMatch(g->g.equals(f))).collect(toList()));

            // Remove all files with same name
            files.removeAll(duplicateNames);
        }


        // map name to file content
        final Map<String, String> name2Content = files.stream().collect(toMap(p -> getName(p), p -> this.read(p)));

        // map file to name
        final Map<String, Path> name2File = files.stream().collect(toMap(p -> getName(p), identity()));

        final Map<Path, Set<Path>> dependencies = new HashMap<>();

        // build parent-child dependencies
        files.forEach(p -> {
            name2Content.entrySet().stream()
                    .filter(e -> !e.getKey().equalsIgnoreCase(getName(p)))
                    .filter(e -> isContains(getName(p), e))
                    .forEach(e -> {
                        Path parent = name2File.get(e.getKey());
                        dependencies.merge(parent, new HashSet<>(Arrays.asList(p)), (v1,v2) -> { v1.addAll(v2); return v1;});
                    });
        });

        /*
        // add missing nodes (which don't have parent)
        final Map<Path, Set<Path>> deps2 = dependencies.entrySet().stream().flatMap(e -> e.getValue().stream()).distinct()
                .filter(p -> ! dependencies.keySet().contains(p))
                .collect(Collectors.toMap(p->p, p-> Collections.emptySet()));
        dependencies.putAll(deps2);
*/


        return dependencies;

    }

    public void printToDot(Map<Path, Set<Path>> dependencies, PrintWriter writer) {

        writer.format("digraph \"FileDeps\" {%n");


        dependencies.entrySet().stream().flatMap(e -> e.getValue().stream().map(to -> new Path[] {e.getKey(), to})).forEach(pair -> {
            Path from = pair[0];
            Path to = pair[1];

            writer.format("   %-50s -> \"%s\";%n",
                    String.format("\"%s\"", from.toString()),
                    String.format("%s", to.toString()));


        });


        writer.println("}");
    }

    private List<List<Path>> getDuplicateContents(List<Path> files) {

        final Map<String, List<Path>> content2File = files.stream().collect(groupingBy(f -> read(f)));

        final List<List<Path>> groups = content2File.entrySet().stream().filter(e -> e.getValue().size() > 1).map(e -> e.getValue()).collect(toList());

        return groups;
    }

    public boolean isContains(String name, Map.Entry<String, String> e) {
        return e.getValue().toUpperCase().contains(name.toUpperCase());
    }

    public String getName(Path p) {
        return p.getFileName().toString();
    }

    public List<Path> getDuplicateNames(List<Path> files) {
        return files.stream().collect(groupingBy(p -> getName(p).toUpperCase()))
                .entrySet().stream()
                .filter(e -> e.getValue().size() > 1).flatMap(
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
