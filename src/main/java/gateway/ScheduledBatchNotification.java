package gateway;

import java.util.ArrayList;
import java.util.List;

public class ScheduledBatchNotification extends Notification {
    private final List<String> recipients = new ArrayList<>();

    public ScheduledBatchNotification(MessageSender sender) { super(sender); }

    public void addRecipient(String recipient) { recipients.add(recipient); }

    @Override
    public void send(String recipient, String message) { sendBatch(message); }

    public void sendBatch(String message) {
        int successful = 0;
        for (String recipient : recipients) {
            if (sender.sendMessage(recipient, "Batch", message)) {
                successful++;
            }
        }
        System.out.println("Total: " + recipients.size());
        System.out.println("Successful: " + successful);
        System.out.println("Failed: " + (recipients.size() - successful));
    }
}
