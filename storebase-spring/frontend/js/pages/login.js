Pages.login = {
  load() {
    const form = document.getElementById('login-form');
    if (!form) return;

    form.onsubmit = async (e) => {
      e.preventDefault();
      const login = formValue('login-user');
      const senha = formValue('login-pass');
      if (!login || !senha) { showToast('Preencha todos os campos.', 'warning'); return; }

      App.setLoading(true);
      try {
        const data = await apiFetch('/auth/login', {
          method: 'POST',
          body: JSON.stringify({ login, senha }),
        });
        if (data) App.login(data);
      } catch (err) {
        showToast(err.message || 'Erro ao fazer login.', 'error');
      } finally {
        App.setLoading(false);
      }
    };

    
    const toggleBtn = document.getElementById('toggle-pass-btn');
    const passInput = document.getElementById('login-pass');
    if (toggleBtn && passInput) {
      toggleBtn.onclick = () => {
        const isPass = passInput.type === 'password';
        passInput.type = isPass ? 'text' : 'password';
        toggleBtn.innerHTML = isPass
          ? '<i data-lucide="eye-off"></i>'
          : '<i data-lucide="eye"></i>';
        lucide.createIcons();
      };
    }
  },
};
