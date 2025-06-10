package export;

import model.Vacancy;
import org.junit.jupiter.api.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class JsonExporterTest {

    private static final String FILE = "test.json";
    private JsonExporter exporter;
    private List<Vacancy> data;

    @BeforeEach
    void setUp() {
        exporter = new JsonExporter();
        data = List.of(makeVacancy());
    }

    @AfterEach
    void clean() { new File(FILE).delete(); }

    @Test
    void exportCreatesValidJson() throws Exception {
        exporter.export(data, FILE);

        String json = new String(java.nio.file.Files.readAllBytes(new File(FILE).toPath()))
                .replaceAll("\\s+","");          // убираем все пробелы/CR/LF

        assertTrue(json.startsWith("[") && json.endsWith("]"));
        assertTrue(json.contains("\"hhId\":\"123\""));
        assertTrue(json.contains("\"title\":\"JavaDeveloper\""));
        assertTrue(json.contains("\"company\":\"TestCorp\""));
        assertTrue(json.contains("\"city\":\"Moscow\""));
        assertTrue(json.contains("\"salaryMin\":100000"));
        assertTrue(json.contains("\"salaryMax\":150000"));
    }

    private Vacancy makeVacancy() {
        Vacancy v = new Vacancy();
        v.setHhId("123");
        v.setTitle("Java Developer");
        v.setCompany("TestCorp");
        v.setCity("Moscow");
        v.setSalaryMin(100_000);
        v.setSalaryMax(150_000);
        v.setPublishedDate(LocalDate.of(2025,5,27));
        v.setUrl("http://example.com");
        return v;
    }
}
