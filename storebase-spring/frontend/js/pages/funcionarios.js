Pages.funcionarios = {
  load() { this._render(); },

  async _render() {
    App.setLoading(true);
    try {
      const funcionarios = await apiFetch('/funcionarios');
      const el = document.getElementById('page-funcionarios');
      if (!el) return;
      el.innerHTML = `
        <div class="page-topbar">
          <div></div>
          <button class="btn btn-primary" onclick="Pages.funcionarios._openForm(null)">
            <i data-lucide="plus"></i> Novo Funcionário
          </button>
        </div>
        <div class="card" style="padding:0">
          <div class="table-container">
            ${this._tableHTML(funcionarios)}
          </div>
        </div>
      `;
      lucide.createIcons();
    } catch (err) {
      showToast('Erro ao carregar funcionários.', 'error');
    } finally {
      App.setLoading(false);
    }
  },

  _tableHTML(funcionarios) {
    if (!funcionarios.length) return `<div class="empty-state"><i data-lucide="user-check"></i><p>Nenhum funcionário cadastrado.</p></div>`;
    return `
      <table class="table">
        <thead>
          <tr>
            <th>Nome</th>
            <th>Cargo</th>
            <th>Salário</th>
            <th>Login</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          ${funcionarios.map(f => `
            <tr>
              <td>${escHtml(f.nome)}</td>
              <td>${badgeHTML(f.cargo)}</td>
              <td>${fmt(f.salario)}</td>
              <td><span class="text-mono">${escHtml(f.login)}</span></td>
              <td>
                <div class="actions-cell">
                  <button onclick="Pages.funcionarios._openForm(${f.id})" title="Editar">
                    <i data-lucide="pencil" style="color:var(--loja-text-muted)"></i>
                  </button>
                  <button onclick="Pages.funcionarios._delete(${f.id},'${escHtml(f.nome)}')" title="Excluir">
                    <i data-lucide="trash-2" style="color:var(--loja-error-text)"></i>
                  </button>
                </div>
              </td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    `;
  },

  async _openForm(id) {
    App.editingId = id;
    const isEdit = id !== null;
    let f = null;
    if (isEdit) {
      try {
        f = await apiFetch(`/funcionarios/${id}`);
      } catch (err) {
        showToast('Erro ao carregar funcionário.', 'error');
        return;
      }
    }

    openModal({
      title: isEdit ? 'Editar Funcionário' : 'Novo Funcionário',
      width: 520,
      contentHTML: `
        <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px">
          <div class="form-group" style="grid-column:1/-1">
            <label>Nome *</label>
            <input class="input" id="func-nome" type="text" value="${escHtml(f?.nome || '')}" placeholder="Nome completo">
          </div>
          <div class="form-group">
            <label>Cargo *</label>
            <select class="input" id="func-cargo">
              <option value="ADMINISTRADOR"   ${f?.cargo==='ADMINISTRADOR'?'selected':''}>Administrador</option>
              <option value="VENDEDOR"        ${f?.cargo==='VENDEDOR'?'selected':''}>Vendedor</option>
              <option value="GERENTE_ESTOQUE" ${f?.cargo==='GERENTE_ESTOQUE'?'selected':''}>Gerente de Estoque</option>
            </select>
          </div>
          <div class="form-group">
            <label>Salário (R$)</label>
            <input class="input" id="func-salario" type="number" step="0.01" min="0" value="${f?.salario || ''}">
          </div>
          <div class="form-group">
            <label>Login *</label>
            <input class="input" id="func-login" type="text" value="${escHtml(f?.login || '')}" placeholder="usuario.nome">
          </div>
          <div class="form-group">
            <label>Senha ${isEdit ? '(deixe em branco para manter)' : '*'}</label>
            <input class="input" id="func-senha" type="password" placeholder="${isEdit ? '••••••••' : 'Nova senha'}">
          </div>
        </div>
      `,
      footerHTML: `
        <button class="btn btn-outline" onclick="closeModal()">Cancelar</button>
        <button class="btn btn-primary" onclick="Pages.funcionarios._save()">
          ${isEdit ? 'Salvar Alterações' : 'Criar Funcionário'}
        </button>
      `,
    });
  },

  async _save() {
    const nome    = formValue('func-nome');
    const cargo   = formValue('func-cargo');
    const salario = parseFloat(formValue('func-salario')) || 0;
    const login   = formValue('func-login');
    const senha   = formValue('func-senha');

    if (!nome || !cargo || !login) { showToast('Nome, Cargo e Login são obrigatórios.', 'warning'); return; }
    if (!App.editingId && !senha) { showToast('Senha é obrigatória para novo funcionário.', 'warning'); return; }

    App.setLoading(true);
    try {
      const body = { nome, cargo, salario, login };
      if (senha) body.senha = senha;
      if (App.editingId) {
        await apiFetch(`/funcionarios/${App.editingId}`, { method:'PUT', body: JSON.stringify(body) });
        showToast('Funcionário atualizado com sucesso!', 'success');
      } else {
        await apiFetch('/funcionarios', { method:'POST', body: JSON.stringify(body) });
        showToast('Funcionário criado com sucesso!', 'success');
      }
      closeModal();
      this._render();
    } catch (err) {
      showToast(err.message || 'Erro ao salvar funcionário.', 'error');
    } finally {
      App.setLoading(false);
    }
  },

  _delete(id, nome) {
    if (id === App.user?.id) { showToast('Você não pode excluir o seu próprio cadastro.', 'warning'); return; }
    confirmDialog({
      title: 'Excluir Funcionário',
      description: `Tem certeza que deseja excluir "${nome}"?`,
      confirmLabel: 'Excluir',
      danger: true,
      onConfirm: async () => {
        App.setLoading(true);
        try {
          await apiFetch(`/funcionarios/${id}`, { method:'DELETE' });
          showToast('Funcionário excluído.', 'success');
          this._render();
        } catch (err) {
          showToast(err.message || 'Erro ao excluir funcionário.', 'error');
        } finally {
          App.setLoading(false);
        }
      },
    });
  },
};
