package com.ed.ecommerce.mvcDemo.Controller;

import com.ed.ecommerce.mvcDemo.Model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void agregarAtributosSesion(HttpSession session, Model model) {

        // Recuperar el usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
        }

    }
}
