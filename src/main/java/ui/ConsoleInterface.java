package ui;

import export.CsvExporter;
import export.JsonExporter;
import export.HtmlExporter;
import model.Vacancy;
import service.VacancyService;
import service.AnalyticsService;
import service.AnalyticsService.SalaryStats;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Консольный интерфейс для взаимодействия с пользователем.
 * Поддерживает команды: help, update, list, search, export, stats, top, exit.
 */
public class ConsoleInterface {
    private final VacancyService service;
    private final AnalyticsService analytics;
    private final Scanner scanner;

    public ConsoleInterface() {
        this.service = new VacancyService();
        this.analytics = new AnalyticsService();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Добро пожаловать в JobParser!");
        printHelp();

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(" ", 2);
            String cmd = parts[0].toLowerCase();
            String arg = parts.length > 1 ? parts[1] : "";

            switch (cmd) {
                case "help": printHelp(); break;
                case "update":
                    String keyword = arg.isEmpty() ? "" : arg;
                    System.out.println("Обновляем вакансии по ключевому слову: " + keyword);
                    service.updateVacancies(keyword, 1);
                    break;
                case "list": printVacancies(service.getAllVacancies()); break;
                case "search":
                    if (arg.isEmpty()) System.out.println("Укажите слово для поиска. Пример: search Java");
                    else printVacancies(service.searchByKeyword(arg));
                    break;
                case "export":
                    handleExport(arg);
                    break;
                case "stats":
                    handleStats(arg);
                    break;
                case "top":
                    handleTop(arg);
                    break;
                case "exit":
                    System.out.println("Выход. До свидания!"); scanner.close(); return;
                default:
                    System.out.println("Неизвестная команда: " + cmd + ". Введите help для списка команд.");
            }
        }
    }

    private void handleExport(String arg) {
        String[] ex = arg.split("\\s+", 2);
        if (ex.length < 2) {
            System.out.println("Использование: export <csv|json|html> <filename>");
            return;
        }
        String fmt = ex[0].toLowerCase(), filename = ex[1];
        List<Vacancy> toExport = service.getAllVacancies();
        switch (fmt) {
            case "csv": new CsvExporter().export(toExport, filename); break;
            case "json": new JsonExporter().export(toExport, filename); break;
            case "html": new HtmlExporter().export(toExport, filename); break;
            default: System.out.println("Неподдерживаемый формат: " + fmt);
        }
    }

    private void handleStats(String arg) {
        switch (arg.trim().toLowerCase()) {
            case "categories":
                Map<String, Integer> byCat = analytics.countByCategory();
                byCat.forEach((cat, count) -> System.out.println(cat + ": " + count));
                break;
            case "salary_by_city":
                Map<String, SalaryStats> stats = analytics.salaryStatsByCity();
                stats.forEach((city, s) -> System.out.println(
                        city + " -> count=" + s.getCount()
                                + ", avg=" + String.format("%.0f", s.getAverage())
                                + ", max=" + s.getMax() ));
                break;
            default:
                System.out.println("Использование: stats <categories|salary_by_city>");
        }
    }

    private void handleTop(String arg) {
        try {
            int n = Integer.parseInt(arg.trim());
            List<Vacancy> top = analytics.topSalaryVacancies(n);
            printVacancies(top);
        } catch (NumberFormatException e) {
            System.out.println("Использование: top <N> (число вакансий)");
        }
    }

    private void printVacancies(List<Vacancy> list) {
        if (list.isEmpty()) { System.out.println("Вакансии не найдены."); return; }
        System.out.println("Найдено вакансий: " + list.size());
        System.out.println(String.format("%-5s %-30s %-20s %-20s %-10s",
                "ID","Должность","Компания","Город","Зарплата"));
        for (Vacancy v : list) {
            String salary = (v.getSalaryMin()!=null&&v.getSalaryMax()!=null)
                    ? v.getSalaryMin()+"-"+v.getSalaryMax() : "";
            System.out.println(String.format("%-5d %-30.30s %-20.20s %-20.20s %-10s",
                    v.getId(),v.getTitle(),v.getCompany(),v.getCity(),salary));
        }
    }

    private void printHelp() {
        System.out.println("Доступные команды:");
        System.out.println("  help                        - Справка");
        System.out.println("  update [keyword]            - Обновить вакансии");
        System.out.println("  list                        - Показать все вакансии");
        System.out.println("  search <keyword>            - Поиск вакансий");
        System.out.println("  export <fmt> <filename>     - Экспорт (csv|json|html)");
        System.out.println("  stats <categories|salary_by_city>  - Статистика");
        System.out.println("  top <N>                     - Топ по зарплате");
        System.out.println("  exit                        - Выход");
    }
}
