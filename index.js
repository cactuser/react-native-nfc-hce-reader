import { NativeModules, DeviceEventEmitter } from "react-native";

const { RNHceReader } = NativeModules;

export default {
  supportNFC: function() {
    return RNHceReader.supportNFC;
  },
  listenNFCStatus: function(callback) {
    DeviceEventEmitter.addListener("listenNFCStatus", resp => {
      if (resp.status) {
        callback(resp.status);
      }
    });
  },
  listenDataReceived: function(callback) {
    DeviceEventEmitter.addListener("receivedData", resp => {
      if (resp.data) {
        callback(resp.data);
      }
    });
  }
};
