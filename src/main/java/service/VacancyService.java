package service;

import repository.VacancyRepository;
import model.Vacancy;
import parser.HhParser;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VacancyService {

    private final HhParser parser = new HhParser();
    private final VacancyRepository repository = new VacancyRepository();

    /** Парсинг + сохранение, показывает что происходит. */
    public void updateVacancies(String keyword, int pages) {
        long t0 = System.currentTimeMillis();

        List<Vacancy> parsed = parser.fetchVacancies(keyword, pages);

        System.out.println("С парсера пришло: " + parsed.size());
        for (Vacancy v : parsed) {
            System.out.println(" → " + v.getHhId() + " | " + v.getTitle());
        }

        Set<String> currentIds = parsed.stream()
                .map(Vacancy::getHhId)
                .collect(Collectors.toSet());

        for (Vacancy v : parsed) {
            boolean isNew = repository.findByHhId(v.getHhId()) == null;
            repository.saveOrUpdate(v);
            System.out.println((isNew ? "➕  Добавлена: " : "♻  Обновлена: ")
                    + v.getTitle() + " (" + v.getHhId() + ")");
        }

        repository.deactivateOldVacancies(currentIds);

        System.out.println("✔ Завершено за "
                + (System.currentTimeMillis() - t0) + " мс");
    }

    public List<Vacancy> getAllVacancies()          { return repository.findAll(); }
    public List<Vacancy> searchByKeyword(String kw) { return repository.findByKeyword(kw); }
}
