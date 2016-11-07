package manager

import groovy.util.AntBuilder;

import java.io.File;
import java.net.URL;

class PhoneGapSDKManager {

	URL downloadSDKUrl = new URL('https://github.com/phonegap/phonegap/zipball/2.0.0')
	File sdkDir = new File(System.getProperty("java.io.tmpdir"), 'phonegap_sdk')

	AntBuilder ant = new AntBuilder()
	Downloader downloader = new Downloader()

	void install() {
		new Downloader().install(ant, downloadSDKUrl, sdkDir)
	}

	public static void main(String[] args) {
		PhoneGapSDKManager main = new PhoneGapSDKManager()
		main.install()
	}

}
