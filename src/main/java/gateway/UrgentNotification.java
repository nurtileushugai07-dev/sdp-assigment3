package gateway;

public class UrgentNotification extends Notification {
    private final MessageSender fallback;

    public UrgentNotification(MessageSender sender) { this(sender, null); }

    public UrgentNotification(MessageSender sender, MessageSender fallback) {
        super(sender);
        this.fallback = fallback;
    }

    @Override
    public void send(String recipient, String message) {
        String body = "[URGENT] " + message;
        boolean success = sender.isAvailable()
                && sender.sendMessage(recipient, "Urgent", body);
        if (!success && fallback != null && fallback.isAvailable()) {
            System.out.println("Fallback -> " + fallback.getChannelName());
            success = fallback.sendMessage(recipient, "Urgent", body);
        }
        System.out.println(success ? "Urgent: sent" : "Urgent: failed");
    }
}
