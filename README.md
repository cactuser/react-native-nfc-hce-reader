
# react-native-hce-reader

## Getting started

`$ npm install react-native-hce-reader --save`

### Mostly automatic installation

`$ react-native link react-native-hce-reader`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import studio.bb.rnlib.RNHceReaderPackage;` to the imports at the top of the file
  - Add `new RNHceReaderPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-hce-reader'
  	project(':react-native-hce-reader').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-hce-reader/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-hce-reader')
  	```


## Usage
```javascript
import RNHceReader from 'react-native-hce-reader';

// TODO: What to do with the module?
RNHceReader;
```
  