package gateway;

public class PushNotificationSender implements MessageSender {
    private final boolean available;

    public PushNotificationSender() { this(true); }
    public PushNotificationSender(boolean available) { this.available = available; }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable() || recipient == null || recipient.isBlank() || body == null) {
            return false;
        }
        System.out.println("Push -> device=" + recipient + " | [" + title + "] " + body);
        return true;
    }

    @Override
    public boolean isAvailable() { return available; }

    @Override
    public String getChannelName() { return "Push"; }
}
