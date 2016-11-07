Deploy this app to Google App Engine:

1. Registering a new application at http://appengine.google.com/ and remember the app-id
2. Replace all references to 'embetin2' by <app-id> in appengine-web.xml and pom.xml files
3. Use command
mvn install
to upload the application to Google App Engine server
4. Test by browsing to http://<app-id>.appspot.com

Developing with Eclipse:

1. Install 'Google Plugin for Eclipse' to your Eclipse IDE (update site: http://dl.google.com/eclipse/plugin/3.4)
2. Use command
mvn package eclipse:eclipse
to create Eclipse project files
3. Import project to Eclipse using 'File/Import/Import Existing projects to Workspace')
4. Add classpath variable M2_REPO to point to <your home>/.m2/repository
