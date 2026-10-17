package com.techservices.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios_api")
@Getter
@Setter
@NoArgsConstructor
public class UsuarioApi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nombre_usuario", nullable = false, length = 100)
    private String nombreUsuario;

    @Column(name = "api_key_hash", nullable = false, length = 64)
    private String apiKeyHash;

    @Column(name = "token_auth_hash", nullable = false, length = 64)
    private String tokenAuthHash;

    @Column(name = "tokens_disponibles", nullable = false)
    private Integer tokensDisponibles;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
}
