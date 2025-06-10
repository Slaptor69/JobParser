package export;

import model.Vacancy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Класс для экспорта списка вакансий в JSON-файл.
 * Генерирует массив объектов, где каждый объект соответствует Vacancy.
 */
public class JsonExporter {
    // Формат даты для JSON
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Экспортирует список вакансий в JSON-файл по указанному пути.
     * @param vacancies список вакансий для экспорта
     * @param filePath  путь к создаваемому JSON-файлу
     */
    public void export(List<Vacancy> vacancies, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("[");
            writer.newLine();

            for (int i = 0; i < vacancies.size(); i++) {
                Vacancy v = vacancies.get(i);
                writer.write("  {");
                writer.newLine();

                writeJsonField(writer, "hhId", v.getHhId(), true);
                writeJsonField(writer, "title", v.getTitle(), true);
                writeJsonField(writer, "company", v.getCompany(), true);
                writeJsonField(writer, "city", v.getCity(), true);

                // salaryMin and salaryMax
                writer.write("    \"salaryMin\": " + (v.getSalaryMin() != null ? v.getSalaryMin() : "null") + ",");
                writer.newLine();
                writer.write("    \"salaryMax\": " + (v.getSalaryMax() != null ? v.getSalaryMax() : "null") + ",");
                writer.newLine();

                // publishedDate
                String date = v.getPublishedDate() != null
                        ? DATE_FORMAT.format(v.getPublishedDate()) : null;
                writer.write("    \"publishedDate\": " + (date != null ? ("\"" + date + "\"") : "null") + ",");
                writer.newLine();

                writeJsonField(writer, "url", v.getUrl(), false);

                writer.write("  }" + (i < vacancies.size() - 1 ? "," : ""));
                writer.newLine();
            }
            writer.write("]");
            writer.newLine();

            System.out.println("Экспорт в JSON выполнен: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при экспорте в JSON: " + e.getMessage());
        }
    }

    /**
     * Пишет строку поля в формате JSON с отступами и экранированием.
     * @param writer   поток для записи
     * @param key      имя поля
     * @param value    значение поля (строка)
     * @param comma    добавить ли запятую после значения
     * @throws IOException
     */
    private void writeJsonField(BufferedWriter writer, String key, String value, boolean comma) throws IOException {
        String val = value != null ? escape(value) : "";
        writer.write("    \"" + key + "\": \"" + val + "\"" + (comma ? "," : ""));
        writer.newLine();
    }

    /**
     * Экранирует специальные символы в строке для JSON.
     */
    private String escape(String s) {
        return s.replace("\\", "\\\\")    // обратный слеш
                .replace("\"", "\\\"") // двойная кавычка
                .replace("\n", "\\n");     // перевод строки
    }
}
