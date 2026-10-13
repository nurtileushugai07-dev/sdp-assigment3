package gateway;

import java.util.Objects;

public abstract class Notification {
    // Bridge
    protected MessageSender sender;

    protected Notification(MessageSender sender) {
        this.sender = Objects.requireNonNull(sender);
    }

    public void setSender(MessageSender sender) {
        this.sender = Objects.requireNonNull(sender);
    }

    public abstract void send(String recipient, String message);
}
