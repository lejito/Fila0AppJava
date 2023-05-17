package lpp.fila0app.repository;

import lpp.fila0app.models.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {
    @Query(value = """
                SELECT id
                FROM modulos
                WHERE usuario = :usuario AND clave = :clave
            """, nativeQuery = true)
    Optional<Modulo> validarLogin(@Param("usuario") String usuario, @Param("clave") String clave);

}
