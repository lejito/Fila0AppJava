package lpp.fila0app.repository;

import lpp.fila0app.models.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {
    @Query(value = "SELECT m FROM Modulo m WHERE m.usuario = ?1 AND m.clave = ?2")
    Optional<Modulo> validarLogin(String usuario, String clave);
}
