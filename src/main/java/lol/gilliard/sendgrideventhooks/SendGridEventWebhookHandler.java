package lol.gilliard.sendgrideventhooks;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Controller
public class SendGridEventWebhookHandler {

    private static final Logger logger = Logger.getLogger(SendGridEventWebhookHandler.class.getName());

    private final Map<String, Set<String>> openedEmails = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> clickedLinks = new ConcurrentHashMap<>();

    @PostMapping("/events")
    @ResponseBody
    public String receiveSGEventHook(@RequestBody List<SendGridEvent> events) {

        logger.info(String.format("Received %d events", events.size()));

        events.forEach(event -> {
            switch (event.eventType) {
                case "open":
                    openedEmails.computeIfAbsent(event.sgMessageId, k -> new HashSet<>()).add(event.email);
                    break;

                case "click":
                    clickedLinks.computeIfAbsent(event.url, k -> new HashSet<>()).add(event.email);
                    break;
            }
        });

        return "ok";
    }

    @GetMapping("/opened")
    @ResponseBody
    public Map<String, Set<String>> getOpenedEmailData() {
        return openedEmails;
    }

    @GetMapping("/clicked")
    @ResponseBody
    public Map<String, Set<String>> getClickedLinksData() {
        return clickedLinks;
    }

}
