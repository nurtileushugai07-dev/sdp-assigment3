package gateway;

public class Main {
    public static void main(String[] args) {
        MessageSender telegram = new TelegramMessageSender();
        MessageSender email = new EmailMessageSender();
        MessageSender sms = new SmsMessageSender();

        System.out.println("1. Urgent + Telegram");
        Notification urgent = new UrgentNotification(telegram, sms);
        urgent.send("user1", "Server is down");

        System.out.println("\n2. Encrypted + Email");
        new EncryptedNotification(email).send("user@example.com", "Secret message");

        System.out.println("\n3. Batch + SMS");
        ScheduledBatchNotification batch = new ScheduledBatchNotification(sms);
        batch.addRecipient("+77000000001");
        batch.addRecipient("+77000000002");
        batch.addRecipient(""); // Simulated failure: invalid recipient.
        batch.sendBatch("Class starts at 10:00");

        System.out.println("\n4. Same urgent object: Telegram -> Email");
        urgent.setSender(email);
        urgent.send("user@example.com", "Server is back online");

        System.out.println("\n5. Telegram unavailable -> SMS fallback");
        new UrgentNotification(new TelegramMessageSender(false), sms)
                .send("+77000000001", "Check the server");

        System.out.println("\n6. Marketing + Push");
        new MarketingNotification(new PushNotificationSender()).send("device1", "20% discount today");

        System.out.println("\nAdditional provider: Kafka");
        new UrgentNotification(new KafkaEventSender()).send("alerts", "New event");
    }
}
