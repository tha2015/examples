package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileDeps {

    public static boolean validFile(Path file) {
        return Files.isRegularFile(file) && !file.getFileName().endsWith(".DS_Store");
    }

    public static void main(String[] args) throws IOException {

        List<Path> files;


        if (args.length > 0) {
            var path = args[0];

            try (Stream<Path> pathStream = Files.find(Paths.get(path), Integer.MAX_VALUE, (f, a) -> FileDeps.validFile(f))) {
                files = pathStream.map(p -> p.toAbsolutePath().normalize()).collect(Collectors.toList());
            }

        } else {

            Scanner in = new Scanner(System.in);
            files = new ArrayList<>();

            while (in.hasNext()) {
                var line = in.next();
                files.add(Paths.get(line).toAbsolutePath().normalize());
            }

        }

        System.out.println(files);

    }

}
