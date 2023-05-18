package lpp.fila0app.controllers;

import lpp.fila0app.models.Turno;
import lpp.fila0app.models.TurnoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/api/turnos")
public class TurnosController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TurnosController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/registrar")
    private ResponseEntity<?> registrar(@RequestBody Turno turno) {
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
                Turno turnoGenerado = new Turno(
                        (Integer) keyHolder.getKeys().get("id"),
                        (Integer) keyHolder.getKeys().get("usuario"),
                        (Integer) keyHolder.getKeys().get("modulo"),
                        (Timestamp) keyHolder.getKeys().get("fecha"),
                        (String) keyHolder.getKeys().get("categoria"),
                        (String) keyHolder.getKeys().get("codigo"),
                        (String) keyHolder.getKeys().get("estado"),
                        (Timestamp) keyHolder.getKeys().get("fecha_asignado"),
                        (Timestamp) keyHolder.getKeys().get("fecha_cambio")
                );
                return ResponseEntity.ok(turnoGenerado);
            } else {
                throw new Exception("No se insertó ningún turno.");
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ha ocurrido un error al intentar registrar el turno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/buscarPendientes")
    private ResponseEntity<?> buscarPendientes() {
        try {
            String sql = "SELECT * FROM turnos INNER JOIN usuarios ON usuarios.id = turnos.usuario WHERE turnos.estado = 'Pendiente' ORDER BY turnos.id ASC LIMIT 32";
            List<TurnoUsuario> turnosPendientes = jdbcTemplate.query(sql, new TurnosController.TurnoUsuarioMapper());
            if (!turnosPendientes.isEmpty()) {
                return ResponseEntity.ok(turnosPendientes);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "No se encontraron turnos pendientes.");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ha ocurrido un error al intentar buscar los últimos turnos pendientes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/buscarAsignados")
    private ResponseEntity<?> buscarAsignados() {
        try {
            String sql = "SELECT * FROM turnos INNER JOIN usuarios ON usuarios.id = turnos.usuario WHERE turnos.estado = 'Asignado' ORDER BY turnos.fecha_asignado DESC LIMIT 8";
            List<TurnoUsuario> turnosAsignados = jdbcTemplate.query(sql, new TurnosController.TurnoUsuarioMapper());
            if (!turnosAsignados.isEmpty()) {
                return ResponseEntity.ok(turnosAsignados);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "No se encontraron turnos asignados.");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ha ocurrido un error al intentar buscar los últimos turnos asignados: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/buscarCompletados")
    private ResponseEntity<?> buscarCompletados() {
        try {
            String sql = "SELECT * FROM turnos INNER JOIN usuarios ON usuarios.id = turnos.usuario WHERE turnos.estado = 'Completado' ORDER BY turnos.fecha_cambio DESC LIMIT 32";
            List<TurnoUsuario> turnosCompletados = jdbcTemplate.query(sql, new TurnosController.TurnoUsuarioMapper());
            if (!turnosCompletados.isEmpty()) {
                return ResponseEntity.ok(turnosCompletados);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "No se encontraron turnos completados.");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ha ocurrido un error al intentar buscar los últimos turnos completados: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/buscarCancelados")
    private ResponseEntity<?> buscarCancelados() {
        try {
            String sql = "SELECT * FROM turnos INNER JOIN usuarios ON usuarios.id = turnos.usuario WHERE turnos.estado = 'Cancelado' ORDER BY turnos.fecha_cambio DESC LIMIT 32";
            List<TurnoUsuario> turnosCancelados = jdbcTemplate.query(sql, new TurnosController.TurnoUsuarioMapper());
            if (!turnosCancelados.isEmpty()) {
                return ResponseEntity.ok(turnosCancelados);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "No se encontraron turnos cancelados.");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ha ocurrido un error al intentar buscar los últimos turnos cancelados: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/asignar")
    private ResponseEntity<?> asignar(@RequestBody Turno turno) {
        try {
            String sql;
            if (Objects.equals(turno.getCategoria(), "N/A")) {
                sql = "UPDATE turnos SET estado = 'Asignado', modulo = ?, fecha_asignado = CURRENT_TIMESTAMP WHERE id = (SELECT MIN(id) FROM turnos WHERE estado = 'Pendiente')";
            } else {
                sql = "UPDATE turnos SET estado = 'Asignado', modulo = ?, fecha_asignado = CURRENT_TIMESTAMP WHERE id = (SELECT MIN(id) FROM turnos WHERE estado = 'Pendiente' AND categoria = ?)";
            }

            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rowsAffected = jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setObject(1, turno.getModulo());
                if (!Objects.equals(turno.getCategoria(), "N/A")) {
                    statement.setObject(2, turno.getCategoria());
                }
                return statement;
            }, keyHolder);

            if (rowsAffected > 0) {
                try {
                    int idTurno = (int) keyHolder.getKeys().get("id");
                    String sql2 = "SELECT * FROM turnos INNER JOIN usuarios ON usuarios.id = turnos.usuario WHERE turnos.id = ?";
                    List<TurnoUsuario> turnosEncontrados = jdbcTemplate.query(sql2, new TurnosController.TurnoUsuarioMapper(), idTurno);
                    if (!turnosEncontrados.isEmpty()) {
                        return ResponseEntity.ok(turnosEncontrados.get(0));
                    } else {
                        Map<String, String> response = new HashMap<>();
                        response.put("error", "No se encontraron turnos con el id generado.");
                        return ResponseEntity.ok(response);
                    }
                } catch (Exception e) {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Ha ocurrido un error al intentar buscar los datos del turno asignado: " + e.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            } else {
                throw new Exception("No se asignó ningún turno.");
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ha ocurrido un error al intentar asignar un turno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/actualizarEstado")
    private ResponseEntity<?> actualizarEstado(@RequestBody Turno turno) {
        try {
            String sql = "UPDATE turnos SET estado = ?, modulo = ?, fecha_cambio = CURRENT_TIMESTAMP WHERE id = ?";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rowsAffected = jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setObject(1, turno.getEstado());
                statement.setObject(2, turno.getModulo());
                statement.setObject(3, turno.getId());
                return statement;
            }, keyHolder);

            if (rowsAffected > 0) {
                Turno turnoActualizado = new Turno(
                        (Integer) keyHolder.getKeys().get("id"),
                        (Integer) keyHolder.getKeys().get("usuario"),
                        (Integer) keyHolder.getKeys().get("modulo"),
                        (Timestamp) keyHolder.getKeys().get("fecha"),
                        (String) keyHolder.getKeys().get("categoria"),
                        (String) keyHolder.getKeys().get("codigo"),
                        (String) keyHolder.getKeys().get("estado"),
                        (Timestamp) keyHolder.getKeys().get("fecha_asignado"),
                        (Timestamp) keyHolder.getKeys().get("fecha_cambio")
                );
                return ResponseEntity.ok(turnoActualizado);
            } else {
                throw new Exception("No se actualizó el estado del turno.");
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ha ocurrido un error al intentar actualizar el estado del turno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/devolverAPendientes")
    private ResponseEntity<?> devolverAPendientes(@RequestBody Turno turno) {
        try {
            String sql = "UPDATE turnos SET estado = 'Pendiente', modulo = NULL, fecha_cambio = CURRENT_TIMESTAMP WHERE id = ?";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rowsAffected = jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setObject(1, turno.getId());
                return statement;
            }, keyHolder);

            if (rowsAffected > 0) {
                Turno turnoActualizado = new Turno(
                        (Integer) keyHolder.getKeys().get("id"),
                        (Integer) keyHolder.getKeys().get("usuario"),
                        (Integer) keyHolder.getKeys().get("modulo"),
                        (Timestamp) keyHolder.getKeys().get("fecha"),
                        (String) keyHolder.getKeys().get("categoria"),
                        (String) keyHolder.getKeys().get("codigo"),
                        (String) keyHolder.getKeys().get("estado"),
                        (Timestamp) keyHolder.getKeys().get("fecha_asignado"),
                        (Timestamp) keyHolder.getKeys().get("fecha_cambio")
                );
                return ResponseEntity.ok(turnoActualizado);
            } else {
                throw new Exception("No se actualizó el estado del turno a 'Pendiente'.");
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ha ocurrido un error al intentar actualizar a 'Pendiente' el estado del turno: " + e.getMessage());
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

    private static class TurnoUsuarioMapper implements RowMapper<TurnoUsuario> {
        @Override
        public TurnoUsuario mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new TurnoUsuario(
                    resultSet.getInt("id"),
                    resultSet.getInt("usuario"),
                    resultSet.getString("tipo_documento"),
                    resultSet.getString("numero_documento"),
                    resultSet.getString("primer_nombre"),
                    resultSet.getString("segundo_nombre"),
                    resultSet.getString("primer_apellido"),
                    resultSet.getString("segundo_apellido"),
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
