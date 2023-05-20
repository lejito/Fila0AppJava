package lpp.fila0app.controllers;

import lpp.fila0app.models.Modulo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/modulos")
public class ModulosController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ModulosController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/validarLogin")
    public ResponseEntity<Object> validarLogin(@RequestBody Modulo modulo) {
        String sql = "SELECT * FROM modulos WHERE usuario = ? AND clave = ?";
        List<Modulo> modulosEncontrados = jdbcTemplate.query(sql, new Object[]{modulo.getUsuario(), modulo.getClave()}, new ModuloMapper());
        if (!modulosEncontrados.isEmpty()) {
            return ResponseEntity.ok(modulosEncontrados.get(0));
        } else {
            return GlobalExceptionController.warningResponse("Los datos de inicio de sesión son inválidos.");
        }
    }

    private static class ModuloMapper implements RowMapper<Modulo> {
        @Override
        public Modulo mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new Modulo(
                    resultSet.getInt("id"),
                    resultSet.getString("usuario"),
                    resultSet.getString("clave")
            );
        }
    }
}
