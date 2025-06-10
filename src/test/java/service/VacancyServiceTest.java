package service;

import repository.VacancyRepository;          // ← добавили
import model.Vacancy;
import parser.HhParser;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Тесты для VacancyService.
 */
public class VacancyServiceTest {

    private VacancyService service;
    private List<String>   savedIds;
    private List<Set<String>> deactivatedSets;

    @BeforeEach
    void setUp() throws Exception {
        savedIds       = new ArrayList<>();
        deactivatedSets= new ArrayList<>();

        // заглушка репозитория
        VacancyRepository stubRepo = new VacancyRepository() {
            @Override
            public void saveOrUpdate(Vacancy v){ savedIds.add(v.getHhId()); }
            @Override
            public void deactivateOldVacancies(Set<String> ids){ deactivatedSets.add(ids); }
        };

        // заглушка парсера
        HhParser stubParser = new HhParser() {
            @Override public List<Vacancy> fetchVacancies(String kw,int pages){
                Vacancy a=new Vacancy(); a.setHhId("A");
                Vacancy b=new Vacancy(); b.setHhId("B");
                return List.of(a,b);
            }
        };

        service = new VacancyService();

        // внедряем заглушки через Reflection
        Field repoF = VacancyService.class.getDeclaredField("repository");
        repoF.setAccessible(true); repoF.set(service, stubRepo);
        Field parsF = VacancyService.class.getDeclaredField("parser");
        parsF.setAccessible(true); parsF.set(service, stubParser);
    }

    /* … ваши @Test-методы остаются … */
}
