package com.waldo.test.ImageSocketServer;

public class SocketMessage {

    public interface OnResponseListener {
        void onResponse(SocketMessage message);
    }

    private SocketCommand command;
    private String message;
    private OnResponseListener responseListener;

    public SocketMessage(SocketCommand command, String message) {
        this.command = command;
        this.message = message;
    }

    public SocketMessage(SocketCommand command, String message, OnResponseListener responseListener) {
        this(command, message);
        this.responseListener = responseListener;
    }

    @Override
    public String toString() {
        return getCommand().getIntValue() + ";" + getMessage();
    }

    public boolean isValid() {
        return !getCommand().equals(SocketCommand.Invalid);
    }

    public void response(SocketMessage response) {
        if (responseListener != null) {
            responseListener.onResponse(response);
        }
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
