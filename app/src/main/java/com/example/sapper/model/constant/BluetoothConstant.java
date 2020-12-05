package com.example.sapper.model.constant;

public class BluetoothConstant {
    // Debugging
    public static final String TAG = "myLogs";
    public static final int DIALOG_CHOICE = 1;
    private static final int DIALOG_MINER_LOSS = 2;
    private static final int DIALOG_MINER_WON = 3;
    private static final int DIALOG_BLUETOOTH_MENU = 4;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final  int BOARD_POST = 6;
    public static final int MESSAGE_SAPPER_LOSS = 7;
    public static final int MESSAGE_SAPPER_WIN = 8;

    public static final int SOCKET_SERVER = 4;
    public static final int SOCKET_CLIENT = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 2;
    public static final int REQUEST_ENABLE_BT = 3;
}
