package com.techservices.api.repository;

import com.techservices.api.model.UsuarioApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioApiRepository extends JpaRepository<UsuarioApi, Integer> {

    Optional<UsuarioApi> findByApiKeyHashAndTokenAuthHashAndEstado(
            String apiKeyHash, String tokenAuthHash, String estado);

    // Actualizacion atomica: impide saldos negativos incluso con consultas concurrentes.
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            UPDATE usuarios_api
            SET tokens_disponibles = tokens_disponibles - 1
            WHERE id = :id
              AND estado = 'ACTIVO'
              AND tokens_disponibles > 0
            """, nativeQuery = true)
    int descontarToken(@Param("id") Integer id);

    @Query(value = """
            SELECT tokens_disponibles
            FROM usuarios_api
            WHERE id = :id
            """, nativeQuery = true)
    Integer obtenerSaldo(@Param("id") Integer id);
}
