package library;

import java.net.URI;
import java.net.http.*;
import java.util.List;
import java.util.stream.Collectors;

public class AIRecommender {

    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private String apiKey;

    public AIRecommender(String apiKey) { this.apiKey = apiKey; }
    public void    setApiKey(String k)  { this.apiKey = k; }
    public boolean hasApiKey()          { return apiKey != null && !apiKey.isBlank(); }

    public String getRecommendations(Member member, List<LibraryItem> catalog) {
        if (!hasApiKey()) return "Please set your API key in Settings.";

        String borrowed = member.getBorrowedItems().isEmpty()
            ? "No borrowing history yet."
            : member.getBorrowedItems().stream()
                .map(i -> i.getTitle() + " (" + i.getItemType() + ")")
                .collect(Collectors.joining(", "));

        String available = catalog.stream()
            .filter(LibraryItem::isAvailable)
            .map(i -> i.getTitle() + " [" + i.getItemType() + "]")
            .collect(Collectors.joining(", "));

        String prompt = "You are a library assistant. Member: " + member.getName()
            + ". Borrowing history: " + borrowed
            + ". Available items: " + available
            + ". Recommend 2 or 3 items they might enjoy. Be friendly and brief.";

        return callAPI(prompt);
    }

    public String askQuestion(String question, List<LibraryItem> catalog) {
        if (!hasApiKey()) return "Please set your API key in Settings.";

        String catalogList = catalog.stream()
            .map(i -> i.getTitle() + " [" + i.getItemType() + "]")
            .collect(Collectors.joining(", "));

        String prompt = "You are a helpful library assistant. "
            + "Library catalog: " + catalogList + ". "
            + "Answer briefly: " + question;

        return callAPI(prompt);
    }

    private String callAPI(String prompt) {
        try {
            // Build JSON manually — fully escaped
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"model\":\"google/gemma-3-4b-it:free\",");
            sb.append("\"messages\":[");
            sb.append("{");
            sb.append("\"role\":\"user\",");
            sb.append("\"content\":");
            sb.append(jsonString(prompt));
            sb.append("}");
            sb.append("]");
            sb.append("}");

            String body = sb.toString();

            System.out.println("DEBUG body: " + body); // remove after testing

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type",  "application/json")
                .header("Authorization", "Bearer " + apiKey.trim())
                .header("HTTP-Referer",  "http://localhost")
                .header("X-Title",       "LibrarySystem")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString());

            System.out.println("DEBUG status: " + response.statusCode());
            System.out.println("DEBUG body:   " + response.body());

            if (response.statusCode() == 200) {
                return extractText(response.body());
            } else if (response.statusCode() == 401) {
                return "Invalid API key. Go to Settings and re-enter your key.";
            } else if (response.statusCode() == 400) {
                return "Request error: " + response.body();
            } else {
                return "AI error (" + response.statusCode() + "): " + response.body();
            }

        } catch (Exception e) {
            return "Connection error: " + e.getMessage();
        }
    }

    // Properly escape a Java string into a JSON string value including quotes
    private String jsonString(String text) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"'  -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default   -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    private String extractText(String json) {
        try {
            // OpenRouter returns: "message":{"role":"assistant","content":"..."}
            String[] markers = {"\"content\":\"", "\"content\": \""};
            int start = -1;
            String foundMarker = "";
            for (String marker : markers) {
                int idx = json.indexOf(marker);
                if (idx != -1) { start = idx + marker.length(); foundMarker = marker; break; }
            }
            if (start == -1) return "No response found in: " + json.substring(0, Math.min(200, json.length()));

            int end = start;
            while (end < json.length()) {
                char c = json.charAt(end);
                if (c == '"' && json.charAt(end - 1) != '\\') break;
                end++;
            }

            return json.substring(start, end)
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\t", "\t");

        } catch (Exception e) {
            return "Parse error: " + e.getMessage() + " | Raw: " + json.substring(0, Math.min(300, json.length()));
        }
    }
}
