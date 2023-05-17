package lpp.fila0app.controllers;

import lpp.fila0app.models.Modulo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lpp.fila0app.services.ModuloService;

import java.util.Optional;

@RestController
@RequestMapping("/api/modulos")
public class ModulosController {
    @Autowired
    private ModuloService moduloService;

    public ModulosController(ModuloService moduloService) {
        this.moduloService = moduloService;
    }

    @RequestMapping("/validarLogin")
    private ResponseEntity<Optional<Modulo>> validarLogin(@RequestBody Modulo modulo){
        try {
                return ResponseEntity.ok(moduloService.validarLogin(modulo));
        }
         catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
