package lpp.fila0app.controllers;

import lpp.fila0app.models.Modulo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/modulos")
public class ModulosController {
    private final JdbcTemplate jdbcTemplate;
    public static final String ID_COLUMN = "id";
    public static final String USUARIO_COLUMN = "usuario";
    public static final String CLAVE_COLUMN = "clave";

    @Autowired
    public ModulosController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/validarLogin")
    public ResponseEntity<Object> validarLogin(@RequestBody Modulo modulo) {
        String sql = "SELECT * FROM modulos WHERE usuario = ? AND clave = ?";
        List<Modulo> modulosEncontrados = mapearListaModulo(jdbcTemplate.queryForList(sql, modulo.getUsuario(), modulo.getClave()));
        if (!modulosEncontrados.isEmpty()) {
            return ResponseEntity.ok(modulosEncontrados.get(0));
        } else {
            return GlobalExceptionController.warningResponse("Los datos de inicio de sesión son inválidos.");
        }
    }

    private Modulo mapearModulo(Map<String, Object> keys) {
        Modulo modulo = new Modulo();
        modulo.setId((Integer) keys.get(ID_COLUMN));
        modulo.setUsuario((String) keys.get(USUARIO_COLUMN));
        modulo.setClave((String) keys.get(CLAVE_COLUMN));
        return modulo;
    }

    private List<Modulo> mapearListaModulo(List<Map<String, Object>> mapList) {
        return mapList.stream().map(this::mapearModulo).collect(Collectors.toList());
    }
}
