package lpp.fila0app.services;

import lpp.fila0app.models.Modulo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lpp.fila0app.repository.ModuloRepository;

import java.util.Optional;

@Service
public class ModuloService {
    @Autowired
    private ModuloRepository moduloRepository;

    public ModuloService(ModuloRepository moduloRepository) {
        this.moduloRepository = moduloRepository;
    }

    public Optional<Modulo> validarLogin(Modulo modulo) {
        return moduloRepository.validarLogin(modulo.getUsuario(), modulo.getClave());
    }
}
