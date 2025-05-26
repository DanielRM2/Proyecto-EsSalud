import { createChat } from 'https://cdn.jsdelivr.net/npm/@n8n/chat/dist/chat.bundle.es.js';

createChat({
    webhookUrl: 'https://n8n-khrf.onrender.com/webhook/7a7171cc-769f-4fa3-a3fa-8ef8f94a4951/chat',
    webhookConfig: {
        method: 'POST',
        headers: {}
    },
    target: '#n8n-chat',
    mode: 'window',
    chatInputKey: 'chatInput',
    chatSessionKey: 'sessionId',
    metadata: {},
    showWelcomeScreen: false,
    defaultLanguage: 'es',
    initialMessages: [
        '¡Hola! Bienvenido a ESSALUD ¿Tienes alguna duda o consulta?',
    ],

    i18n: {
        es: {
            title: 'ESSI IA',
            subtitle: "Inicia una conversación. Estamos aquí para ayudarte 24/7.",
            footer: '',
            getStarted: 'Nueva Conversación',
            inputPlaceholder: 'Escribe tu pregunta ...',
        },
    },
});


document.addEventListener('DOMContentLoaded', function() {
    // Espera a que el chat se cargue
    setTimeout(() => {
        const chatButton = document.querySelector('.chat-window-toggle');
        if (chatButton) {
            // Oculta el SVG original
            const svg = chatButton.querySelector('svg');
            if (svg) svg.style.display = 'none';

            // Crea y añade la imagen
            const img = document.createElement('img');
            img.src = '/images/chat-icon.png';
            img.style.width = '60%';
            img.style.height = '60%';
            img.style.objectFit = 'contain';
            chatButton.appendChild(img);
        }
    }, 100); // Delay para asegurar que el chat se haya cargado
});

document.addEventListener('DOMContentLoaded', function () {
    const interval = setInterval(() => {
        const chatToggle = document.querySelector('.chat-window-toggle');

        if (chatToggle && !document.getElementById('chat-label-msg')) {
            // Crear el mensaje oculto
            const msg = document.createElement('div');
            msg.id = 'chat-label-msg';
            msg.innerText = '¿Tienes alguna consulta?';
            msg.style.position = 'fixed';
            msg.style.bottom = '50px'; // ajusta según sea necesario
            msg.style.right = '150px';   // ajusta según sea necesario
            msg.style.background = '#ffffff';
            msg.style.padding = '8px 12px';
            msg.style.borderRadius = '12px';
            msg.style.boxShadow = '0 2px 6px rgba(0,0,0,0.2)';
            msg.style.fontFamily = 'sans-serif';
            msg.style.fontSize = '16px';
            msg.style.fontWeight = '700';
            msg.style.zIndex = '9999';
            msg.style.display = 'none'; // oculto por defecto

            document.body.appendChild(msg);

            // Mostrar el mensaje cuando el mouse entra al botón
            chatToggle.addEventListener('mouseenter', () => {
                msg.style.display = 'block';
            });

            // Ocultar el mensaje cuando el mouse sale del botón
            chatToggle.addEventListener('mouseleave', () => {
                msg.style.display = 'none';
            });

            clearInterval(interval);
        }
    }, 500);
});