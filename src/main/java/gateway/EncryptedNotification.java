package gateway;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptedNotification extends Notification {
    public EncryptedNotification(MessageSender sender) { super(sender); }

    @Override
    public void send(String recipient, String message) {
        // Assignment simulation only
        String encoded = Base64.getEncoder().encodeToString(message.getBytes(StandardCharsets.UTF_8));
        boolean success = sender.sendMessage(recipient, "Encoded message", encoded);
        System.out.println(success ? "Encoded: sent" : "Encoded: failed");
    }
}
