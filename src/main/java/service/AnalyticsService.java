package service;

import dao.VacancyRepository;
import model.Vacancy;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для аналитики вакансий: подсчёт по категориям,
 * статистика по городам и топ по зарплате.
 */
public class AnalyticsService {
    private final VacancyRepository repository;

    public AnalyticsService() {
        this.repository = new VacancyRepository();
    }

    /**
     * Подсчёт количества вакансий по категориям.
     * @return Map<категория, количество>
     */
    public Map<String, Integer> countByCategory() {
        List<Vacancy> all = repository.findAll();
        Map<String, Integer> result = new HashMap<>();
        for (Vacancy v : all) {
            String cat = v.getCategory() != null && !v.getCategory().isEmpty()
                    ? v.getCategory() : "(не указана)";
            result.put(cat, result.getOrDefault(cat, 0) + 1);
        }
        return result;
    }

    /**
     * Статистика зарплат по городам: количество, среднее и максимальное.
     * @return Map<город, SalaryStats>
     */
    public Map<String, SalaryStats> salaryStatsByCity() {
        List<Vacancy> all = repository.findAll();
        Map<String, List<Integer>> byCity = new HashMap<>();
        for (Vacancy v : all) {
            if (v.getSalaryMax() != null) {
                String city = v.getCity() != null && !v.getCity().isEmpty()
                        ? v.getCity() : "(не указан)";
                byCity.computeIfAbsent(city, k -> new ArrayList<>()).add(v.getSalaryMax());
            }
        }
        Map<String, SalaryStats> stats = new HashMap<>();
        for (Map.Entry<String, List<Integer>> entry : byCity.entrySet()) {
            List<Integer> list = entry.getValue();
            int count = list.size();
            double avg = list.stream().mapToInt(i -> i).average().orElse(0);
            int max = list.stream().mapToInt(i -> i).max().orElse(0);
            stats.put(entry.getKey(), new SalaryStats(count, avg, max));
        }
        return stats;
    }

    /**
     * Топ N вакансий по максимальной зарплате.
     * @param N число вакансий
     * @return список отсортированных вакансий
     */
    public List<Vacancy> topSalaryVacancies(int N) {
        return repository.findAll().stream()
                .filter(v -> v.getSalaryMax() != null)
                .sorted(Comparator.comparingInt(Vacancy::getSalaryMax).reversed())
                .limit(N)
                .collect(Collectors.toList());
    }

    /**
     * Класс для хранения статистики зарплат.
     */
    public static class SalaryStats {
        private final int count;
        private final double average;
        private final int max;

        public SalaryStats(int count, double average, int max) {
            this.count = count;
            this.average = average;
            this.max = max;
        }

        public int getCount() {
            return count;
        }

        public double getAverage() {
            return average;
        }

        public int getMax() {
            return max;
        }
    }
}
