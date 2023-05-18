package lpp.fila0app.controllers;

import lpp.fila0app.models.Turno;
import lpp.fila0app.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/turnos")
public class TurnosController {
    @Autowired
    private final TurnoRepository turnoRepository;

    public TurnosController(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    @PostMapping("/registrar")
    private ResponseEntity<?> registrar(@RequestBody Turno turno) {
        try {
            Turno turnoCreado = turnoRepository.save(turno);
            return ResponseEntity.ok(turnoCreado);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Ha ocurrido un error al intentar registrar el turno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
