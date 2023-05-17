package controller;

import models.Modulo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.ModuloService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/modulos")
public class ModuloController {
    @Autowired
    private ModuloService moduloService;

    @PostMapping
    private ResponseEntity<Modulo> save(@RequestBody Modulo modulo){
        Modulo temporal = moduloService.create(modulo);
        try {
                return  ResponseEntity.created(new URI("/api/modulos"+temporal.getId())).body(temporal);
        }
         catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    private ResponseEntity<List<Modulo>> listModulos(){
        try {
            return  ResponseEntity.ok(moduloService.getAllModulos());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping
    private  ResponseEntity<Void> deleteModulo(@RequestBody Modulo modulo){
        try {
            moduloService.delete(modulo);
            return  ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping( value = "{id}")
    private ResponseEntity<Optional<Modulo>> getModulo(@PathVariable long id){
        try {
            return  ResponseEntity.ok(moduloService.getById(id));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
