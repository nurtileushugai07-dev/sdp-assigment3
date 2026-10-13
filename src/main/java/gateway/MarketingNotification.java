package gateway;

public class MarketingNotification extends Notification {
    public MarketingNotification(MessageSender sender) { super(sender); }

    @Override
    public void send(String recipient, String message) {
        boolean success = sender.sendMessage(recipient, "Special offer", "[PROMO] " + message);
        System.out.println(success ? "Marketing: sent" : "Marketing: failed");
    }
}
