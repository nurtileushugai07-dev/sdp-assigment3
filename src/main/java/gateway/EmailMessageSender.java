package gateway;

public class EmailMessageSender implements MessageSender {
    private final boolean available;

    public EmailMessageSender() { this(true); }
    public EmailMessageSender(boolean available) { this.available = available; }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable() || recipient == null || recipient.isBlank() || body == null) {
            return false;
        }
        System.out.println("Email -> " + recipient + " | Subject: " + title + "\n" + body + "\n-- Notification Gateway");
        return true;
    }

    @Override
    public boolean isAvailable() { return available; }

    @Override
    public String getChannelName() { return "Email"; }
}
