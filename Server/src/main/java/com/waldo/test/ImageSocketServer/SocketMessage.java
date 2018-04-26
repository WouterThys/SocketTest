package com.waldo.test.ImageSocketServer;

public class SocketMessage {

    private SocketCommand command;
    private String message;

    public SocketMessage(SocketCommand command, String message) {
        this.command = command;
        this.message = message;
    }

    @Override
    public String toString() {
        return getCommand().getIntValue() + ";" + getMessage();
    }

    public SocketCommand getCommand() {
        if (command == null) {
            command = SocketCommand.Invalid;
        }
        return command;
    }

    public String getMessage() {
        if (message == null) {
            message = "";
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static SocketMessage convert(String input) {
        SocketMessage message = new SocketMessage(SocketCommand.Invalid, "");
        try {
            String[] split = input.split(";");

            int comm = Integer.valueOf(split[0]);
            String mes = split[1];

            message.command = SocketCommand.fromInt(comm);
            message.message = mes;

        } catch (Exception e) {
            message.command = SocketCommand.Invalid;
        }
        return message;
    }
}
