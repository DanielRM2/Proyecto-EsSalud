const fechaCitaInput = document.getElementById('fechaCita');
if (fechaCitaInput) {
    fechaCitaInput.addEventListener('change', function() {
        const fechaSeleccionada = new Date(this.value);
        const ahora = new Date();

        if (fechaSeleccionada < ahora) {
            alert('No puedes agendar citas en fechas pasadas');
            this.value = '';
        }
    });
}

 document.addEventListener("DOMContentLoaded", function () {
        flatpickr(".fecha-input", {
            locale: "es",
            enableTime: true,
            dateFormat: "Y-m-d\\TH:i", // para LocalDateTime
            time_24hr: true,
            minDate: "today"
        });
    });