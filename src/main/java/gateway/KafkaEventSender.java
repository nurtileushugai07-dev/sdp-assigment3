package gateway;

public class KafkaEventSender implements MessageSender {
    private final boolean available;

    public KafkaEventSender() { this(true); }
    public KafkaEventSender(boolean available) { this.available = available; }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable() || recipient == null || recipient.isBlank() || body == null) {
            return false;
        }
        System.out.println("Kafka event -> topic=" + recipient + " | key=" + title + " | value=" + body);
        return true;
    }

    @Override
    public boolean isAvailable() { return available; }

    @Override
    public String getChannelName() { return "Kafka"; }
}
