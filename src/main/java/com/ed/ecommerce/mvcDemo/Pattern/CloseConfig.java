package com.ed.ecommerce.mvcDemo.Pattern;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component // Anotación que marca esta clase como un componente de Spring para su gestión automática
public class CloseConfig {

    // Metodo que se ejecuta antes de que la aplicación se apague (cuando Spring detecta el cierre de la aplicación)
    @PreDestroy
    public void cerrarPoolDeConexiones() {
        // Llama al metodo estático de ConexionBD para cerrar el pool de conexiones
        ConexionBD.cerrarPool();
        // Mensaje en consola indicando que el pool de conexiones ha sido cerrado correctamente
        System.out.println("El pool de conexiones ha sido cerrado al apagar la aplicación");
    }
}
