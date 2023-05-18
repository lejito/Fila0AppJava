package lpp.fila0app.controllers;

import lpp.fila0app.models.Usuario;
import lpp.fila0app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosController {
    @Autowired
    private final UsuarioRepository usuarioRepository;

    public UsuariosController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/validarIngreso")
    private ResponseEntity<?> validarIngreso(@RequestBody Usuario usuario){
        try {
            String tipoDocumento = usuario.getTipoDocumento();
            String numeroDocumento = usuario.getNumeroDocumento();
            Optional<Usuario> usuarioEncontrado = usuarioRepository.validarIngreso(tipoDocumento, numeroDocumento);
            if (usuarioEncontrado.isPresent()) {
                return ResponseEntity.ok(usuarioEncontrado.get());
            }
            else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Los datos de ingreso son inválidos.'");
                return ResponseEntity.ok(response);
            }
        }
        catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ha ocurrido un error al intentar validar los datos de ingreso: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
