// Funções compartilhadas por todas as páginas do SeoSong.
// Carregado no <head> SEM "defer", para estar disponível antes dos scripts das páginas.

// Se a página foi aberta pelo próprio Spring (http://localhost:8083/...), usa o mesmo endereço.
// Se foi aberta direto do disco (file://), cai no localhost:8083.
const API_URL = window.location.protocol.startsWith("http")
    ? window.location.origin
    : "http://localhost:8083";

/**
 * fetch + JSON com tratamento de erro.
 * Se o servidor responder com erro, lança um Error com a mensagem que veio em { "error": "..." }.
 */
async function apiFetch(path, options = {}) {
    let response;
    try {
        response = await fetch(API_URL + path, options);
    } catch (e) {
        throw new Error("Não foi possível conectar ao servidor. Verifique se ele está rodando.");
    }

    if (!response.ok) {
        let message = `Erro ${response.status}`;
        try {
            const data = await response.json();
            if (data && (data.error || data.message)) message = data.error || data.message;
        } catch (ignored) { /* resposta sem JSON */ }
        const error = new Error(message);
        error.status = response.status;
        throw error;
    }

    if (response.status === 204) return null;
    return response.json();
}

/** Atalho para enviar JSON (POST/PUT). */
function apiSend(path, method, body) {
    return apiFetch(path, {
        method,
        headers: { "Content-Type": "application/json" },
        body: body === undefined ? undefined : JSON.stringify(body)
    });
}

/** Escapa texto antes de colocar dentro de innerHTML (evita quebrar a página / XSS). */
function esc(value) {
    return String(value ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

/**
 * Formata "2008-04-27" como 27/04/2008.
 * (new Date("2008-04-27") interpreta como meia-noite UTC e, no Brasil, mostrava o dia anterior.)
 */
function formatDate(dateString) {
    if (!dateString) return "-";
    const [y, m, d] = String(dateString).split("-");
    if (!y || !m || !d) return "-";
    return `${d.padStart(2, "0")}/${m.padStart(2, "0")}/${y}`;
}

function placeholderPhoto(name) {
    return "https://ui-avatars.com/api/?background=random&name=" + encodeURIComponent(name || "?");
}

/** Converte "" em null (campos opcionais de formulário). */
function emptyToNull(value) {
    if (value === undefined || value === null) return null;
    const v = String(value).trim();
    return v === "" ? null : v;
}

/** Converte o valor de um <input type="number"> em número ou null. */
function numberOrNull(value) {
    const v = emptyToNull(value);
    if (v === null) return null;
    const n = Number(v);
    return Number.isFinite(n) ? n : null;
}

/**
 * Preenche um <select> com itens da API (mostra o nome, guarda o id).
 *   placeholder: primeira opção vazia ("Escolha...")
 *   empty: texto quando a lista está vazia
 *   selected: id que já deve vir selecionado (ex.: vindo da URL)
 */
function fillOptions(select, items, labelFn, { placeholder, empty, selected } = {}) {
    if (!items || items.length === 0) {
        select.innerHTML = `<option value="">${esc(empty || "Nenhum item cadastrado")}</option>`;
        select.disabled = true;
        return;
    }
    select.disabled = false;
    select.innerHTML =
        (placeholder ? `<option value="">${esc(placeholder)}</option>` : "") +
        items.map(item => `<option value="${esc(item.id)}">${esc(labelFn(item))}</option>`).join("");
    if (selected != null && items.some(i => String(i.id) === String(selected))) {
        select.value = String(selected);
    }
}

/** Rótulo padrão de uma música: "Nome - Artista". */
function songLabel(s) {
    return `${s.name || "Sem nome"}${s.artist && s.artist.name ? " - " + s.artist.name : ""}`;
}

// Imagens quebradas: troca pela imagem do atributo data-fallback (uma única vez).
// Substitui os onerror="..." inline, que quebravam com nomes contendo aspas.
document.addEventListener("error", function (event) {
    const img = event.target;
    if (img && img.tagName === "IMG" && img.dataset.fallback && !img.dataset.fallbackUsed) {
        img.dataset.fallbackUsed = "1";
        img.src = img.dataset.fallback;
    }
}, true);
