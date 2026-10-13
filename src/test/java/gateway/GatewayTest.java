package gateway;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

class GatewayTest {
    // A test
    static class RecordingSender implements MessageSender {
        boolean available = true;
        boolean result = true;
        int calls;
        String body;
        String title;
        public boolean sendMessage(String recipient, String title, String body) {
            calls++;
            this.title = title;
            this.body = body;
            return result;
        }
        public boolean isAvailable() { return available; }
        public String getChannelName() { return "Test"; }
    }

    private String output(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream capture = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            action.run();
        } finally {
            System.setOut(original);
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    @Test void telegramDeliversFormattedMessage() {
        String text = output(() -> assertTrue(new TelegramMessageSender().sendMessage("u", "Title", "Hello")));
        assertTrue(text.contains("**Title** Hello"));
    }

    @Test void unavailableProviderRejectsDelivery() {
        MessageSender sender = new TelegramMessageSender(false);
        assertFalse(sender.isAvailable());
        assertFalse(sender.sendMessage("u", "t", "Hello"));
    }

    @Test void urgentUsesFallbackWhenPrimaryUnavailable() {
        RecordingSender primary = new RecordingSender();
        primary.available = false;
        RecordingSender fallback = new RecordingSender();
        new UrgentNotification(primary, fallback).send("u", "Help");
        assertEquals(0, primary.calls);
        assertEquals(1, fallback.calls);
        assertEquals("[URGENT] Help", fallback.body);
    }

    @Test void smsAccepts160ButRejects161Characters() {
        SmsMessageSender sms = new SmsMessageSender();
        assertTrue(sms.sendMessage("u", "t", "a".repeat(160)));
        assertFalse(sms.sendMessage("u", "t", "a".repeat(161)));
    }

    @Test void encryptedNotificationTransformsUtf8Message() {
        RecordingSender sender = new RecordingSender();
        new EncryptedNotification(sender).send("u", "Құпия");
        assertNotEquals("Құпия", sender.body);
        assertEquals("Құпия", new String(Base64.getDecoder().decode(sender.body), StandardCharsets.UTF_8));
        assertEquals(1, sender.calls);
    }

    @Test void batchCountsSuccessAndFailure() {
        ScheduledBatchNotification batch = new ScheduledBatchNotification(new SmsMessageSender());
        batch.addRecipient("u1");
        batch.addRecipient("u2");
        batch.addRecipient("");
        String text = output(() -> batch.send(null, "Hello"));
        assertTrue(text.contains("SMS -> u1: Hello"));
        assertTrue(text.contains("SMS -> u2: Hello"));
        assertTrue(text.contains("Total: 3"));
        assertTrue(text.contains("Successful: 2"));
        assertTrue(text.contains("Failed: 1"));
    }

    @Test void sameNotificationSwitchesSenderAtRuntime() {
        RecordingSender first = new RecordingSender();
        RecordingSender second = new RecordingSender();
        Notification notification = new UrgentNotification(first);
        notification.send("u", "First");
        notification.setSender(second);
        notification.send("u", "Second");
        assertEquals(1, first.calls);
        assertEquals(1, second.calls);
        assertEquals("[URGENT] Second", second.body);
    }

    @Test void pushWorksWithExistingUrgentNotification() {
        String text = output(() -> new UrgentNotification(new PushNotificationSender()).send("device1", "Help"));
        assertTrue(text.contains("Push -> device=device1"));
        assertTrue(text.contains("[URGENT] Help"));
        assertTrue(text.contains("Urgent: sent"));
    }

    @Test void marketingWorksWithExistingEmailSender() {
        String text = output(() -> new MarketingNotification(new EmailMessageSender()).send("u", "Sale"));
        assertTrue(text.contains("Subject: Special offer"));
        assertTrue(text.contains("[PROMO] Sale"));
        assertTrue(text.contains("-- Notification Gateway"));
    }

    @Test void urgentReportsFailureWhenBothProvidersUnavailable() {
        String text = output(() -> new UrgentNotification(new TelegramMessageSender(false),
                new SmsMessageSender(false)).send("u", "Help"));
        assertTrue(text.contains("Urgent: failed"));
        assertFalse(text.contains("Urgent: sent"));
    }

    @Test void successfulPrimaryDoesNotUseFallback() {
        RecordingSender primary = new RecordingSender();
        RecordingSender fallback = new RecordingSender();
        new UrgentNotification(primary, fallback).send("u", "Help");
        assertEquals(1, primary.calls);
        assertEquals(0, fallback.calls);
        assertEquals("[URGENT] Help", primary.body);
    }

    @Test void kafkaPublishesSimulatedEvent() {
        String text = output(() -> assertTrue(new KafkaEventSender().sendMessage("alerts", "Created", "42")));
        assertTrue(text.contains("topic=alerts | key=Created | value=42"));
    }
}
