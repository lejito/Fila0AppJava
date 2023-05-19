package lpp.fila0app.controllers;

import lpp.fila0app.models.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UsuariosController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/validarIngreso")
    public ResponseEntity<Object> validarIngreso(@RequestBody Usuario usuario) {
        String sql = "SELECT * FROM usuarios WHERE tipo_documento = ? AND numero_documento = ?";
        List<Usuario> usuariosEncontrados = jdbcTemplate.query(sql, new Object[]{usuario.getTipoDocumento(), usuario.getNumeroDocumento()}, new UsuarioMapper());
        if (!usuariosEncontrados.isEmpty()) {
            return ResponseEntity.ok(usuariosEncontrados.get(0));
        } else {
            return GlobalExceptionController.warningResponse("Los datos de ingreso son inválidos.");
        }
    }

    private static class UsuarioMapper implements RowMapper<Usuario> {
        @Override
        public Usuario mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new Usuario(
                    resultSet.getInt("id"),
                    resultSet.getString("tipo_documento"),
                    resultSet.getString("numero_documento"),
                    resultSet.getString("primer_nombre"),
                    resultSet.getString("segundo_nombre"),
                    resultSet.getString("primer_apellido"),
                    resultSet.getString("segundo_apellido")
            );
        }
    }
}
