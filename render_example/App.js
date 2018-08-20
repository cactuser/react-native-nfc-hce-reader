//@flow

import React, { Component } from "react";
import { Platform, StyleSheet, Text, View } from "react-native";
import HCEReader from "react-native-nfc-hce-reader";

export default class App extends Component {
  state = {
    support: false,
    enabled: false,
    warnText: null,
    receivedData: "請觸碰ＨＣＥ裝置"
  };

  componentDidMount = () => {
    let { support, enabled } = HCEReader.supportNFC();
    this.setState({ support, enabled });
    if (support) {
      this._listenNFCStatus();
      if (!enabled) return;
      this._listenDataReceived();
    } else {
      this.setState({ warnText: "你的裝置不支援ＮＦＣ" });
    }
  };

  _listenNFCStatus = () => {
    HCEReader.listenNFCStatus(enabled => {
      let warnText = enabled ? null : "請開啟ＮＦＣ";
      this.setState({ enabled, warnText });
    });
  };

  _listenDataReceived = () => {
    HCEReader.listenDataReceived(data => {
      this.setState({ receivedData: data });
    });
  };

  render() {
    const { warnText, receivedData } = this.state;
    return (
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center" }}>
        <Text style={{ fontSize: 24 }}>
          {warnText ? warnText : receivedData}
        </Text>
      </View>
    );
  }
}
