module.exports = {
  dependency: {
    platforms: {
      android: {
        sourceDir: './android',
        packageImportPath: 'import com.consistynsdlsdk.ConsistyNsdlSdkPackage;',
        packageInstance: 'new ConsistyNsdlSdkPackage()',
        buildTypes: {
          release: {},
          debug: {},
        },
      },
    },
  },
};