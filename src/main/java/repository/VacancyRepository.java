package repository;

import db.DatabaseManager;
import model.Vacancy;

import java.sql.*;
import java.util.*;

public class VacancyRepository {

    /* ------------------------------------------------------------------ */
    /* saveOrUpdate — теперь пишет category и description                 */
    /* ------------------------------------------------------------------ */
    public void saveOrUpdate(Vacancy v) {
        String sql = """
            INSERT INTO vacancy
              (hh_id,title,company,city,salary_min,salary_max,
               category,description,active)
            VALUES (?,?,?,?,?,?,?,?,true)
            ON CONFLICT (hh_id) DO UPDATE SET
               title        = EXCLUDED.title,
               company      = EXCLUDED.company,
               city         = EXCLUDED.city,
               salary_min   = EXCLUDED.salary_min,
               salary_max   = EXCLUDED.salary_max,
               category     = EXCLUDED.category,
               description  = EXCLUDED.description,
               active       = true
            """;
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, v.getHhId());
            ps.setString(2, v.getTitle());
            ps.setString(3, v.getCompany());
            ps.setString(4, v.getCity());

            if (v.getSalaryMin()!=null) ps.setInt(5, v.getSalaryMin());
            else ps.setNull(5, Types.INTEGER);

            if (v.getSalaryMax()!=null) ps.setInt(6, v.getSalaryMax());
            else ps.setNull(6, Types.INTEGER);

            ps.setString(7, v.getCategory());
            ps.setString(8, v.getDescription());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("saveOrUpdate error", e);
        }
    }

    /** Все активные вакансии. */
    public List<Vacancy> findAll() {
        List<Vacancy> list = new ArrayList<>();
        String sql = "SELECT * FROM vacancy WHERE active = TRUE";
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("findAll error", e);
        }
        return list;
    }

    public Vacancy findByHhId(String hhId) {
        String sql = "SELECT * FROM vacancy WHERE hh_id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, hhId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByHhId error", e);
        }
        return null;
    }
    public List<Vacancy> findByKeyword(String kw) {
        List<Vacancy> list = new ArrayList<>();
        String sql = """
            SELECT * FROM vacancy
             WHERE active = TRUE
               AND (LOWER(title) LIKE ? OR LOWER(description) LIKE ?)
            """;
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String pattern = "%" + kw.toLowerCase() + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByKeyword error", e);
        }
        return list;
    }

    private Connection getConnection() {
        return DatabaseManager.getConnection();
    }

    private Vacancy map(ResultSet rs) throws SQLException {
        Vacancy v = new Vacancy();
        v.setId(rs.getInt("id"));
        v.setHhId(rs.getString("hh_id"));
        v.setTitle(rs.getString("title"));
        v.setCompany(rs.getString("company"));
        v.setCity(rs.getString("city"));
        int min = rs.getInt("salary_min"); if (!rs.wasNull()) v.setSalaryMin(min);
        int max = rs.getInt("salary_max"); if (!rs.wasNull()) v.setSalaryMax(max);
        v.setDescription(rs.getString("description"));
        v.setCategory(rs.getString("category"));
        return v;
    }

    public void deactivateOldVacancies(Set<String> currentIds) {
    }
}
