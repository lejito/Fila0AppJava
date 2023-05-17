package repository;
import models.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuloRespository extends JpaRepository<Modulo, Long> {
}
