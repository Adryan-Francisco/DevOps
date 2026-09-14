// ========== MÁSCARAS ==========
function somenteDigitos(value, limite) {
    return (value || '').replace(/\D/g, '').slice(0, limite);
}

const mascaras = {
    // (00) 0000-0000 ou (00) 00000-0000
    phone(value) {
        const v = somenteDigitos(value, 11);
        if (v.length <= 2) return v;
        if (v.length <= 6) return `(${v.slice(0, 2)}) ${v.slice(2)}`;
        if (v.length <= 10) return `(${v.slice(0, 2)}) ${v.slice(2, 6)}-${v.slice(6)}`;
        return `(${v.slice(0, 2)}) ${v.slice(2, 7)}-${v.slice(7)}`;
    },
    // 000.000.000-00
    cpf(value) {
        const v = somenteDigitos(value, 11);
        if (v.length <= 3) return v;
        if (v.length <= 6) return `${v.slice(0, 3)}.${v.slice(3)}`;
        if (v.length <= 9) return `${v.slice(0, 3)}.${v.slice(3, 6)}.${v.slice(6)}`;
        return `${v.slice(0, 3)}.${v.slice(3, 6)}.${v.slice(6, 9)}-${v.slice(9)}`;
    },
    // 00000-0
    rm(value) {
        const v = somenteDigitos(value, 6);
        return v.length <= 5 ? v : `${v.slice(0, 5)}-${v.slice(5)}`;
    },
    number(value) {
        return somenteDigitos(value, 9);
    }
};

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-mask]').forEach(campo => {
        const mascara = mascaras[campo.dataset.mask];
        if (!mascara) return;
        campo.addEventListener('input', () => { campo.value = mascara(campo.value); });
    });

    // Confirmação antes de ações destrutivas
    document.querySelectorAll('form[data-confirm]').forEach(form => {
        form.addEventListener('submit', event => {
            if (!confirm(form.dataset.confirm)) event.preventDefault();
        });
    });

    // Fechar alertas
    document.querySelectorAll('.alert-close').forEach(botao => {
        botao.addEventListener('click', () => botao.closest('.alert').remove());
    });

    // Menu lateral no celular
    const toggle = document.querySelector('.sidebar-toggle');
    if (toggle) {
        toggle.addEventListener('click', () => {
            const sidebar = toggle.closest('.sidebar');
            const aberto = sidebar.classList.toggle('open');
            toggle.setAttribute('aria-expanded', aberto);
        });
    }
});
