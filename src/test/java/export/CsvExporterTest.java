package export;

import model.Vacancy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юнит-тест для CsvExporter.
 */
public class CsvExporterTest {
    private static final String TEST_FILE = "test_vacancies.csv";

    private CsvExporter exporter;
    private List<Vacancy> vacancies;

    @BeforeEach
    public void setUp() {
        exporter = new CsvExporter();
        vacancies = new ArrayList<>();
        Vacancy v = new Vacancy();
        v.setHhId("123");
        v.setTitle("Java Developer");
        v.setCompany("TestCorp");
        v.setCity("Moscow");
        v.setSalaryMin(100000);
        v.setSalaryMax(150000);
        v.setPublishedDate(LocalDate.of(2025, 5, 27));
        v.setUrl("http://example.com");
        vacancies.add(v);
    }

    @AfterEach
    public void tearDown() {
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testExportCreatesFileWithCorrectContent() throws Exception {
        // Выполняем экспорт
        exporter.export(vacancies, TEST_FILE);

        // Проверяем, что файл создан
        File file = new File(TEST_FILE);
        assertTrue(file.exists(), "CSV файл не был создан");

        // Считываем первую строку (заголовок) и вторую (данные)
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            assertEquals("hhId,title,company,city,salaryMin,salaryMax,publishedDate,url", header,
                    "Заголовок CSV не соответствует ожидаемому");

            String line = reader.readLine();
            assertNotNull(line, "В файле нет строки с данными");
            // Проверяем, что строка содержит поля
            assertTrue(line.contains("123"));
            assertTrue(line.contains("Java Developer"));
            assertTrue(line.contains("TestCorp"));
            assertTrue(line.contains("Moscow"));
            assertTrue(line.contains("100000"));
            assertTrue(line.contains("150000"));
            assertTrue(line.contains("2025-05-27"));
            assertTrue(line.contains("http://example.com"));
        }
    }
}
