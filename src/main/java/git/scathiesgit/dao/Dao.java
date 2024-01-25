package git.scathiesgit.dao;

import java.util.List;
import java.util.OptionalInt;

public interface Dao<T> {

    List<T> findAll();

    OptionalInt save(T entity);
}
