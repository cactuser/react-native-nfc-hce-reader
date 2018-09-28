import { NativeModules, DeviceEventEmitter } from "react-native";

const { RNHceReader } = NativeModules;

export default {
  supportNFC: function() {
    return RNHceReader.supportNFC;
  },
  startListenNFCStatus: function(callback) {
    DeviceEventEmitter.addListener("listenNFCStatus", callback);
  },
  stopListenNFCStatus: function(callback) {
    DeviceEventEmitter.removeListener("listenNFCStatus", callback);
  },
  startListenDataReceived: function(callback) {
    DeviceEventEmitter.addListener("receivedData", callback);
  },
  stopListenDataReceived: function(callback) {
    DeviceEventEmitter.removeListener("receivedData", callback);
  }
};
