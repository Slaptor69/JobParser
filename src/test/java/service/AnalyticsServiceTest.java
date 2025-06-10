package service;

import dao.VacancyRepository;           // ← правильный импорт
import model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юнит-тест для AnalyticsService.
 */
public class AnalyticsServiceTest {

    private AnalyticsService analytics;
    private VacancyRepository repository;

    @BeforeEach
    public void setUp() throws Exception {
        // Заглушка репозитория
        repository = new VacancyRepository() {

            public List<Vacancy> findAll() {
                List<Vacancy> data = new ArrayList<>();
                data.add(createVacancy("1", "Dev",     "A", "Moscow", 50000, 70000, "IT",     LocalDate.of(2025, 1, 1)));
                data.add(createVacancy("2", "QA",      "B", "Moscow", 40000, 60000, "QA",     LocalDate.of(2025, 2, 1)));
                data.add(createVacancy("3", "DevOps",  "C", "Spb",    null,   80000, "DevOps", LocalDate.of(2025, 3, 1)));
                return data;
            }
        };

        analytics = new AnalyticsService();
        // внедряем заглушку через reflection
        Field f = AnalyticsService.class.getDeclaredField("repository");
        f.setAccessible(true);
        f.set(analytics, repository);
    }

    private Vacancy createVacancy(String id, String title, String comp, String city,
                                  Integer min, Integer max, String cat, LocalDate date) {
        Vacancy v = new Vacancy();
        v.setHhId(id);
        v.setTitle(title);
        v.setCompany(comp);
        v.setCity(city);
        v.setSalaryMin(min);
        v.setSalaryMax(max);
        v.setCategory(cat);
        v.setPublishedDate(date);
        return v;
    }

    @Test
    public void testCountByCategory() {
        Map<String,Integer> m = analytics.countByCategory();
        assertEquals(3, m.size());
        assertEquals(1, m.get("IT"));
        assertEquals(1, m.get("QA"));
        assertEquals(1, m.get("DevOps"));
    }

    @Test
    public void testSalaryStatsByCity() {
        Map<String,AnalyticsService.SalaryStats> s = analytics.salaryStatsByCity();
        assertEquals(2, s.size());

        AnalyticsService.SalaryStats mos = s.get("Moscow");
        assertEquals(2, mos.getCount());
        assertEquals(65000.0, mos.getAverage(), 0.1);
        assertEquals(70000, mos.getMax());

        AnalyticsService.SalaryStats spb = s.get("Spb");
        assertEquals(1, spb.getCount());
        assertEquals(80000.0, spb.getAverage(), 0.1);
        assertEquals(80000, spb.getMax());
    }

    @Test
    public void testTopSalaryVacancies() {
        List<Vacancy> top2 = analytics.topSalaryVacancies(2);
        assertEquals(2, top2.size());
        assertEquals("3", top2.get(0).getHhId());   // 80 000
        assertEquals("1", top2.get(1).getHhId());   // 70 000
    }
}
