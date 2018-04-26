package com.waldo.test.ImageSocketServer;

public enum SocketCommand {
    Invalid(0, "Invalid"),
    ConnectClient(1, "Connect client"),
    DisconnectClient(2, "Disconnect client");

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

    public String getDescription() {
        return description;
    }

    public static SocketCommand fromInt(int intValue) {
        switch (intValue) {
            default:
                return Invalid;
            case 1:
                return ConnectClient;
            case 2:
                return DisconnectClient;
        }
    }
}
