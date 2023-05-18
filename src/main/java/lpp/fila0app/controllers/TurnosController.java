package lpp.fila0app.controllers;

import lpp.fila0app.models.Turno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/turnos")
public class TurnosController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TurnosController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/registrar")
    private ResponseEntity<?> validarIngreso(@RequestBody Turno turno) {
        try {
            String sql = "INSERT INTO turnos(usuario, categoria) VALUES (?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            int rowsAffected = jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setObject(1, turno.getUsuario());
                statement.setObject(2, turno.getCategoria());
                return statement;
            }, keyHolder);

            if (rowsAffected > 0) {
                int idUsuario = keyHolder.getKey().intValue();
                String sql2 = "SELECT * FROM turnos WHERE id = ?";
                List<Turno> turnosEncontrados = jdbcTemplate.query(sql2, new Object[]{idUsuario}, new TurnoMapper());
                if (!turnosEncontrados.isEmpty()) {
                    return ResponseEntity.ok(turnosEncontrados.get(0));
                } else {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Ha ocurrido un error al intentar obtener los datos del turno registrado.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                throw new Exception("No se insertó ningún turno.");
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ha ocurrido un error al intentar registrar el turno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    private static class TurnoMapper implements RowMapper<Turno> {
        @Override
        public Turno mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new Turno(
                    resultSet.getInt("id"),
                    resultSet.getInt("usuario"),
                    resultSet.getInt("modulo"),
                    resultSet.getTimestamp("fecha"),
                    resultSet.getString("categoria"),
                    resultSet.getString("codigo"),
                    resultSet.getString("estado"),
                    resultSet.getTimestamp("fecha_asignado"),
                    resultSet.getTimestamp("fecha_cambio")
            );
        }
    }
}
