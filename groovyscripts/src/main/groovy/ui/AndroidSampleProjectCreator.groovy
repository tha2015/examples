package ui

class AndroidSampleProjectCreator {

	static main(args) {
		def dir = new File('d:/programs/android-sdk/samples/android-19')
		dir = new File('d:/programs/android_sdk_r21/extras/android/support/samples')
		dir.eachDir {
			File projectDir = it
			gen(projectDir)
		}
	}

	static gen(File projectDir) {
		if (new File(projectDir, "AndroidManifest.xml").exists()) {
			def projectName = projectDir.name
			String projectText = AndroidSampleProjectCreator.class.getResourceAsStream("/project").text
			new File(projectDir, ".project").text = projectText.replaceAll("\\\$\\{projectName\\}", projectName)

			String classpathText = AndroidSampleProjectCreator.class.getResourceAsStream("/classpath").text
			new File(projectDir, ".classpath").text = classpathText.replaceAll("\\\$\\{projectName\\}", projectName)

			String projectPropsText = AndroidSampleProjectCreator.class.getResourceAsStream("/project.properties").text
			new File(projectDir, "project.properties").text = projectPropsText.replaceAll("\\\$\\{projectName\\}", projectName)
		} else {
			projectDir.eachDir {
				File projectDir2 = it
				gen(projectDir2)
			}
		}
	}

}
