package lpp.fila0app.controllers;

import lpp.fila0app.models.Modulo;
import lpp.fila0app.repository.ModuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/modulos")
public class ModulosController {
    @Autowired
    private final ModuloRepository moduloRepository;

    public ModulosController(ModuloRepository moduloRepository) {
        this.moduloRepository = moduloRepository;
    }

    @PostMapping("/validarLogin")
    private ResponseEntity<?> validarLogin(@RequestBody Modulo modulo){
        try {
            String usuario = modulo.getUsuario();
            String clave = modulo.getClave();
            Optional<Modulo> moduloEncontrado = moduloRepository.validarLogin(usuario, clave);
            if (moduloEncontrado.isPresent()) {
                return ResponseEntity.ok(moduloEncontrado.get());
            }
            else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Los datos de inicio de sesión son inválidos.");
                return ResponseEntity.ok(response);
            }
        }
         catch (Exception e) {
             Map<String, String> response = new HashMap<>();
             response.put("error", "Ha ocurrido un error al intentar validar los datos de inicio de sesión: " + e.getMessage());
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
