package com.techservices.api;

import com.techservices.api.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApiTechservicesApplicationTests {

    // Pruebas de validacion sin necesitar conexion a una base de datos.
    private final ClienteService service = new ClienteService(null, null);

    @Test
    void rechazaIdInvalido() {
        var error = assertThrows(ResponseStatusException.class,
                () -> service.consultarCliente(0, "key", "Bearer token"));
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatusCode());
    }

    @Test
    void rechazaCredencialesAusentes() {
        var error = assertThrows(ResponseStatusException.class,
                () -> service.consultarCliente(1, null, null));
        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
    }

    @Test
    void rechazaCabeceraSinBearer() {
        var error = assertThrows(ResponseStatusException.class,
                () -> service.consultarCliente(1, "api-key", "token-sin-bearer"));
        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
    }
}
