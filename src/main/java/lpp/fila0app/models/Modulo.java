package lpp.fila0app.models;

import jakarta.persistence.*;

@Entity
@Table(name="modulos")
public class Modulo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "usuario")
    private String usuario;
    @Column(name = "clave")
    private  String clave;

    public Modulo(long id, String usuario, String clave){
        this.id = id;
        this.usuario = usuario;
        this.clave = clave;
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
