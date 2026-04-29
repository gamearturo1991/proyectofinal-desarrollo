package mx.swb.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "tipo_movimiento_inventarios")
public class TipoMovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipo_movimiento_id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;

    // 1 = entrada, -1 = salida
    @NotNull
    @Column(name = "signo", nullable = false)
    private Integer signo;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getSigno() {
        return signo;
    }

    public void setSigno(Integer signo) {
        this.signo = signo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

