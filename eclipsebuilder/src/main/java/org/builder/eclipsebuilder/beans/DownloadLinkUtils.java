package org.builder.eclipsebuilder.beans;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.builder.eclipsebuilder.beans.Configuration.BuildType;

public class DownloadLinkUtils {

    public static Artifact parseDownloadLink(String urlStr)
            throws MalformedURLException {
        Artifact artifact;

        artifact = parseURLType1(urlStr);
        if (artifact == null)
            artifact = parseURLType21(urlStr);
        if (artifact == null)
            artifact = parseURLType2223(urlStr);
        if (artifact == null)
            artifact = parseURLType3(urlStr);
        if (artifact == null)
            artifact = parseURLType4(urlStr);
        if (artifact == null)
            artifact = parseURLType5(urlStr);

        if (artifact != null && artifact.getVersion() != null && artifact.getBuildType() == null) {
            // build type is defaulted to RELEASE if version contains numbers only
            Pattern pattern = Pattern.compile("\\d+(\\.\\d+)+");
            if (pattern.matcher(artifact.getVersion()).matches()) {
                artifact.setBuildType(BuildType.RELEASE);
            }
        }
        return artifact;
    }

    /**
     * Parse URLs using type 5:
     * http://filesync4eclipse.googlecode.com/files/de.loskutov.FileSync_1.3.2.1.jar
     * http://prdownloads.sourceforge.net/findbugs/edu.umd.cs.findbugs.plugin.eclipse_1.3.2.20080222.zip?download
     *
     * @param urlStr
     * @return
     * @throws MalformedURLException
     */
    private static Artifact parseURLType5(String urlStr) {
        Artifact artifact = null;

        String patternStr;
        Pattern pattern;
        Matcher m;

        String[] patterns = new String[]{"([^/&=]+.jar)", "([^/&=]+.zip)"};
        for (int i = 0; i < patterns.length; i++) {
            patternStr = patterns[i];
            pattern = Pattern.compile(patternStr);
            m = pattern.matcher(urlStr);
            if (m.find()) { // match type 5
                artifact = new Artifact();
                String fileName = m.group();
                parseVersionInfoString(fileName.substring(0, fileName.length() - 4), artifact);
                artifact.setFileName(fileName);
            }
        }

        return artifact;
    }


    /**
     * Parse URLs using type 4:
     * http://download.eclipse.org/birt/downloads/build.php?build=M-R1-2.3M5-200802191157
     * http://www.eclipse.org/tptp/home/downloads/?ver=4.5.0
     * http://www.eclipse.org/tptp/home/downloads/?buildId=TPTP-4.5.0M5-200802170400,
     *
     * @param urlStr
     * @return
     * @throws MalformedURLException
     */
    private static Artifact parseURLType4(String urlStr) {
        Artifact artifact = null;

        String patternStr;
        Pattern pattern;
        Matcher m;

        String[] patterns = new String[]{
                "/build.php\\?build=([^/]+)$",
                "/downloads/\\?ver=([^/]+)$",
                "/downloads/\\?buildId=([^/]+)$"};
        for (int i = 0; i < patterns.length; i++) {
            patternStr = patterns[i];
            pattern = Pattern.compile(patternStr);
            m = pattern.matcher(urlStr);
            if (m.find()) { // match type 4
                artifact = new Artifact();
                parseVersionInfoString(m.group(1), artifact);
            }
        }

        return artifact;
    }

    /**
     * Parse URLs using type 3:
     * http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/checksum/eclipse-SDK-3.4M5-win32.zip.md5
     * http://download.eclipse.org/modeling/emf/emf/downloads/drops/2.3.1/R200709252135/emf-sdo-xsd-SDK-2.3.1.zip.md5
     *
     * @param urlStr
     * @return
     * @throws MalformedURLException
     */
    private static Artifact parseURLType3(String urlStr)
            throws MalformedURLException {
        Artifact artifact = null;
        URL url = new URL(urlStr);
        String path = url.getPath();
        String query = url.getQuery();
        if (path != null && path.indexOf("/drops/") != -1 && query == null
                && (path.endsWith(".zip") || path.endsWith(".md5"))) {
            String[] splits = path.split("/drops/");
            if (splits.length == 2 && splits[1].indexOf('/') != -1) { // match
                                                                        // type
                                                                        // 3
                artifact = new Artifact();
                String fileName = path.substring(path.lastIndexOf('/') + 1);
                String[] parts = splits[1].split("/");
                Pattern pattern = Pattern.compile("[0-9]");
                String versionString;
                if (pattern.matcher(parts[1]).find()
                        && !parts[1].equals(fileName)) {
                    versionString = parts[0] + "/" + parts[1];
                } else {
                    versionString = parts[0];
                }
                parseVersionInfoString(versionString, artifact);
                artifact.setFileName(fileName);
                String[] strings = fileName.split("-" + artifact.getVersion());
                artifact.setArtifactId(strings[0]);
            }
        }
        return artifact;
    }

    /**
     * Parse URLs using type 2.2 and 2.3:
     * http://www.eclipse.org/downloads/download.php?file=/eclipse/downloads/drops/S-3.4M5-200802071530/eclipse-SDK-3.4M5-win32.zip
     *
     * http://www.eclipse.org/downloads/download.php?file=/eclipse/downloads/drops/S-3.4M5-200802071530/eclipse-SDK-3.4M5-win32.zip&url=http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/eclipse-SDK-3.4M5-win32.zip&mirror_id=1
     *
     * http://www.eclipse.org/downloads/download.php?file=/webtools/downloads/drops/R3.0/S-3.0M5-20080218021547/wtp-sdk-S-3.0M5-20080218021547.zip
     *
     * @param urlStr
     * @return
     * @throws MalformedURLException
     */
    private static Artifact parseURLType2223(String urlStr)
            throws MalformedURLException {
        Artifact artifact = null;
        URL url = new URL(urlStr);
        String path = url.getPath();
        String query = url.getQuery();
        if (path != null && path.endsWith("/download.php") && query != null && query.contains("file=")) {
            String patternStr = "file=([^&=]+)";
            Pattern pattern = Pattern.compile(patternStr);
            Matcher m = pattern.matcher(urlStr);
            if (m.find()) {
                String filePath = m.group(1);
                artifact = new Artifact();
                String versionInfo = filePath;
                if (versionInfo.endsWith(".zip")) versionInfo = versionInfo.substring(0, versionInfo.length() - 4);
                parseVersionInfoString(versionInfo, artifact);
                String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
                artifact.setFileName(fileName);

                String separator = "-";
                if (artifact.getVersion() != null) {
                    String version = artifact.getVersion();
                    int index = fileName.indexOf(version);
                    if (index > 0) {
                        char sepChar = fileName.charAt(index - 1);
                        if (sepChar == '_') {
                            separator = "_";
                        }
                    }
                }
                String[] strings = fileName.split(separator);
                String artifactId = null;
                Pattern pattern2 = Pattern.compile("\\d");
                for (String s : strings) {
                    if (s.length() == 1 || pattern2.matcher(s).find()) break;
                    if (artifactId == null) artifactId = s; else artifactId += separator + s;
                }
                artifact.setArtifactId(artifactId);
            }
        }
        return artifact;
    }

    /**
     * Parse URLs using type 2.1:
     * http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/download.php?dropFile=eclipse-SDK-3.4M5-win32.zip
     *
     * @param urlStr
     * @return
     * @throws MalformedURLException
     */
    private static Artifact parseURLType21(String urlStr)
            throws MalformedURLException {
        Artifact artifact = null;
        URL url = new URL(urlStr);
        String path = url.getPath();
        String query = url.getQuery();
        if (path != null && path.endsWith("/download.php") && query != null
                && query.contains("dropFile=")) {
            String patternStr1 = "/downloads/drops/([^/]+)/download.php";
            Pattern pattern1 = Pattern.compile(patternStr1);
            Matcher m1 = pattern1.matcher(urlStr);
            String patternStr2 = "dropFile=(.+)";
            Pattern pattern2 = Pattern.compile(patternStr2);
            Matcher m2 = pattern2.matcher(urlStr);
            if (m1.find() && m2.find()) { // match type 2.1
                artifact = new Artifact();
                parseVersionInfoString(m1.group(1), artifact);
                String fileName = m2.group(1);
                artifact.setFileName(fileName);
                String[] strings = fileName.split("-" + artifact.getVersion()
                        + "-");
                artifact.setArtifactId(strings[0]);
            }
        }
        return artifact;
    }

    private static void parseVersionInfoString(String versionString,
            Artifact artifact) {
        Pattern versionPattern = Pattern.compile("\\d+(\\.\\d+)+([a-zA-Z]\\d+)?");

        String separator = "-";
        String fileName = versionString.substring(versionString.lastIndexOf('/') + 1);
        Matcher m = versionPattern.matcher(fileName);
        if (m.find()) {
            String version = m.group();
            int index = fileName.indexOf(version);
            if (index > 0) {
                char sepChar = fileName.charAt(index - 1);
                if (sepChar == '_') {
                    separator = "_";
                }
            }
        }
        List<String> strings = Arrays.asList(versionString.split("[\\" + separator + "/]"));
        List<String> versionInfo = new ArrayList<String>();
        for (Iterator<String> it = strings.iterator(); it.hasNext();) {
            String s = it.next();
            if (s.length() > 1 && !Character.isDigit(s.charAt(0))
                    && Character.isDigit(s.charAt(1))) {
                versionInfo.add(Character.toString(s.charAt(0)));
                versionInfo.add(s.substring(1));
            } else {
                versionInfo.add(s);
            }
        }
        Collections.reverse(versionInfo);
        for (String s : versionInfo) {
            // Build Type
            if (artifact.getBuildType() == null && s.length() == 1) {
                switch (s.charAt(0)) {
                case 'R':
                    artifact.setBuildType(BuildType.RELEASE);
                    break;
                case 'S':
                    artifact.setBuildType(BuildType.STABLE);
                    break;
                case 'I':
                    artifact.setBuildType(BuildType.INTEGRATION);
                    break;
                case 'N':
                    artifact.setBuildType(BuildType.NIGHTLY);
                    break;
                }
            }
            // Version
            Matcher m2 = versionPattern.matcher(s);
            if (artifact.getVersion() == null && m2.find()) {
                artifact.setVersion(m2.group());
            }
            // BuildDate
            if (artifact.getBuildDate() == null && s.indexOf('.') == -1
                    && s.length() >= 8 && s.charAt(0) == '2') {
                artifact.setBuildDate(s);
            }
        }
    }

    /**
     * Parse URLs using type 1.1:
     * http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/index.php
     * and 1.2:
     * http://download.eclipse.org/webtools/downloads/drops/R3.0/S-3.0M5-20080218021547/
     *
     * @param urlStr
     * @return
     */
    private static Artifact parseURLType1(String urlStr) {
        Artifact artifact = null;

        String patternStr = "/downloads/drops/(.+)/(index.php)?$";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher m = pattern.matcher(urlStr);
        if (m.find()) { // match type 1.1 and 1.2
            artifact = new Artifact();
            parseVersionInfoString(m.group(1), artifact);
        }
        return artifact;
    }

    public DownloadLinkUtils() {
        super();
    }

}