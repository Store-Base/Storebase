
const MENU_ITEMS = [
  { label:'Dashboard',    page:'dashboard',    icon:'layout-dashboard', roles:['ADMINISTRADOR','VENDEDOR','GERENTE_ESTOQUE'] },
  { label:'Produtos',     page:'produtos',     icon:'package',          roles:['ADMINISTRADOR','GERENTE_ESTOQUE'] },
  { label:'Clientes',     page:'clientes',     icon:'users',            roles:['ADMINISTRADOR','VENDEDOR'] },
  { label:'Funcionários', page:'funcionarios', icon:'user-check',       roles:['ADMINISTRADOR'] },
  { label:'Orçamentos',   page:'orcamentos',   icon:'file-text',        roles:['ADMINISTRADOR','VENDEDOR'] },
  { label:'Vendas',       page:'vendas',       icon:'shopping-cart',    roles:['ADMINISTRADOR','VENDEDOR'] },
  { label:'Estoque',      page:'estoque',      icon:'boxes',            roles:['ADMINISTRADOR','GERENTE_ESTOQUE'] },
  { label:'Relatórios',   page:'relatorios',   icon:'bar-chart-2',      roles:['ADMINISTRADOR'] },
];

function renderSidebar() {
  if (!App.user) return;
  const allowed = MENU_ITEMS.filter(m => m.roles.includes(App.user.cargo));
  const nav = document.getElementById('sidebar-nav');
  if (!nav) return;

  nav.innerHTML = allowed.map(item => `
    <button class="nav-item ${App.page === item.page ? 'active' : ''}"
            onclick="Router.navigate('${item.page}')">
      <i data-lucide="${item.icon}"></i>
      ${escHtml(item.label)}
    </button>
  `).join('');

  const userRole = document.getElementById('sidebar-user-role');
  const userName = document.getElementById('sidebar-user-name');
  if (userRole) userRole.textContent = App.user.cargo.replace(/_/g,' ');
  if (userName) userName.textContent = App.user.nome;

  lucide.createIcons();
}


const BADGE_CONFIG = {
  OK:              { label:'OK',             bg:'#d4edda', color:'#155724' },
  BAIXO:           { label:'Baixo',          bg:'#fff3cd', color:'#856404' },
  CRITICO:         { label:'Crítico',        bg:'#f8d7da', color:'#721c24' },
  ABERTO:          { label:'Aberto',         bg:'#cce5ff', color:'#004085' },
  FECHADO:         { label:'Fechado',        bg:'#e2e3e5', color:'#383d41' },
  ADMINISTRADOR:   { label:'Administrador',  bg:'#e8d5f5', color:'#6f42c1' },
  VENDEDOR:        { label:'Vendedor',       bg:'#cce5ff', color:'#004085' },
  GERENTE_ESTOQUE: { label:'Ger. Estoque',   bg:'#d4edda', color:'#155724' },
  PIX:             { label:'PIX',            bg:'#d4edda', color:'#155724' },
  DINHEIRO:        { label:'Dinheiro',       bg:'#d4edda', color:'#155724' },
  CARTAO_CREDITO:  { label:'Cartão Crédito', bg:'#cce5ff', color:'#004085' },
  CARTAO_DEBITO:   { label:'Cartão Débito',  bg:'#d1ecf1', color:'#0c5460' },
  BOLETO:          { label:'Boleto',         bg:'#fff3cd', color:'#856404' },
};

function badgeHTML(status) {
  const c = BADGE_CONFIG[status] ?? { label: status, bg:'#e2e3e5', color:'#383d41' };
  return `<span class="badge" style="background:${c.bg};color:${c.color}">${escHtml(c.label)}</span>`;
}


function stockBadgeHTML(qty) {
  if (qty >= 20) return `<span class="stock-badge stock-ok">${qty} un.</span>`;
  if (qty >= 10) return `<span class="stock-badge stock-low">${qty} un.</span>`;
  if (qty >= 5)  return `<span class="stock-badge stock-warn">${qty} un.</span>`;
  return `<span class="stock-badge stock-crit">${qty} un.</span>`;
}


function showToast(message, type = 'info') {
  const bg = type === 'success' ? '#28A745'
           : type === 'error'   ? '#DC3545'
           : type === 'warning' ? '#fd7e14' : '#17a2b8';
  Toastify({
    text: message,
    duration: 3500,
    gravity: 'bottom',
    position: 'right',
    stopOnFocus: true,
    style: { background: bg, borderRadius: '8px', fontSize: '13.5px', padding: '10px 16px' },
  }).showToast();
}


function openModal({ id = 'main-modal', title, contentHTML, width = 520, footerHTML = '' }) {
  const dialog = document.getElementById(id);
  if (!dialog) return;
  const titleEl = dialog.querySelector('.modal-title');
  const bodyEl  = dialog.querySelector('.modal-body');
  const footerEl = dialog.querySelector('.modal-footer');
  if (titleEl) titleEl.textContent = title;
  if (bodyEl)  bodyEl.innerHTML = contentHTML;
  if (footerEl && footerHTML !== null) footerEl.innerHTML = footerHTML;
  dialog.style.setProperty('--modal-width', width + 'px');
  dialog.showModal();
  lucide.createIcons();
}

function closeModal(id = 'main-modal') {
  const dialog = document.getElementById(id);
  if (dialog) dialog.close();
}


function confirmDialog({ title, description, onConfirm, confirmLabel = 'Confirmar', danger = false }) {
  const dialog = document.getElementById('confirm-dialog');
  if (!dialog) return;
  dialog.querySelector('.confirm-title').textContent = title;
  dialog.querySelector('.confirm-desc').textContent = description;
  const btn = dialog.querySelector('#confirm-btn');
  btn.textContent = confirmLabel;
  btn.className = danger ? 'btn btn-danger' : 'btn btn-primary';
  btn.onclick = () => { dialog.close(); onConfirm(); };
  dialog.showModal();
}

function paginationHTML({ currentPage, totalPages, onPage }) {
  if (totalPages <= 1) return '';
  const btns = [];

  btns.push(`<button class="page-btn" onclick="(${onPage})(${currentPage - 1})" ${currentPage === 0 ? 'disabled' : ''}>
    <i data-lucide="chevron-left"></i></button>`);

  let start = Math.max(0, currentPage - 2);
  let end   = Math.min(totalPages - 1, currentPage + 2);
  if (start > 0) {
    btns.push(`<button class="page-btn" onclick="(${onPage})(0)">1</button>`);
    if (start > 1) btns.push(`<span class="page-btn" style="border:none;cursor:default">…</span>`);
  }

  for (let i = start; i <= end; i++) {
    btns.push(`<button class="page-btn ${i === currentPage ? 'active' : ''}" onclick="(${onPage})(${i})">${i + 1}</button>`);
  }

  if (end < totalPages - 1) {
    if (end < totalPages - 2) btns.push(`<span class="page-btn" style="border:none;cursor:default">…</span>`);
    btns.push(`<button class="page-btn" onclick="(${onPage})(${totalPages - 1})">${totalPages}</button>`);
  }

  btns.push(`<button class="page-btn" onclick="(${onPage})(${currentPage + 1})" ${currentPage >= totalPages - 1 ? 'disabled' : ''}>
    <i data-lucide="chevron-right"></i></button>`);

  return `<div class="pagination">${btns.join('')}</div>`;
}

function formValue(id) {
  const el = document.getElementById(id);
  return el ? el.value.trim() : '';
}

function setFormValue(id, value) {
  const el = document.getElementById(id);
  if (el) el.value = value ?? '';
}

function clearForm(ids) {
  ids.forEach(id => setFormValue(id, ''));
}
