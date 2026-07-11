const App = {
  user: null,     
  page: 'login',
  loading: false,
  editingId: null,

  init() {
    this.initTheme();
    const token = localStorage.getItem('token');
    if (token) {
      this.user = {
        id:    Number(localStorage.getItem('loja_id')),
        nome:  localStorage.getItem('loja_nome'),
        cargo: localStorage.getItem('loja_cargo'),
        token,
      };
      this.page = 'dashboard';
    }
  },

  initTheme() {
    const salvo = localStorage.getItem('loja_tema');
    let tema;
    if (salvo === 'dark' || salvo === 'light') {
      tema = salvo;
    } else {
      tema = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }
    document.documentElement.setAttribute('data-theme', tema);
  },

  toggleTheme() {
    const atual = document.documentElement.getAttribute('data-theme');
    const novo = atual === 'dark' ? 'light' : 'dark';
    document.documentElement.setAttribute('data-theme', novo);
    localStorage.setItem('loja_tema', novo);
    renderSidebar();
    if (['dashboard', 'relatorios'].includes(this.page)) {
      Router.navigate(this.page);
    }
  },

  login(userData) {
    localStorage.setItem('token',     userData.token);
    localStorage.setItem('loja_id',   String(userData.id));
    localStorage.setItem('loja_nome', userData.nome);
    localStorage.setItem('loja_cargo',userData.cargo);
    this.user = userData;
    Router.navigate('dashboard');
  },

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('loja_id');
    localStorage.removeItem('loja_nome');
    localStorage.removeItem('loja_cargo');
    this.user = null;
    Router.navigate('login');
  },

  setLoading(v) {
    this.loading = v;
    const spinner = document.getElementById('global-spinner');
    if (spinner) spinner.style.display = v ? 'flex' : 'none';
  },

  isAdmin()   { return this.user?.cargo?.toUpperCase() === 'ADMINISTRADOR'; },
  isVendedor(){ return this.user?.cargo?.toUpperCase() === 'VENDEDOR'; },
  isEstoque() { return this.user?.cargo?.toUpperCase() === 'GERENTE_ESTOQUE'; },

  hasRole(...roles) { return roles.includes(this.user?.cargo); },
};
