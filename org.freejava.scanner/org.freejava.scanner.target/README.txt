
To run install new jar files to p2 repo

1. Download osgi bundle jar to bundles/plugins/ folder

2. Run mvn tycho-p2-extras:publish-features-and-bundles to install the bundles to p2/ repo

NOTE: bundles/plugins/ folder is just used for installing jars to to p2/ repo only. After building the p2/ repo, you can delete it if you want.