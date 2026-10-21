package com.denkitronik.clienteservice2.cliente.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "clientes")
// Agregamos "id" al orden para que aparezca en el JSON
@JsonPropertyOrder({"id", "nombre", "apellido", "email", "foto", "region_id", "createAt"})
@Getter
@Setter
public class Cliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 👈 QUITAMOS EL @JsonIgnore

    @NotEmpty(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 20, message = "El nombre debe tener entre 3 y 20 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotEmpty(message = "El apellido no puede estar vacío")
    @Size(min = 3, max = 20, message = "El apellido debe tener entre 3 y 20 caracteres")
    private String apellido;

    @NotEmpty(message = "El email no puede estar vacío")
    @Email(message = "El email no tiene un formato válido")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "create_at")
    @Temporal(TemporalType.DATE)
    private Date createAt; //  QUITAMOS EL @JsonIgnore para que la fecha viaje

    private String foto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Region region;

    @JsonProperty("region_id")
    public Long getRegionId() {
        return (region != null) ? region.getId() : null;
    }
}