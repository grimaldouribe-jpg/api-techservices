package com.techservices.api.dto;

public record ConsultaClienteResponse(
        String mensaje,
        ClienteDatos cliente,
        Integer tokensRestantes
) {

    public record ClienteDatos(
            Integer id,
            String nombres,
            String apellidos,
            String dni,
            String correo,
            String telefono
    ) {
    }
}
