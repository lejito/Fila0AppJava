package lpp.fila0app.controllers;

import lpp.fila0app.models.Turno;
import lpp.fila0app.models.TurnoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/turnos")
public class TurnosController {
    private final JdbcTemplate jdbcTemplate;
    public static final String ID_COLUMN = "id";
    public static final String USUARIO_COLUMN = "usuario";
    public static final String TIPO_DOCUMENTO_COLUMN = "tipo_documento";
    public static final String NUMERO_DOCUMENTO_COLUMN = "numero_documento";
    public static final String PRIMER_NOMBRE_COLUMN = "primer_nombre";
    public static final String SEGUNDO_NOMBRE_COLUMN = "segundo_nombre";
    public static final String PRIMER_APELLIDO_COLUMN = "primer_apellido";
    public static final String SEGUNDO_APELLIDO_COLUMN = "segundo_apellido";
    public static final String MODULO_COLUMN = "modulo";
    public static final String FECHA_COLUMN = "fecha";
    public static final String CATEGORIA_COLUMN = "categoria";
    public static final String CODIGO_COLUMN = "codigo";
    public static final String ESTADO_COLUMN = "estado";
    public static final String FECHA_ASIGNADO_COLUMN = "fecha_asignado";
    public static final String FECHA_CAMBIO_COLUMN = "fecha_cambio";

    @Autowired
    public TurnosController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/registrar")
    public ResponseEntity<Object> registrar(@RequestBody Turno turno) {
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
                Turno turnoGenerado = mapearTurno(Objects.requireNonNull(keyHolder.getKeys()));
                return ResponseEntity.ok(turnoGenerado);
            } else {
                return GlobalExceptionController.warningResponse("No se encontró el turno registrado.");
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ha ocurrido un error al intentar registrar el turno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/buscarPendientes")
    public ResponseEntity<Object> buscarPendientes() {
        String sql = "SELECT * FROM turnos INNER JOIN usuarios ON usuarios.id = turnos.usuario WHERE turnos.estado = 'Pendiente' ORDER BY turnos.id ASC LIMIT 32";
        List<TurnoUsuario> turnosPendientes = mapearListaTurnoUsuario(jdbcTemplate.queryForList(sql));
        if (!turnosPendientes.isEmpty()) {
            return ResponseEntity.ok(turnosPendientes);
        } else {
            return GlobalExceptionController.warningResponse("No se encontraron turnos pendientes.");
        }
    }

    @GetMapping("/buscarAsignados")
    public ResponseEntity<Object> buscarAsignados() {
        String sql = "SELECT * FROM turnos INNER JOIN usuarios ON usuarios.id = turnos.usuario WHERE turnos.estado = 'Asignado' ORDER BY turnos.fecha_asignado DESC LIMIT 8";
        List<TurnoUsuario> turnosAsignados = mapearListaTurnoUsuario(jdbcTemplate.queryForList(sql));
        if (!turnosAsignados.isEmpty()) {
            return ResponseEntity.ok(turnosAsignados);
        } else {
            return GlobalExceptionController.warningResponse("No se encontraron turnos asignados.");
        }
    }

    @GetMapping("/buscarCompletados")
    public ResponseEntity<Object> buscarCompletados() {
        String sql = "SELECT * FROM turnos INNER JOIN usuarios ON usuarios.id = turnos.usuario WHERE turnos.estado = 'Completado' ORDER BY turnos.fecha_cambio DESC LIMIT 32";
        List<TurnoUsuario> turnosCompletados = mapearListaTurnoUsuario(jdbcTemplate.queryForList(sql));
        if (!turnosCompletados.isEmpty()) {
            return ResponseEntity.ok(turnosCompletados);
        } else {
            return GlobalExceptionController.warningResponse("No se encontraron turnos completados.");
        }
    }

    @GetMapping("/buscarCancelados")
    public ResponseEntity<Object> buscarCancelados() {
        String sql = "SELECT * FROM turnos INNER JOIN usuarios ON usuarios.id = turnos.usuario WHERE turnos.estado = 'Cancelado' ORDER BY turnos.fecha_cambio DESC LIMIT 32";
        List<TurnoUsuario> turnosCancelados = mapearListaTurnoUsuario(jdbcTemplate.queryForList(sql));
        if (!turnosCancelados.isEmpty()) {
            return ResponseEntity.ok(turnosCancelados);
        } else {
            return GlobalExceptionController.warningResponse("No se encontraron turnos cancelados.");
        }
    }

    @PutMapping("/asignar")
    public ResponseEntity<Object> asignar(@RequestBody Turno turno) {
        String sql = "UPDATE turnos SET estado = 'Asignado', modulo = ?, fecha_asignado = CURRENT_TIMESTAMP WHERE id = (SELECT MIN(id) FROM turnos WHERE estado = 'Pendiente' AND (categoria = ? OR ? = 'N/A'))";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setObject(1, turno.getModulo());
            statement.setObject(2, turno.getCategoria());
            statement.setObject(3, turno.getCategoria());
            return statement;
        }, keyHolder);

        if (rowsAffected > 0) {
            int idTurno = (int) Objects.requireNonNull(keyHolder.getKeys()).get("id");
            String sql2 = "SELECT * FROM turnos INNER JOIN usuarios ON usuarios.id = turnos.usuario WHERE turnos.id = ?";
            List<TurnoUsuario> turnosEncontrados = mapearListaTurnoUsuario(jdbcTemplate.queryForList(sql2, idTurno));
            if (!turnosEncontrados.isEmpty()) {
                return ResponseEntity.ok(turnosEncontrados.get(0));
            } else {
                return GlobalExceptionController.warningResponse("No se encontraron los datos del turno asignado.");
            }
        } else {
            return GlobalExceptionController.warningResponse("No se encontró ningun turno para asignar.");
        }
    }

    @PutMapping("/actualizarEstado")
    public ResponseEntity<Object> actualizarEstado(@RequestBody Turno turno) {
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
            Turno turnoActualizado = mapearTurno(Objects.requireNonNull(keyHolder.getKeys()));
            return ResponseEntity.ok(turnoActualizado);
        } else {
            return GlobalExceptionController.warningResponse("No se encontró el turno a actualizar.");
        }
    }

    @PutMapping("/devolverAPendientes")
    public ResponseEntity<Object> devolverAPendientes(@RequestBody Turno turno) {
        String sql = "UPDATE turnos SET estado = 'Pendiente', modulo = NULL, fecha_cambio = CURRENT_TIMESTAMP WHERE id = ?";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setObject(1, turno.getId());
            return statement;
        }, keyHolder);

        if (rowsAffected > 0) {
            Turno turnoActualizado = mapearTurno(Objects.requireNonNull(keyHolder.getKeys()));
            return ResponseEntity.ok(turnoActualizado);
        } else {
            return GlobalExceptionController.warningResponse("No se encontró el turno a devolver a pendientes.");
        }
    }

    private Turno mapearTurno(Map<String, Object> keys) {
        Turno turno = new Turno();
        turno.setId((Integer) keys.get(ID_COLUMN));
        turno.setUsuario((Integer) keys.get(USUARIO_COLUMN));
        turno.setModulo((Integer) keys.get(MODULO_COLUMN));
        turno.setFecha((Timestamp) keys.get(FECHA_COLUMN));
        turno.setCategoria((String) keys.get(CATEGORIA_COLUMN));
        turno.setCodigo((String) keys.get(CODIGO_COLUMN));
        turno.setEstado((String) keys.get(ESTADO_COLUMN));
        turno.setFechaAsignado((Timestamp) keys.get(FECHA_ASIGNADO_COLUMN));
        turno.setFechaCambio((Timestamp) keys.get(FECHA_CAMBIO_COLUMN));
        return turno;
    }

    private TurnoUsuario mapearTurnoUsuario(Map<String, Object> keys) {
        TurnoUsuario turnoUsuario = new TurnoUsuario();
        turnoUsuario.setId((Integer) keys.get(ID_COLUMN));
        turnoUsuario.setUsuario((Integer) keys.get(USUARIO_COLUMN));
        turnoUsuario.setTipoDocumento((String) keys.get(TIPO_DOCUMENTO_COLUMN));
        turnoUsuario.setNumeroDocumento((String) keys.get(NUMERO_DOCUMENTO_COLUMN));
        turnoUsuario.setPrimerNombre((String) keys.get(PRIMER_NOMBRE_COLUMN));
        turnoUsuario.setSegundoNombre((String) keys.get(SEGUNDO_NOMBRE_COLUMN));
        turnoUsuario.setPrimerApellido((String) keys.get(PRIMER_APELLIDO_COLUMN));
        turnoUsuario.setSegundoApellido((String) keys.get(SEGUNDO_APELLIDO_COLUMN));
        turnoUsuario.setModulo((Integer) keys.get(MODULO_COLUMN));
        turnoUsuario.setFecha((Timestamp) keys.get(FECHA_COLUMN));
        turnoUsuario.setCategoria((String) keys.get(CATEGORIA_COLUMN));
        turnoUsuario.setCodigo((String) keys.get(CODIGO_COLUMN));
        turnoUsuario.setEstado((String) keys.get(ESTADO_COLUMN));
        turnoUsuario.setFechaAsignado((Timestamp) keys.get(FECHA_ASIGNADO_COLUMN));
        turnoUsuario.setFechaCambio((Timestamp) keys.get(FECHA_CAMBIO_COLUMN));
        return turnoUsuario;
    }

    private List<TurnoUsuario> mapearListaTurnoUsuario(List<Map<String, Object>> mapList) {
        return mapList.stream().map(this::mapearTurnoUsuario).collect(Collectors.toList());
    }
}
