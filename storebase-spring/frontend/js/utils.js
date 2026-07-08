
function fmt(v) {
  return Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}


function fmtDate(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleString('pt-BR', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  });
}


function fmtDateOnly(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleDateString('pt-BR');
}


function fmtDataHora(iso) {
  if (!iso) return '—';
  const d = new Date(iso);
  const data = d.toLocaleDateString('pt-BR');
  const hora = d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  return `<div style="font-weight:600">${data}</div><div style="font-size:12px;color:var(--loja-text-muted)">${hora}</div>`;
}

function maskCPF(value) {
  return value.replace(/\D/g, '').slice(0, 11)
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})\.(\d{3})(\d)/, '$1.$2.$3')
    .replace(/(\d{3})\.(\d{3})\.(\d{3})(\d)/, '$1.$2.$3-$4');
}


function debounce(fn, ms = 300) {
  let t;
  return (...args) => {
    clearTimeout(t);
    t = setTimeout(() => fn(...args), ms);
  };
}


function truncate(str, n = 40) {
  if (!str) return '';
  return str.length > n ? str.slice(0, n) + '…' : str;
}


function fmtK(v) {
  return v >= 1000 ? 'R$' + (v / 1000).toFixed(1) + 'k' : 'R$' + v;
}


function todayISO() {
  return new Date().toISOString().slice(0, 10);
}


function applyCPFMask(input) {
  input.addEventListener('input', () => {
    input.value = maskCPF(input.value);
  });
}


function chartThemeOptions() {
  const css = getComputedStyle(document.documentElement);
  return {
    text:    css.getPropertyValue('--loja-text').trim(),
    muted:   css.getPropertyValue('--loja-text-muted').trim(),
    grid:    css.getPropertyValue('--loja-card-border').trim(),
    primary: css.getPropertyValue('--loja-primary-text').trim(),
  };
}


function escHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}
