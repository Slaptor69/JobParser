package export;

import model.Vacancy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Класс для экспорта списка вакансий в CSV-файл.
 * Каждая вакансия записывается в отдельную строку, поля разделяются запятой.
 */
public class CsvExporter {
    /**
     * Экспортирует список вакансий в CSV-файл по указанному пути.
     * @param vacancies список вакансий для экспорта
     * @param filePath   путь к создаваемому CSV-файлу
     */
    public void export(List<Vacancy> vacancies, String filePath) {
        // Заголовок CSV-файла
        String header = "hhId,title,company,city,salaryMin,salaryMax,publishedDate,url";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(header);
            writer.newLine();

            for (Vacancy v : vacancies) {
                StringBuilder sb = new StringBuilder();
                // Экранируем запятые в текстовых полях
                sb.append(escape(v.getHhId())).append(',')
                        .append(escape(v.getTitle())).append(',')
                        .append(escape(v.getCompany())).append(',')
                        .append(escape(v.getCity())).append(',')
                        .append(v.getSalaryMin() != null ? v.getSalaryMin() : "").append(',')
                        .append(v.getSalaryMax() != null ? v.getSalaryMax() : "").append(',')
                        .append(v.getPublishedDate() != null ? v.getPublishedDate() : "").append(',')
                        .append(escape(v.getUrl()));
                writer.write(sb.toString());
                writer.newLine();
            }
            System.out.println("Экспорт в CSV выполнен: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при экспорте в CSV: " + e.getMessage());
        }
    }

    /**
     * Экранирование символов, например, кавычек и запятых внутри значения.
     * @param value исходная строка
     * @return безопасная для CSV строка
     */
    private String escape(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\""); // двойные кавычки → ""
        if (escaped.contains(",") || escaped.contains("\"")) {
            // Оборачиваем в кавычки, если есть запятые или кавычки
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
