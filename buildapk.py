import os,re
import subprocess



def setVersonName(version):
	f = open("./app/build.gradle", "r")
	txt = f.read()
	f.close()

	txt = re.sub(r'versionName "[^"]+"', 'versionName "'+version+'"', txt)
	f = open("./app/build.gradle", "w")
	f.write(txt)
	f.close()


if __name__=="__main__":  
	import sys  
	vIndex = -1
	if "-v" in sys.argv:
		vIndex = sys.argv.index("-v")
	if vIndex>0 and len(sys.argv)>vIndex+1 :
		print("v")
		setVersonName(sys.argv[vIndex+1])
	
	
	#os.system("./gradlew assembleRelease")
	apkPath = "./app/build/outputs/apk"
	apkFile = apkPath + "/app-release.apk"
	os.popen("unzip -qo "+apkFile+" -d "+apkPath+"/temp")
	print os.popen("keytool -printcert -file " + apkPath + "/temp/META-INF/CERT.RSA").read()
	appInfo = os.popen("$ANDROID_HOME/build-tools/23.0.3/aapt dump badging " + apkFile).read()
	package = re.match(r".* name='([^']+)'.*", appInfo, re.M|re.I)
	print "package: " + package.group(1)
	versionCode = re.match(r".* versionCode='([^']+)'.*", appInfo, re.M|re.I)
	print "versionCode: " + versionCode.group(1)
	versionName = re.match(r".* versionName='([^']+)'.*", appInfo, re.M|re.I)
	print "versionName: " + versionName.group(1)
	platformBuildVersionName = re.match(r".* platformBuildVersionName='([^']+)'.*", appInfo, re.M|re.I)
	print "platformBuildVersionName: " + platformBuildVersionName.group(1)
	applicationLabel = re.match(r"[\s\S]*application: label='([^']+)'.*", appInfo, re.M|re.I)
	print "applicationLabel: " + applicationLabel.group(1)
	subprocess.Popen("nautilus ./app/build/outputs/apk/app-release.apk", shell=True,  stdout=subprocess.PIPE, stderr=subprocess.STDOUT)



