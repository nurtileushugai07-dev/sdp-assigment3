package gateway;

public class TelegramMessageSender implements MessageSender {
    private final boolean available;

    public TelegramMessageSender() { this(true); }
    public TelegramMessageSender(boolean available) { this.available = available; }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable() || recipient == null || recipient.isBlank() || body == null) {
            return false;
        }
        System.out.println("Telegram -> " + recipient + ": **" + title + "** " + body);
        return true;
    }

    @Override
    public boolean isAvailable() { return available; }

    @Override
    public String getChannelName() { return "Telegram"; }
}
