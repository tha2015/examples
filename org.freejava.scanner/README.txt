DEVELOPMENT GUIDE

1. Build application from command line

> cd scanner
> mvn package

The generated zip files will be in scanner\org.freejava.scanner.product\target folder

NOTE: When building on Windows, native launchers for *nix / macos do not have execution permission set correctly (see Bug 355370 - Product-Export for MacOS does not set executable bit on native launcher (when building on Windows) )

2. Working from Eclipse
a. Import all projects to Eclipse
b. Open /org.freejava.scanner.target/org.freejava.scanner.target.target and click on the Set As Target Platform link at the top and verify that the target is used for Eclipse


3. To add bundles from to local p2 repo to use from your plugin/feature: Read org.freejava.scanner.target/README.txt