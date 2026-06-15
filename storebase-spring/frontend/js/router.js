const pageLabels = {
  login:        '',
  dashboard:    'Dashboard',
  produtos:     'Produtos',
  clientes:     'Clientes',
  funcionarios: 'Funcionários',
  orcamentos:   'Orçamentos',
  vendas:       'Vendas',
  'nova-venda': 'Nova Venda',
  estoque:      'Estoque',
  relatorios:   'Relatórios',
};

const Router = {
  pages: ['login','dashboard','produtos','clientes','funcionarios',
          'orcamentos','vendas','nova-venda','estoque','relatorios'],

  navigate(page) {
    App.page = page;

    this.pages.forEach(p => {
      const el = document.getElementById('page-' + p);
      if (el) el.style.display = (p === page) ? '' : 'none';
    });

    const titleEl = document.getElementById('header-title');
    if (titleEl) titleEl.textContent = pageLabels[page] ?? '';

    const isAuth = page !== 'login';
    const layout = document.getElementById('main-layout');
    const loginPage = document.getElementById('page-login');

    if (layout)    layout.style.display = isAuth ? 'flex' : 'none';
    if (loginPage) loginPage.style.display = !isAuth ? 'flex' : 'none';

    if (isAuth) renderSidebar();

    if (typeof Pages !== 'undefined' && Pages[page]?.load) {
      
      setTimeout(() => Pages[page].load(), 0);
    }
  },
};
