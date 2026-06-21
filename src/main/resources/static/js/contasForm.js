document.addEventListener('DOMContentLoaded', function() {
    const tipoSelect = document.getElementById('tipo');
    const diaFechamentoGroup = document.getElementById('diaFechamentoGroup');
    const diaFechamentoInput = document.getElementById('diaFechamento');

    const CARTAO_CREDITO_VALUE = 'CARTAO_CREDITO'; // Este é o nome do enum

    function toggleDiaFechamento() {
        if (tipoSelect.value === CARTAO_CREDITO_VALUE) {
            diaFechamentoGroup.style.display = 'block';
            diaFechamentoInput.setAttribute('required', 'true');
        } else {
            diaFechamentoGroup.style.display = 'none';
            diaFechamentoInput.removeAttribute('required');
            diaFechamentoInput.value = '';
        }
    }

    tipoSelect.addEventListener('change', toggleDiaFechamento);
    toggleDiaFechamento();
});