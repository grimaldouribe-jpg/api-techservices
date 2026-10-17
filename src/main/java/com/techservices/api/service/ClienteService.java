package com.techservices.api.service;

import com.techservices.api.dto.ConsultaClienteResponse;
import com.techservices.api.model.Cliente;
import com.techservices.api.model.UsuarioApi;
import com.techservices.api.repository.ClienteRepository;
import com.techservices.api.repository.UsuarioApiRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioApiRepository usuarioRepository;

    public ClienteService(
            ClienteRepository clienteRepository,
            UsuarioApiRepository usuarioRepository) {

        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ConsultaClienteResponse consultarCliente(
            Integer idCliente,
            String apiKey,
            String authorization) {

        // 1. VALIDAR EL ID
        if (idCliente == null || idCliente <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El ID debe ser mayor a cero"
            );
        }

        // 2. EXTRAER EL BEARER TOKEN
        String token = extraerBearerToken(authorization);

        // 3. COMPROBAR CREDENCIALES
        if (apiKey == null || apiKey.isBlank()
                || token == null || token.isBlank()) {

            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Debe proporcionar API Key y Bearer Token"
            );
        }

        // 4. GENERAR HASHES SHA-256
        String apiKeyHash = generarHash(apiKey);
        String tokenHash = generarHash(token);

        // 5. VALIDAR USUARIO ACTIVO
        UsuarioApi usuario = usuarioRepository
            .findByApiKeyHashAndTokenAuthHashAndEstado(
                apiKeyHash,
                tokenHash,
                "ACTIVO"
            )
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales incorrectas o usuario inactivo"
                )
            );

        // 6. VERIFICAR SALDO
        if (usuario.getTokensDisponibles() == null
                || usuario.getTokensDisponibles() <= 0) {

            throw new ResponseStatusException(
                HttpStatus.TOO_MANY_REQUESTS,
                "No tiene tokens disponibles"
            );
        }

        // 7. CONSULTAR CLIENTE
        Cliente cliente = clienteRepository
            .findById(idCliente)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cliente no encontrado"
                )
            );

        // 8. DESCONTAR UN TOKEN
        int actualizados = usuarioRepository
            .descontarToken(usuario.getId());

        if (actualizados != 1) {
            throw new ResponseStatusException(
                HttpStatus.TOO_MANY_REQUESTS,
                "No tiene tokens disponibles"
            );
        }

        // 9. OBTENER SALDO ACTUALIZADO
        Integer saldo = usuarioRepository
            .obtenerSaldo(usuario.getId());

        if (saldo == null) {
            throw new IllegalStateException(
                "No se pudo obtener el saldo actualizado"
            );
        }

        // 10. CREAR RESPUESTA
        var datos = new ConsultaClienteResponse.ClienteDatos(
            cliente.getId(),
            cliente.getNombres(),
            cliente.getApellidos(),
            cliente.getDni(),
            cliente.getCorreo(),
            cliente.getTelefono()
        );

        return new ConsultaClienteResponse(
            "Consulta realizada correctamente",
            datos,
            saldo
        );
    }

    // EXTRAER BEARER TOKEN
    private String extraerBearerToken(String authorization) {

        if (authorization == null) {
            return null;
        }

        String valor = authorization.trim();

        if (!valor.regionMatches(
                true, 0, "Bearer ", 0, 7)) {
            return null;
        }

        String token = valor.substring(7).trim();

        return token.isEmpty() ? null : token;
    }

    // HASH SHA-256
    private String generarHash(String valor) {

        try {
            MessageDigest digest =
                MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                valor.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                "Algoritmo SHA-256 no disponible",
                e
            );
        }
    }
}
