package export;

import model.Vacancy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юнит-тест для HtmlExporter.
 */
public class HtmlExporterTest {
    private static final String TEST_FILE = "test_vacancies.html";
    private HtmlExporter exporter;
    private List<Vacancy> vacancies;

    @BeforeEach
    public void setUp() {
        exporter = new HtmlExporter();
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
    public void tearDown() throws Exception {
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }

    @Test
    public void testExportCreatesHtmlWithTableAndData() throws Exception {
        // Выполняем экспорт
        exporter.export(vacancies, TEST_FILE);

        // Проверяем, что файл создан
        File file = new File(TEST_FILE);
        assertTrue(file.exists(), "HTML файл не был создан");

        // Считываем содержимое файла
        String content = new String(Files.readAllBytes(Paths.get(TEST_FILE)), "UTF-8");

        // Проверяем базовую структуру HTML
        assertTrue(content.contains("<!DOCTYPE html>"), "Должен быть DOCTYPE");
        assertTrue(content.contains("<table>"), "Должна быть таблица вакансий");

        // Проверяем, что в таблице присутствуют данные вакансии
        assertTrue(content.contains("<td>123</td>"), "Должен содержаться hhId");
        assertTrue(content.contains("<td>Java Developer</td>"), "Должен содержаться заголовок");
        assertTrue(content.contains("<td>TestCorp</td>"), "Должен содержаться компания");
        assertTrue(content.contains("<td>Moscow</td>"), "Должен содержаться город");
        assertTrue(content.contains("100000-150000"), "Должна содержаться информация о зарплате");
        assertTrue(content.contains("<a href=\"http://example.com\""), "Должна быть ссылка на вакансию");
    }
}
