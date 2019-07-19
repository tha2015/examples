#!/bin/bash
find ../../eclipse-builder/src/main -type f -name '*.groovy' -o -name '*.xml' | java -cp target/classes org.example.FileDeps