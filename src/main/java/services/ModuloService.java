package services;

import models.Modulo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.ModuloRespository;

import java.util.List;
import java.util.Optional;

@Service
public class ModuloService {
    @Autowired
    private ModuloRespository moduloRespository;
     public Modulo create(Modulo modulo){
          return moduloRespository.save(modulo);
     }
     public List<Modulo> getAllModulos(){
         return  moduloRespository.findAll();
     }
     public void delete(Modulo modulo){
         moduloRespository.delete(modulo);
     }
     public Optional<Modulo> getById(Long id){
         return moduloRespository.findById(id);
     }
}
