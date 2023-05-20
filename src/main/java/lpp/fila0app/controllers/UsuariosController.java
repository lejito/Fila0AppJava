package lpp.fila0app.controllers;

import lpp.fila0app.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {
    private final JdbcTemplate jdbcTemplate;
    public static final String ID_COLUMN = "id";
    public static final String TIPO_DOCUMENTO_COLUMN = "tipo_documento";
    public static final String NUMERO_DOCUMENTO_COLUMN = "numero_documento";
    public static final String PRIMER_NOMBRE_COLUMN = "primer_nombre";
    public static final String SEGUNDO_NOMBRE_COLUMN = "segundo_nombre";
    public static final String PRIMER_APELLIDO_COLUMN = "primer_apellido";
    public static final String SEGUNDO_APELLIDO_COLUMN = "segundo_apellido";

    @Autowired
    public UsuariosController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/validarIngreso")
    public ResponseEntity<Object> validarIngreso(@RequestBody Usuario usuario) {
        String sql = "SELECT * FROM usuarios WHERE tipo_documento = ? AND numero_documento = ?";
        List<Usuario> usuariosEncontrados = mapearListaUsuario(jdbcTemplate.queryForList(sql, usuario.getTipoDocumento(), usuario.getNumeroDocumento()));
        if (!usuariosEncontrados.isEmpty()) {
            return ResponseEntity.ok(usuariosEncontrados.get(0));
        } else {
            return GlobalExceptionController.warningResponse("Los datos de ingreso son inválidos.");
        }
    }

    private Usuario mapearUsuario(Map<String, Object> keys) {
        Usuario usuario = new Usuario();
        usuario.setId((Integer) keys.get(ID_COLUMN));
        usuario.setTipoDocumento((String) keys.get(TIPO_DOCUMENTO_COLUMN));
        usuario.setNumeroDocumento((String) keys.get(NUMERO_DOCUMENTO_COLUMN));
        usuario.setPrimerNombre((String) keys.get(PRIMER_NOMBRE_COLUMN));
        usuario.setSegundoNombre((String) keys.get(SEGUNDO_NOMBRE_COLUMN));
        usuario.setPrimerApellido((String) keys.get(PRIMER_APELLIDO_COLUMN));
        usuario.setSegundoApellido((String) keys.get(SEGUNDO_APELLIDO_COLUMN));
        return usuario;
    }

    private List<Usuario> mapearListaUsuario(List<Map<String, Object>> mapList) {
        return mapList.stream().map(this::mapearUsuario).collect(Collectors.toList());
    }
}
