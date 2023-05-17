package models;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

@Entity
@Table(name="modulos")
public class Modulo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "usuario")
    private String usuario;
    @Column(name = "clave")
    private  String clave;

    public Modulo(long id,String clave,String usuario){
        this.id = id;
        this.clave = clave;
        this.usuario = usuario;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
