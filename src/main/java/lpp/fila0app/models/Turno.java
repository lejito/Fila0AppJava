package lpp.fila0app.models;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "turnos")
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;

    @Column(name = "usuario")
    private long usuario;

    @Column(name = "modulo")
    private long modulo;

    @Column(name = "fecha")
    private Timestamp fecha;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_asignado")
    private Timestamp fechaAsignado;

    @Column(name = "fecha_cambio")
    private Timestamp fechaCambio;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUsuario() {
        return usuario;
    }

    public void setUsuario(long usuario) {
        this.usuario = usuario;
    }

    public long getModulo() {
        return modulo;
    }

    public void setModulo(long modulo) {
        this.modulo = modulo;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFechaAsignado() {
        return fechaAsignado;
    }

    public void setFechaAsignado(Timestamp fechaAsignado) {
        this.fechaAsignado = fechaAsignado;
    }

    public Timestamp getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(Timestamp fechaCambio) {
        this.fechaCambio = fechaCambio;
    }
}