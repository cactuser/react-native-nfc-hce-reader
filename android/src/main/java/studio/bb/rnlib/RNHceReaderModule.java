
package studio.bb.rnlib;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RNHceReaderModule extends ReactContextBaseJavaModule implements NfcAdapter.ReaderCallback, LifecycleEventListener {

    private static final String TAG = "ReaderModule";

    private static final String AID = "F201808175";
    private static final String SELECT_APDU_HEADER = "00A40400";
    private static final byte[] SELECT_OK_SW = {(byte) 0x90, (byte) 0x00};

    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;

    private final ReactApplicationContext reactContext;

    public RNHceReaderModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        reactContext.addLifecycleEventListener(this);
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        this.reactContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public String getName() {
        return "RNHceReader";
    }

    private WritableMap supportNFC() {
        NfcManager manager = (NfcManager) this.reactContext.getSystemService(this.reactContext.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        WritableMap map = Arguments.createMap();
        if (adapter != null) {
            map.putBoolean("support", true);
            if (adapter.isEnabled()) {
                map.putBoolean("enabled", true);
            } else {
                map.putBoolean("enabled", false);
            }
        } else {
            map.putBoolean("support", false);
            map.putBoolean("enabled", false);
        }
        return map;
    }

    private void enableReaderMode() {
        Log.i(TAG, "Enabling reader mode");
        Activity activity = this.getCurrentActivity();
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.enableReaderMode(this.getCurrentActivity(), this, READER_FLAGS, null);
        }
    }

    private void disableReaderMode() {
        Log.i(TAG, "Disabling reader mode");
        Activity activity = this.getCurrentActivity();
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(activity);
        if (nfc != null) {
            nfc.disableReaderMode(activity);
        }
    }


    @Override
    public void onTagDiscovered(Tag tag) {
        Log.i(TAG, "New tag discovered");
        IsoDep isoDep = IsoDep.get(tag);
        if (isoDep != null) {
            try {
                isoDep.connect();
                Log.i(TAG, "Requesting remote AID: " + AID);
                byte[] command = BuildSelectApdu(AID);
                Log.i(TAG, "Sending: " + ByteArrayToHexString(command));
                byte[] result = isoDep.transceive(command);
                int resultLength = result.length;
                byte[] statusWord = {result[resultLength - 2], result[resultLength - 1]};
                byte[] payload = Arrays.copyOf(result, resultLength - 2);
                if (Arrays.equals(SELECT_OK_SW, statusWord)) {
                    String readData = new String(payload, "UTF-8");
                    Log.i(TAG, "Received: " + readData);
                    WritableMap obj = Arguments.createMap();
                    obj.putString("data", readData);
                    sendEvent(this.reactContext, "receivedData", obj);
                }
            } catch (IOException e) {
                Log.e(TAG, "Error communicating with card: " + e.toString());
            }
        }
    }

    public static byte[] BuildSelectApdu(String aid) {
        return HexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X", aid.length() / 2) + aid);
    }

    public static String ByteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] HexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("supportNFC", supportNFC());
        return constants;
    }

    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap payload) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, payload);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)) {
                final int state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE,
                        NfcAdapter.STATE_OFF);
                WritableMap payload = Arguments.createMap();
                switch (state) {
                    case NfcAdapter.STATE_OFF:
                        disableReaderMode();
                        payload.putBoolean("status", false);
                        sendEvent(reactContext, "listenNFCStatus", payload);
                        break;
                    case NfcAdapter.STATE_TURNING_OFF:
                        break;
                    case NfcAdapter.STATE_ON:
                        enableReaderMode();
                        payload.putBoolean("status", true);
                        sendEvent(reactContext, "listenNFCStatus", payload);
                        break;
                    case NfcAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    };

    @Override
    public void onHostResume() {
        enableReaderMode();
    }

    @Override
    public void onHostPause() {
        disableReaderMode();
    }

    @Override
    public void onHostDestroy() {
        this.reactContext.unregisterReceiver(mReceiver);
    }
}