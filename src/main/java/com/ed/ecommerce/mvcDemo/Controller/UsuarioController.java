package com.ed.ecommerce.mvcDemo.Controller;

import com.ed.ecommerce.mvcDemo.Model.Usuario;
import com.ed.ecommerce.mvcDemo.Services.ServiceUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/essalud")
public class UsuarioController {

    @Autowired
    private ServiceUsuario serviceUsuario;

    // Mostrar formulario de inicio de sesión
    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {
        if (session.getAttribute("usuario") != null) {
            return "redirect:/essalud/index";
        }
        return "login"; // Retorna la vista de login
    }

    // Procesar inicio de sesión
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo, @RequestParam String contrasena, HttpSession session, Model model) {
        Usuario usuario = serviceUsuario.iniciarSesion(correo, contrasena);
        if (usuario != null) {
            session.setAttribute("usuario", usuario);
            return "redirect:/essalud/index";
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }
    }

    // Procesar registro de usuarios
    @PostMapping("/registrar")
    public String registrarUsuario(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String dni,
            @RequestParam String correo,
            @RequestParam String contrasena,
            Model model) {

        Usuario nuevoUsuario = new Usuario(nombre, apellido, dni, correo, contrasena);
        boolean registroExitoso = serviceUsuario.registrar(nuevoUsuario);

        if (registroExitoso) {
            model.addAttribute("success", "Registro exitoso. ¡Ahora puedes iniciar sesión!");
            return "redirect:/essalud/login"; // redirige a la página de login
        } else {
            model.addAttribute("error", "El correo o DNI ya están registrados.");
            return "login"; // página de registro
        }
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/essalud/login";
    }

    // Mostrar el index
    @GetMapping("/index")
    public String mostrarIndex(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/essalud/login";
        }
        model.addAttribute("usuario", usuario); // <-- enviamos los datos
        return "index";
    }


    //Mostrar detalles de la cuenta
    @GetMapping("/cuenta")
    public String mostrarCuenta(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/essalud/login";
        }
        model.addAttribute("usuario", usuario); // <-- enviamos los datos
        return "cuenta";
    }

    @GetMapping("/perfil/{id}")
    public String mostrarPerfil(@PathVariable int id, Model model) {
        Usuario usuario = serviceUsuario.obtenerPorId(id);
        model.addAttribute("usuario", usuario);
        return "perfilUsuario";
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuario) {
        serviceUsuario.actualizarDatos(usuario);
        return "redirect:/usuario/perfil/" + usuario.getIdUsuario();
    }

}
