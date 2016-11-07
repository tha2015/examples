package manager

import java.util.regex.Matcher
import java.util.regex.Pattern

class AndroidSDKManager {
	URL downloadSDKUrl = new URL('http://dl.google.com/android/android-sdk_r22.3-windows.zip')
	public File sdkDir = null
	String optionalFilter

	Downloader downloader = new Downloader()

	void installSDK() {
		if (sdkDir == null) {
			def env = System.getenv()
			if (env['ANDROID_HOME']) {
				sdkDir = new File(env['ANDROID_HOME'])
			} else {
				sdkDir = new File(System.getProperty("java.io.tmpdir"), 'android_sdk')
			}
		}

		if (!sdkDir.exists() || sdkDir.isDirectory() && sdkDir.listFiles().length == 0) {
			// Install SDK
			downloader.install(downloadSDKUrl, sdkDir)
			// Download more components
			downloadAndroidSDKComponents()
		}
	}

	public void downloadAndroidSDKComponents() {
		File toolsDir = new File(sdkDir, "tools")
		AntBuilder ant = new AntBuilder()

		String androidCmd = 'cmd.exe /c android.bat'
		String adbCmd = new File(sdkDir, "platform-tools/adb.exe").absolutePath
		if (!System.properties['os.name'].toLowerCase().contains('windows')) {
			androidCmd = '/bin/sh -c android'
			adbCmd = new File(sdkDir, "platform-tools/adb").absolutePath
		}

		// Create filter
		String filter = this.optionalFilter
		if (filter == null || filter.equals('')) {
			ant.echo(message: 'Will build Android SDK component filter.')

			// execute command android list sdk --all to get list of components
			String cmd0 = androidCmd + ' list sdk --no-ui --all'
			ant.echo(message: 'Executing ' + cmd0)
			Process p0 = cmd0.execute(null, toolsDir)
			def out = new StringBuilder()
			def err = new StringBuilder()
			p0.waitForProcessOutput(out, err)
			ant.echo(message: out.toString())

			// parse output to find necessary numbers
			ant.echo(message: 'Parsing output.')

			def maxAPI = '0'
			Reader reader = new BufferedReader(new StringReader(out.toString()))
			String line
			while ((line = reader.readLine()) != null) {
				Matcher matcher = Pattern.compile('Android .+ API ([0-9]+)').matcher(line)
				if (matcher.find()) {
					if (Integer.parseInt(matcher.group(1)) > Integer.parseInt(maxAPI)) maxAPI = matcher.group(1)
				}
			}
			maxAPI = 'API ' + maxAPI
			def compatAPI = 'API 10' // Android 2.3.3, API 10

			reader = new BufferedReader(new StringReader(out.toString()))
			while ((line = reader.readLine()) != null) {
				line = line.toLowerCase()
				if (line.contains(maxAPI.toLowerCase()) ||
					line.contains(compatAPI.toLowerCase()) ||
					line.contains('Platform-tools'.toLowerCase()) ||
					line.contains('Documentation'.toLowerCase()) ||
					line.contains('Support Library'.toLowerCase()) ||
					line.contains('AdMob'.toLowerCase()) ||
					line.contains('Analytics'.toLowerCase()) ||
					line.contains('Cloud Messaging'.toLowerCase()) ||
					line.contains('Play services'.toLowerCase()) ||
					line.contains('Play APK Expansion'.toLowerCase()) ||
					line.contains('Play Billing'.toLowerCase()) ||
					line.contains('Play Licensing'.toLowerCase()) ||
					line.contains('USB Driver'.toLowerCase()) ||
					line.contains('Web Driver'.toLowerCase())
					) {
					if (filter != null && !filter.equals('')) filter += ',' else filter = ''
					filter += line.split("\\-")[0].trim()
				}
			}
			ant.echo(message: 'Selected filter=' + filter)
		}

		// Download components
		String cmd1 = androidCmd + ' update sdk --all --no-ui --force --filter ' + filter
		ant.echo(message: 'Executing ' + cmd1)
		Process p1 = cmd1.execute(null, toolsDir)

		// workaround issue http://code.google.com/p/android/issues/detail?id=18868 to fix issue process hangs
		Thread.start{
			def reader = new BufferedReader(new InputStreamReader(p1.in))
			String line
			while ((line = reader.readLine()) != null) {
				ant.echo(message: line)
				if (line.trim().startsWith("Done.") && line.trim().endsWith("installed.")) {
					println 'GOTCHA!'
					String cmd2 = '"' + adbCmd + '" kill-server'
					ant.echo(message: 'Executing ' + cmd2)
					cmd2.execute(null, new File(sdkDir, "platform-tools")).waitFor()
					println 'KILLED SERVER!'
					break
				}
			}
		}
		p1.waitFor()
	}
}
