package lpp.fila0app.repository;

import lpp.fila0app.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Query(value = "SELECT u FROM Usuario u WHERE u.tipoDocumento = ?1 AND u.numeroDocumento = ?2")
    Optional<Usuario> validarIngreso(String tipoDocumento, String numeroDocumento);
}