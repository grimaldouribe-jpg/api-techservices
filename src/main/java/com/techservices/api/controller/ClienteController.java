package com.techservices.api.controller;

import com.techservices.api.dto.ConsultaClienteResponse;
import com.techservices.api.service.ClienteService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // CONSULTAR CLIENTE POR ID
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaClienteResponse> consultarCliente(

            @PathVariable("id") Integer id,

            @RequestHeader(
                value = "x-api-key",
                required = false
            ) String apiKey,

            @RequestHeader(
                value = "Authorization",
                required = false
            ) String authorization) {

        ConsultaClienteResponse respuesta =
                clienteService.consultarCliente(
                    id,
                    apiKey,
                    authorization
                );

        return ResponseEntity.ok(respuesta);
    }
}
