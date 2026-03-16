package library;

import java.net.URI;
import java.net.http.*;
import java.util.List;
import java.util.stream.Collectors;

public class AIRecommender {

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private final HttpClient httpClient  = HttpClient.newHttpClient();
    private String apiKey;

    public AIRecommender(String apiKey) { this.apiKey = apiKey; }
    public void    setApiKey(String k)  { this.apiKey = k; }
    public boolean hasApiKey()          { return apiKey != null && !apiKey.isBlank(); }

    public String getRecommendations(Member member, List<LibraryItem> catalog) {
        if (!hasApiKey()) return "Please set your Claude API key in Settings.";

        String borrowed = member.getBorrowedItems().isEmpty()
            ? "No borrowing history yet."
            : member.getBorrowedItems().stream()
                .map(i -> i.getTitle() + " (" + i.getItemType() + ")")
                .collect(Collectors.joining(", "));

        String available = catalog.stream()
            .filter(LibraryItem::isAvailable)
            .map(i -> i.getTitle() + " [" + i.getItemType() + "]")
            .collect(Collectors.joining(", "));

        String prompt = "You are a library assistant. Member name: " + member.getName()
            + ". Their borrowing history: " + borrowed
            + ". Available items: " + available
            + ". Recommend 2-3 items they might enjoy with a brief reason. Be friendly and concise.";

        return callAPI(prompt);
    }

    public String askQuestion(String question, List<LibraryItem> catalog) {
        if (!hasApiKey()) return "Please set your Claude API key in Settings.";

        String catalogList = catalog.stream()
            .map(i -> i.getTitle() + " [" + i.getItemType() + "]")
            .collect(Collectors.joining(", "));

        String prompt = "You are a helpful library assistant. "
            + "The library catalog contains: " + catalogList + ". "
            + "Answer this question concisely in 2-3 sentences: " + question;

        return callAPI(prompt);
    }

    private String callAPI(String prompt) {
        try {
            String escapedPrompt = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\t", " ");

            String body = "{"
                + "\"model\":\"claude-haiku-4-5-20251001\","
                + "\"max_tokens\":512,"
                + "\"messages\":["
                +   "{"
                +     "\"role\":\"user\","
                +     "\"content\":\"" + escapedPrompt + "\""
                +   "}"
                + "]"
                + "}";

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type",      "application/json")
                .header("x-api-key",         apiKey)
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> response = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractText(response.body());
            } else if (response.statusCode() == 401) {
                return "Invalid API key. Go to Settings and check your key.";
            } else if (response.statusCode() == 400) {
                return "Bad request (400). Details: " + response.body();
            } else {
                return "AI error (" + response.statusCode() + "): " + response.body();
            }

        } catch (Exception e) {
            return "Connection error: " + e.getMessage();
        }
    }

    private String extractText(String json) {
        try {
            String marker = "\"text\":\"";
            int start = json.indexOf(marker);
            if (start == -1) return "No response text found.";
            start += marker.length();

            int end = start;
            while (end < json.length()) {
                if (json.charAt(end) == '"' && json.charAt(end - 1) != '\\') break;
                end++;
            }

            return json.substring(start, end)
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");

        } catch (Exception e) {
            return "Could not parse response: " + e.getMessage();
        }
    }
}
