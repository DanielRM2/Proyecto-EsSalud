// Función para cambiar entre formularios
document.getElementById('show-register').addEventListener('click', function() {
    document.querySelector('.container').classList.add('rotate');
});

document.getElementById('show-login').addEventListener('click', function() {
    document.querySelector('.container').classList.remove('rotate');
});


// Función para cambiar la imagen activa
let currentImageIndex = 0;  // Índice de la imagen actual
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

// Cambiar la imagen cada 4 segundos
setInterval(nextImage, 4000);