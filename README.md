# react-native-nfc-hce-reader

Inspired by [android-CardReader](https://github.com/googlesamples/android-CardReader

🌟🔥Only supported Android !🔥🌟

## Requisites

- Android (API 19+)
- Android SDK 27
- Android Build Tools v27.0.2

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

## Setup

1. Open up `android/app/src/main/AndroidManifest.xml`

- Add `<uses-permission android:name="android.permission.NFC" />`

-Add

```xml
<intent-filter>
<action android:name="android.nfc.action.TECH_DISCOVERED" />
</intent-filter>

            <meta-data
                android:name="android.nfc.action.TECH_DISCOVERED"
                android:resource="@xml/nfc_tech_filter" />
```

2. Create `nfc_tech_filter.xml` in `android/app/src/main/res/xml/`

- Add code in `aid_list.xml`

```xml
<?xml version="1.0" encoding="utf-8"?><!-- This file is used as part of the filter for incoming NFC TECH_DISCOVERED intents. -->
<resources xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- Android's host card emulation feature only supports the IsoDep protocol. -->
    <tech-list>
        <tech>android.nfc.tech.IsoDep</tech>
    </tech-list>
</resources>
```

The aid need to customize

- Edit AID in `react-native-nfc-hce/android/src/main/java/studio/bb/rnlib/RNHceReaderModule.java`

```JAVA
public class RNHceReaderModule extends ReactContextBaseJavaModule implements NfcAdapter.ReaderCallback, LifecycleEventListener {

    private static final String TAG = "ReaderModule";

    private static final String AID = "F201808175";
```

## Usage

```javascript
import HCEReader from "react-native-hce-reader";

componentDidMount = () => {
  let { support, enabled } = HCEReader.supportNFC();
  this.setState({ support, enabled });
  if (support) {
    this._listenNFCStatus();
    if (!enabled) return;
    this._listenDataReceived();
  } else {
    //你的裝置不支援ＮＦＣ
  }
};

_listenNFCStatus = () => {
  HCEReader.listenNFCStatus(enabled => {
    this.setState({ enabled, warnText });
  });
};

_listenDataReceived = () => {
  HCEReader.listenDataReceived(data => {
    this.setState({ receivedData: data });
  });
};
```

### supportNFC()

Get NFC supported and enabled

### listenNFCStatus(enabled:boolean)

Listen NFC enabled status

### listenDataReceived(data:string)

Listen Data Received
