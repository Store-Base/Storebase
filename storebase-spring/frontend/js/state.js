const App = {
  user: null,     
  page: 'login',
  loading: false,
  editingId: null,

  init() {
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

  login(userData) {
    localStorage.setItem('token',     userData.token);
    localStorage.setItem('loja_id',   String(userData.id));
    localStorage.setItem('loja_nome', userData.nome);
    localStorage.setItem('loja_cargo',userData.cargo);
    this.user = userData;
    Router.navigate('dashboard');
  },

  logout() {
    localStorage.clear();
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
