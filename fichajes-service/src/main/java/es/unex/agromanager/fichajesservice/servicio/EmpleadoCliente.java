// fichajes-service/src/main/java/.../servicio/EmpleadoCliente.java
package es.unex.agromanager.fichajesservice.servicio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@Component
public class EmpleadoCliente {

    private final RestClient rest;

    public EmpleadoCliente(@Value("${empleados.api.base}") String baseUrl) {
        this.rest = RestClient.builder().baseUrl(baseUrl).build();
    }

    /** Valida que el empleado existe en empleados-service usando el mismo JWT del usuario. */
    public void validarExiste(String usuario, String bearerToken) {
        try {
            rest.get()
                    .uri("/{usuario}", usuario)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new IllegalArgumentException("El empleado no existe: " + usuario);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new IllegalStateException("Autorización inválida al validar empleado");
        } catch (ResourceAccessException e) {
            throw new IllegalStateException("Servicio de empleados no disponible");
        }
    }
}
