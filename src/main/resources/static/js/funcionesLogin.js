// --------------------------
// FUNCIONES PARA EL CARRUSEL
// --------------------------

let currentImageIndex = 0; // Índice de la imagen actual
const images = document.querySelectorAll('.carousel-item');

// Mostrar la imagen correspondiente al índice seleccionado
function showImage(index) {
    images[currentImageIndex].classList.remove('active');
    currentImageIndex = index;
    images[currentImageIndex].classList.add('active');
}

// Función para cambiar a la siguiente imagen automáticamente
function nextImage() {
    const nextIndex = (currentImageIndex + 1) % images.length;
    showImage(nextIndex);
}

// Iniciar el carrusel automático
function startCarousel() {
    // Cambiar la imagen cada 4 segundos
    setInterval(nextImage, 4000);
}

// -------------------------------
// FUNCIONES PARA CAMBIAR FORMULARIOS
// -------------------------------

function setupFormSwitching() {
    document.getElementById('show-register').addEventListener('click', function(e) {
        e.preventDefault();
        document.querySelector('.container').classList.add('rotate');
    });

    document.getElementById('show-login').addEventListener('click', function(e) {
        e.preventDefault();
        document.querySelector('.container').classList.remove('rotate');
    });
}

// --------------------------
// FUNCIONES PARA VALIDACIÓN DE DNI CON USO DE API
// --------------------------

function setupDniValidation() {
    const dniInput = document.getElementById("dni");
    const nombreInput = document.getElementById("nombre");
    const apellidoInput = document.getElementById("apellido");
    const dniIcon = document.getElementById("dni-icon");
    const registerButton = document.getElementById("register-button");

    const token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImRhbmpvZDcwQGdtYWlsLmNvbSJ9.EjbdfitOYdeOzYSLHwU4o9WFKJTsfO6oARRbmD1pf8Q";

    let timeoutId;

    if (dniInput && nombreInput && apellidoInput && dniIcon && registerButton) {
        dniInput.addEventListener("input", function () {
            const dni = dniInput.value;

            // Limpiar anteriores
            nombreInput.value = "";
            apellidoInput.value = "";
            dniIcon.innerHTML = "";
            registerButton.disabled = true; // Desactiva temporalmente mientras valida

            if (/^\d{8}$/.test(dni)) {
                clearTimeout(timeoutId);
                timeoutId = setTimeout(() => {
                    fetch(`https://dniruc.apisperu.com/api/v1/dni/${dni}?token=${token}`)
                        .then(response => response.json())
                        .then(data => {
                            if (data.success !== false && data.nombres) {
                                nombreInput.value = data.nombres;
                                apellidoInput.value = `${data.apellidoPaterno} ${data.apellidoMaterno}`;
                                dniIcon.innerHTML = '<i style="color:green;" class="fas fa-check-circle"></i>';
                                registerButton.disabled = false; // Activa si la validación fue exitosa
                            } else {
                                dniIcon.innerHTML = '<i style="color:red;" class="fas fa-times-circle"></i>';
                                registerButton.disabled = true;
                            }
                        })
                        .catch(error => {
                            console.error("Error al consultar DNI:", error);
                            dniIcon.innerHTML = '<i style="color:red;" class="fas fa-times-circle"></i>';
                            registerButton.disabled = true;
                        });
                }, 500);
            } else {
                dniIcon.innerHTML = '<i style="color:red;" class="fas fa-times-circle"></i>';
                registerButton.disabled = true;
            }
        });
    }
}


// -------------------------------
// FUNCIONES PARA VALIDACIÓN EN TIEMPO REAL
// -------------------------------

function setupRealTimeValidation() {
    const dniInput = document.getElementById("dni");
    const correoInput = document.getElementById("correo-register");
    const dniError = document.getElementById("dni-error");
    const correoError = document.getElementById("correo-error");
    const registerButton = document.getElementById("register-button");

    if (dniInput && correoInput && dniError && correoError && registerButton) {
        let dniInvalido = false;
        let correoInvalido = false;

        function actualizarEstadoBoton() {
            registerButton.disabled = dniInvalido || correoInvalido;
        }

        // Validar si el DNI ya existe en tiempo real
        dniInput.addEventListener("input", function() {
            const dni = dniInput.value;

            if (dni.length === 8) {
                fetch(`/essalud/validarDni?dni=${dni}`)
                    .then(response => response.json())
                    .then(data => {
                        dniInvalido = data.existe;
                        dniError.style.display = dniInvalido ? 'block' : 'none';
                        actualizarEstadoBoton();
                    })
                    .catch(error => {
                        console.error("Error al validar DNI:", error);
                        dniInvalido = false;
                        actualizarEstadoBoton();
                    });
            } else {
                dniError.style.display = 'none';
                dniInvalido = false;
                actualizarEstadoBoton();
            }
        });

        // Validar si el correo ya existe en tiempo real
        correoInput.addEventListener("input", function() {
            const correo = correoInput.value;

            if (correo.length > 0) {
                fetch(`/essalud/validarCorreo?correo=${correo}`)
                    .then(response => response.json())
                    .then(data => {
                        correoInvalido = data.existe;
                        correoError.style.display = correoInvalido ? 'block' : 'none';
                        actualizarEstadoBoton();
                    })
                    .catch(error => {
                        console.error("Error al validar correo:", error);
                        correoInvalido = false;
                        actualizarEstadoBoton();
                    });
            } else {
                correoError.style.display = 'none';
                correoInvalido = false;
                actualizarEstadoBoton();
            }
        });
    }
}

// -------------------------------
// FUNCIONES PARA MOSTRAR/OCULTAR CONTRASEÑA
// -------------------------------

function setupPasswordToggle() {
    // Inicio de sesión
    const passField = document.getElementById("pass");
    const showBtn = document.querySelector(".login-container .password-label .show-btn i");

    if (showBtn && passField) {
        showBtn.onclick = () => {
            if (passField.type === "password") {
                passField.type = "text";
                showBtn.classList.remove("fa-eye");
                showBtn.classList.add("fa-eye-slash");
            } else {
                passField.type = "password";
                showBtn.classList.remove("fa-eye-slash");
                showBtn.classList.add("fa-eye");
            }
        };
    }

    // Registro
    const registerPassField = document.getElementById("register-pass");
    const registerShowBtn = document.querySelector(".register-container .password-label .show-btn i");

    if (registerShowBtn && registerPassField) {
        registerShowBtn.onclick = () => {
            if (registerPassField.type === "password") {
                registerPassField.type = "text";
                registerShowBtn.classList.remove("fa-eye");
                registerShowBtn.classList.add("fa-eye-slash");
            } else {
                registerPassField.type = "password";
                registerShowBtn.classList.remove("fa-eye-slash");
                registerShowBtn.classList.add("fa-eye");
            }
        };
    }
}

// -------------------------------
// FUNCIONES PARA MENSAJES TEMPORALES
// -------------------------------

function setupMessageTimeouts() {
    const mensajeExitoso = document.querySelector(".success-message");
    if (mensajeExitoso) {
        setTimeout(() => {
            mensajeExitoso.style.display = "none";
        }, 2500);
    }

    const mensajeError = document.querySelector(".error-message");
    if (mensajeError) {
        setTimeout(() => {
            mensajeError.style.display = "none";
        }, 2500);
    }
}

// -------------------------------
// INICIALIZACIÓN DE TODAS LAS FUNCIONES
// -------------------------------

document.addEventListener("DOMContentLoaded", function() {
    setupFormSwitching();      // Para cambiar entre formularios
    startCarousel();           // Para el carrusel automático
    setupDniValidation();      // Para validar DNI y autocompletar
    setupRealTimeValidation(); // Para validaciones en tiempo real
    setupPasswordToggle();     // Para mostrar/ocultar contraseña
    setupMessageTimeouts();    // Para ocultar mensajes automáticamente
});

