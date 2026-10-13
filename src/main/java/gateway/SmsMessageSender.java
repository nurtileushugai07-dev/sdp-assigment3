package gateway;

public class SmsMessageSender implements MessageSender {
    public static final int MAX_LENGTH = 160;
    private final boolean available;

    public SmsMessageSender() { this(true); }
    public SmsMessageSender(boolean available) { this.available = available; }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable() || recipient == null || recipient.isBlank() || body == null) {
            return false;
        }
        if (body.length() > MAX_LENGTH) {
            System.out.println("SMS rejected: maximum " + MAX_LENGTH + " characters");
            return false;
        }
        System.out.println("SMS -> " + recipient + ": " + body);
        return true;
    }

    @Override
    public boolean isAvailable() { return available; }

    @Override
    public String getChannelName() { return "SMS"; }
}
