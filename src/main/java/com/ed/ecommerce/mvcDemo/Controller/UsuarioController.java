package com.ed.ecommerce.mvcDemo.Controller;

import com.ed.ecommerce.mvcDemo.Model.Usuario;
import com.ed.ecommerce.mvcDemo.Services.ServiceUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/essalud")
public class UsuarioController {

    @Autowired
    private ServiceUsuario serviceUsuario;

    private final String RECAPTCHA_SECRET = "6LdkODQrAAAAABmBd6fQPi3QIBUyyL3KIXVNQAsm";
    private final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    // Mostrar formulario de inicio de sesión
    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {
        if (session.getAttribute("usuario") != null) {
            return "index";
        }
        return "login"; // Retorna la vista de login
    }

    // Procesar inicio de sesión
    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam String correo,
            @RequestParam String contrasena,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = serviceUsuario.iniciarSesion(correo, contrasena);
        if (usuario != null) {
            session.setAttribute("usuario", usuario);
            return "redirect:/essalud/index";
        } else {
            redirectAttributes.addFlashAttribute("error", "Correo o contraseña incorrectos");
            return "redirect:/essalud/login";
        }
    }

    @PostMapping("/registrar")
    public String registrarUsuario(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String dni,
            @RequestParam String correo,
            @RequestParam String contrasena,
            @RequestParam("g-recaptcha-response") String captchaResponse,
            RedirectAttributes redirectAttributes) { // Usa RedirectAttributes en lugar de Model

        if (serviceUsuario.verificarDniExistente(dni)) {
            redirectAttributes.addFlashAttribute("error", "El DNI ya está registrado.");
            return "redirect:/essalud/login";
        }

        if (serviceUsuario.verificarCorreoExistente(correo)) {
            redirectAttributes.addFlashAttribute("error", "El correo ya está registrado.");
            return "redirect:/essalud/login";
        }

        Usuario nuevoUsuario = new Usuario(nombre, apellido, dni, correo, contrasena);
        boolean registroExitoso = serviceUsuario.registrar(nuevoUsuario);

        if (registroExitoso) {
            redirectAttributes.addFlashAttribute("registroExitoso", true); // Cambiado aquí
            return "redirect:/essalud/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Hubo un error al registrar.");
            return "redirect:/essalud/login";
        }
    }




    // Metodo privado para validar reCAPTCHA con Google
    private boolean verificarCaptcha(String captchaResponse) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", RECAPTCHA_SECRET);
        params.add("response", captchaResponse);

        ResponseEntity<Map> response = restTemplate.postForEntity(RECAPTCHA_VERIFY_URL, params, Map.class);
        Map<String, Object> body = response.getBody();

        return body != null && (Boolean) body.get("success");
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/essalud/login";
    }

    // Mostrar detalles de la cuenta
    @GetMapping("/cuenta")
    public String mostrarCuenta(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/essalud/login";
        }
        model.addAttribute("usuario", usuario); // <-- enviamos los datos
        return "cuenta";
    }

    // Actualizar perfil de usuario
    @PostMapping("/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuario) {
        serviceUsuario.actualizarDatos(usuario);
        return "redirect:/essalud/cuenta/" + usuario.getIdUsuario();
    }

    // Validar si el DNI está registrado
    @GetMapping("/validarDni")
    @ResponseBody
    public Map<String, Boolean> validarDni(@RequestParam String dni) {
        boolean existe = serviceUsuario.verificarDniExistente(dni);
        return Map.of("existe", existe);
    }

    // Validar si el correo está registrado
    @GetMapping("/validarCorreo")
    @ResponseBody
    public Map<String, Boolean> validarCorreo(@RequestParam String correo) {
        boolean existe = serviceUsuario.verificarCorreoExistente(correo);
        return Map.of("existe", existe);
    }
}
