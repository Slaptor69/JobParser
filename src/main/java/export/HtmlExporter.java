package export;

import model.Vacancy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Класс для экспорта списка вакансий в HTML-файл.
 * Генерирует страницу с таблицей вакансий.
 */
public class HtmlExporter {

    /**
     * Экспортирует список вакансий в HTML-файл по указанному пути.
     * @param vacancies список вакансий для экспорта
     * @param filePath  путь к создаваемому HTML-файлу
     */
    public void export(List<Vacancy> vacancies, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("<!DOCTYPE html>"); writer.newLine();
            writer.write("<html lang=\"ru\">"); writer.newLine();
            writer.write("<head><meta charset=\"UTF-8\"><title>Вакансии</title>"); writer.newLine();
            writer.write("<style>table { border-collapse: collapse; width: 100%; } "); writer.newLine();
            writer.write("th, td { border: 1px solid #ccc; padding: 8px; text-align: left; } "); writer.newLine();
            writer.write("th { background-color: #f2f2f2; } </style>"); writer.newLine();
            writer.write("</head><body>"); writer.newLine();
            writer.write("<h1>Список вакансий</h1>"); writer.newLine();
            writer.write("<table>"); writer.newLine();
            // Заголовок таблицы
            writer.write("<tr>"); writer.newLine();
            writer.write(matrixHeaders()); writer.newLine();
            writer.write("</tr>"); writer.newLine();

            // Строки таблицы
            for (Vacancy v : vacancies) {
                writer.write("<tr>"); writer.newLine();
                writer.write(cell(v.getHhId())); writer.newLine();
                writer.write(cell(v.getTitle())); writer.newLine();
                writer.write(cell(v.getCompany())); writer.newLine();
                writer.write(cell(v.getCity())); writer.newLine();
                String salary = "";
                if (v.getSalaryMin() != null && v.getSalaryMax() != null) {
                    salary = v.getSalaryMin() + "-" + v.getSalaryMax();
                }
                writer.write(cell(salary)); writer.newLine();
                writer.write(cell(v.getPublishedDate() != null ? v.getPublishedDate().toString() : "")); writer.newLine();
                writer.write(linkCell(v.getUrl(), "Ссылка")); writer.newLine();
                writer.write("</tr>"); writer.newLine();
            }

            writer.write("</table>"); writer.newLine();
            writer.write("</body></html>"); writer.newLine();
            System.out.println("Экспорт в HTML выполнен: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при экспорте в HTML: " + e.getMessage());
        }
    }

    private String matrixHeaders() {
        String[] headers = {"hhId", "Должность", "Компания", "Город", "Зарплата", "Дата публикации", "Ссылка"};
        StringBuilder sb = new StringBuilder();
        for (String h : headers) {
            sb.append("<th>").append(escapeHtml(h)).append("</th>");
        }
        return sb.toString();
    }

    private String cell(String value) {
        return "<td>" + escapeHtml(value) + "</td>";
    }

    private String linkCell(String url, String text) {
        String safeUrl = escapeHtml(url);
        String safeText = escapeHtml(text);
        return "<td><a href=\"" + safeUrl + "\" target=\"_blank\">" + safeText + "</a></td>";
    }

    /**
     * Экранирует HTML-специальные символы
     */
    private String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
