package vergilius.repos;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vergilius.Os;
import vergilius.Ttype;

import java.util.List;


public interface OsRepository extends CrudRepository<Os, Integer> {

    @Query("select u from Os u where u.osname = :osname")
    Os findByOsname(@Param("osname") String osname);

    @Query("select u from Os u where u.family = :famname")
    List<Os> findByFamily(@Param("famname") String famname);

    @Query("select u.family from Os u where u.osname = :osname")
    String findFamilyByOsname(@Param("osname") String osname);

    @Query("select DISTINCT u.family from Os u where u.arch = :arch")
    List<String> findByArch(@Param("arch") String arch);

    @Query("select u from Os u where u.arch = :arch and u.family = :famname")
    List<Os> findByArchAndFamily(@Param("arch") String arch, @Param("famname") String famname);

    @Query("select u from Os u where u.arch = :arch and u.family = :famname and u.osname = :osname")
    Os findByArchAndFamilyAndOsname(@Param("arch") String arch, @Param("famname") String famname, @Param("osname") String osname);

}