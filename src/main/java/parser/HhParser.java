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
 * Парсер, использующий стандартный java.net.http.HttpClient
 * для обращения к JSON-API hh.ru.
 */
public class HhParser {

    private static final String API =
            "https://api.hh.ru/vacancies?text=%s&page=%d&per_page=20";
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient  CLIENT  = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * @param keyword ключевые слова поиска
     * @param pages   сколько страниц (0…pages-1)
     */
    public List<Vacancy> fetchVacancies(String keyword, int pages) {
        List<Vacancy> list = new ArrayList<>();

        try {
            for (int page = 0; page < pages; page++) {
                String url = String.format(
                        API,
                        URLEncoder.encode(keyword, StandardCharsets.UTF_8),
                        page);

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(10))
                        .header("User-Agent", USER_AGENT)
                        .GET()
                        .build();

                HttpResponse<byte[]> resp =
                        CLIENT.send(req, HttpResponse.BodyHandlers.ofByteArray());

                JsonNode root = MAPPER.readTree(resp.body());
                for (JsonNode item : root.get("items")) {
                    list.add(toVacancy(item));
                }

                Thread.sleep(500); // не бомбим API
            }
        } catch (Exception e) {
            System.err.println("Ошибка API hh.ru: " + e.getMessage());
        }
        return list;
    }

    /** Преобразуем JSON одной вакансии в нашу модель. */
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
        JsonNode roles = n.get("professional_roles");
        if (roles != null && roles.size() > 0)
            v.setCategory(roles.get(0).get("name").asText());

        v.setActive(true);
        return v;
    }
}
