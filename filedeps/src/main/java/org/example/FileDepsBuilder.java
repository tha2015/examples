package org.example;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.StringUtils.indexOfIgnoreCase;

public class FileDepsBuilder {

    public Map<Path, Set<Path>> buildDependencies(final List<Path> files) {

        // Validate input
/*
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
*/
        // build parent-child dependencies

        final Map<Path, Set<Path>> dependencies = files.stream().flatMap(f1 -> files.stream().filter(f2 -> fileHasReferences(f1, f2, files)).map(f2 -> new Path[]{f1, f2})).collect(groupingBy(p -> p[0], mapping(p -> p[1], toSet())));
/*
        files.forEach(p -> {
            name2Content.entrySet().stream()
                    .filter(e -> !e.getKey().equalsIgnoreCase(getName(p)))
                    .filter(e -> isContains(getName(p), e))
                    .forEach(e -> {
                        Path parent = name2File.get(e.getKey());
                        dependencies.merge(parent, new HashSet<>(Arrays.asList(p)), (v1,v2) -> { v1.addAll(v2); return v1;});
                    });
        });


        // add missing nodes (which don't have parent)
        final Map<Path, Set<Path>> deps2 = dependencies.entrySet().stream().flatMap(e -> e.getValue().stream()).distinct()
                .filter(p -> ! dependencies.keySet().contains(p))
                .collect(Collectors.toMap(p->p, p-> Collections.emptySet()));
        dependencies.putAll(deps2);
*/


        return dependencies;

    }

    public boolean fileHasReferences(Path f1, Path f2, List<Path> files) {

        if (!f1.equals(f2) && Files.isRegularFile(f1) && Files.isRegularFile(f2)) {
            var f1text = readFile(f1);

            var f2name = f2.getFileName().toString();

            int last = -1;

            while (last + 1 < f1text.length()) {

                int foundIdx = indexOfIgnoreCase(f1text, f2name, last + 1);
                if (foundIdx != -1) {
                    // found name, check path

                    char charAfterName = (foundIdx + f2name.length() >= f1text.length()) ? '\0' : f1text.charAt(foundIdx + f2name.length());
                    if (!isPathCharNotSpace(charAfterName)) {
                        Path p = extractPath(f1text, f2name, last, foundIdx);
                        Path p2 = findClosestMatch(p, files, f1);

                        if (p2 == f2) return true;
                    }


                    last = foundIdx + f2name.length() - 1;
                } else {
                    break;
                }
            }
        }
        return false;
    }

    private Path extractPath(String f1text, String f2name, int last, int foundIdx) {
        int startPath = foundIdx;
        while (startPath > last && isPathChar(f1text.charAt(startPath))) {
            startPath --;
        }
        startPath ++;

        String pathStr = f1text.substring(startPath, foundIdx + f2name.length()).replace('\\', '/');
        return Paths.get(pathStr).normalize();
    }

    private Path findClosestMatch(Path p, List<Path> ps, Path base) {
        final List<Path> matchName = ps.stream().map(p -> p.getFileName()).filter(n -> n.toString().equalsIgnoreCase(p.getFileName().toString())).collect(toList());
        if (matchName.size() == 1) {
            return matchName.get(0);
        } else {
            // multiple matching names
            ???
        }
    }

    private boolean isPathChar(char c) {
        return c == ' ' || isPathCharNotSpace(c);
    }

    private boolean isPathCharNotSpace(char c) {
        return StringUtils.isAlphanumeric(String.valueOf(c)) ||  (c == '\\') ||  (c == '/');
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
/*
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
*/

    private Map<Path, String> fileCache = new HashMap<>();

    private String readFile(Path f) {

        if (fileCache.containsKey(f)) return fileCache.get(f);

        try {
            fileCache.put(f, Files.readString(f));
            return fileCache.get(f);

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
