package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Vacancy;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Парсинг через API hh.ru:
 *  1) /vacancies?text=… – получаем список id.
 *  2) /vacancies/{id}   – берём подробную карточку,
 *     чтобы заполнить category и description.
 */
public class HhParser {

    private static final String LIST_API =
            "https://api.hh.ru/vacancies?text=%s&page=%d&per_page=20";
    private static final String CARD_API =
            "https://api.hh.ru/vacancies/%s";
    private static final String UA =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * @param keyword ключевые слова поиска
     * @param pages   сколько страниц (0..pages-1)
     */
    public List<Vacancy> fetchVacancies(String keyword, int pages) {
        List<Vacancy> list = new ArrayList<>();

        try {
            for (int page = 0; page < pages; page++) {
                String url = String.format(
                        LIST_API,
                        URLEncoder.encode(keyword, StandardCharsets.UTF_8),
                        page);

                JsonNode root = sendJson(url);

                for (JsonNode item : root.get("items")) {
                    Vacancy v = toVacancy(item);

                    /* --- подробная карточка --- */
                    JsonNode card = sendJson(String.format(CARD_API, v.getHhId()));

                    // category / professional_roles
                    JsonNode roles = card.get("professional_roles");
                    if (roles != null && roles.size() > 0) {
                        v.setCategory(roles.get(0).get("name").asText());
                    }

                    // description пригодится для поиска
                    JsonNode desc = card.get("description");
                    if (desc != null && !desc.isNull()) {
                        // убираем html-теги
                        v.setDescription(desc.asText().replaceAll("<[^>]+>", ""));
                    }

                    list.add(v);
                    Thread.sleep(200);          // пауза между карточками
                }
                Thread.sleep(500);              // пауза между страницами
            }
        } catch (Exception e) {
            System.err.println("API hh.ru: " + e.getMessage());
        }
        return list;
    }

    /* --------------------------------------------------------------------- */

    private JsonNode sendJson(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("User-Agent", UA)
                .GET()
                .build();
        HttpResponse<byte[]> rsp =
                CLIENT.send(req, HttpResponse.BodyHandlers.ofByteArray());
        return MAPPER.readTree(rsp.body());
    }

    /** mapping «короткой» записи из /vacancies?text… */
    private Vacancy toVacancy(JsonNode n) {
        Vacancy v = new Vacancy();
        v.setHhId(n.get("id").asText());
        v.setTitle(n.get("name").asText());
        v.setCompany(n.get("employer").get("name").asText());
        v.setCity(n.get("area").get("name").asText());
        v.setUrl(n.get("alternate_url").asText());

        JsonNode sal = n.get("salary");
        if (sal != null && !sal.isNull()) {
            if (!sal.get("from").isNull())
                v.setSalaryMin(sal.get("from").asInt());
            if (!sal.get("to").isNull())
                v.setSalaryMax(sal.get("to").asInt());
        }
        v.setActive(true);
        return v;
    }
}
