document.addEventListener('DOMContentLoaded', function() {
    // Seleciona todas as linhas da tabela que têm a classe 'clickable-row'
    const rows = document.querySelectorAll('.clickable-row');

    // Para cada linha encontrada...
    rows.forEach(row => {
        // Adiciona um 'ouvinte de evento' para o clique na linha
        row.addEventListener('click', function(event) {
            // Verifica se o clique ocorreu em um link (botão Editar/Excluir) dentro da linha.
            // event.target é o elemento específico que foi clicado.
            // event.target.tagName === 'A' verifica se o elemento clicado é uma tag <a>.
            // event.target.closest('a') verifica se o elemento clicado (ou qualquer um de seus ancestrais) é uma tag <a>.
            if (event.target.tagName === 'A' || event.target.closest('a')) {
                // Se o clique foi em um link, ele não interrompe o evento,
                // deixando o link funcionar normalmente.
                event.stopPropagation(); // Impede que o evento de clique na linha seja "propagado" para cima.
                return;
            }

            // AQUI ESTÁ A MUDANÇA: Pega a URL de redirecionamento do novo atributo 'data-redirect-url'
            const redirectUrl = this.dataset.redirectUrl;

            // Se a URL de redirecionamento foi encontrada...
            if (redirectUrl) {
                // Redireciona o navegador para a URL completa obtida do HTML
                window.location.href = redirectUrl;
            }
        });
    });
});