package com.waldo.test.ImageSocketServer;

public enum SocketCommand {
    Invalid(0, "Invalid"),
    ConnectClient(1, "Connect client"),
    DisconnectClient(2, "Disconnect client"),

    SendImage(3, "Send image"),
    GetImage(4, "Get image");

    private final int intValue;
    private final String description;

    SocketCommand(int intValue, String description) {
        this.intValue = intValue;
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public int getIntValue() {
        return intValue;
    }

    public static SocketCommand fromInt(int intValue) {
        switch (intValue) {
            default:
                return Invalid;
            case 1:
                return ConnectClient;
            case 2:
                return DisconnectClient;
            case 3:
                return SendImage;
            case 4:
                return GetImage;
        }
    }
}
