package org.freejava.scanner.core;

import java.io.File;

import com.google.common.primitives.Doubles;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public class Scanner {
    public static void scan(File folder) {
        System.out.println("max ..." + Doubles.max(1.1, 1.2, 1.3));
        new Hello().hello();
        CLibrary.INSTANCE.printf("Hello, World\n");

    }

    public interface CLibrary extends Library {
        CLibrary INSTANCE = (CLibrary)
            Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"),
                               CLibrary.class);

        void printf(String format, Object... args);
    }
}