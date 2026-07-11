Pages.clientes = {
  _page: 0,
  _search: '',

  load() {
    this._page = 0;
    this._search = '';
    this._render();
  },

  async _render() {
    App.setLoading(true);
    try {
      const data = await apiFetch(`/clientes?page=${this._page}&size=10&search=${encodeURIComponent(this._search)}`);
      const el = document.getElementById('page-clientes');
      if (!el) return;
      el.innerHTML = `
        <div class="page-topbar">
          <div class="search-wrap">
            <i data-lucide="search"></i>
            <input class="input" id="cli-search" type="text" placeholder="Buscar por nome, CPF ou email..." value="${escHtml(this._search)}">
          </div>
          <button class="btn btn-primary" onclick="Pages.clientes._openForm(null)">
            <i data-lucide="plus"></i> Novo Cliente
          </button>
        </div>
        <div class="card" style="padding:0">
          <div class="table-container">
            ${this._tableHTML(data.content)}
          </div>
          ${paginationHTML({ currentPage: data.currentPage, totalPages: data.totalPages, onPage: 'p => { Pages.clientes._page = p; Pages.clientes._render(); }' })}
        </div>
      `;
      const searchInput = document.getElementById('cli-search');
      if (searchInput) {
        const debouncedSearch = debounce(v => { this._search = v; this._page = 0; this._render(); }, 350);
        searchInput.addEventListener('input', e => debouncedSearch(e.target.value));
      }
      lucide.createIcons();
    } catch (err) {
      showToast('Erro ao carregar clientes.', 'error');
    } finally {
      App.setLoading(false);
    }
  },

  _tableHTML(clientes) {
    if (!clientes.length) return `<div class="empty-state"><i data-lucide="users"></i><p>Nenhum cliente encontrado.</p></div>`;
    return `
      <table class="table">
        <thead>
          <tr>
            <th>Nome</th>
            <th>CPF</th>
            <th>Email</th>
            <th>Endereço</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          ${clientes.map(c => `
            <tr>
              <td class="text-bold">${escHtml(c.nome)}</td>
              <td><span class="text-mono">${escHtml(c.cpf)}</span></td>
              <td>${escHtml(c.email)}</td>
              <td class="truncate" style="max-width:200px" title="${escHtml(c.endereco)}">${escHtml(truncate(c.endereco, 35))}</td>
              <td>
                <div class="actions-cell">
                  <button onclick="Pages.clientes._verHistorico(${c.id},'${escHtml(c.nome)}')" title="Histórico de compras">
                    <i data-lucide="clock" style="color:var(--loja-text-muted)"></i>
                  </button>
                  <button onclick="Pages.clientes._openForm(${c.id})" title="Editar">
                    <i data-lucide="pencil" style="color:var(--loja-text-muted)"></i>
                  </button>
                  <button onclick="Pages.clientes._delete(${c.id},'${escHtml(c.nome)}')" title="Excluir">
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
    let c = null;
    if (isEdit) {
      try {
        c = await apiFetch(`/clientes/${id}`);
      } catch (err) {
        showToast('Erro ao carregar cliente.', 'error');
        return;
      }
    }

    openModal({
      title: isEdit ? 'Editar Cliente' : 'Novo Cliente',
      width: 560,
      contentHTML: `
        <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px">
          <div class="form-group" style="grid-column:1/-1">
            <label>Nome *</label>
            <input class="input" id="cli-nome" type="text" value="${escHtml(c?.nome || '')}" placeholder="Nome completo">
          </div>
          <div class="form-group">
            <label>CPF *</label>
            <input class="input" id="cli-cpf" type="text" value="${escHtml(c?.cpf || '')}" placeholder="000.000.000-00" maxlength="14">
          </div>
          <div class="form-group">
            <label>Telefone</label>
            <input class="input" id="cli-tel" type="text" value="${escHtml(c?.telefone || '')}" placeholder="(43) 99999-0000">
          </div>
          <div class="form-group" style="grid-column:1/-1">
            <label>Email</label>
            <input class="input" id="cli-email" type="email" value="${escHtml(c?.email || '')}" placeholder="email@exemplo.com">
          </div>
          <div class="form-group" style="grid-column:1/-1">
            <label>Endereço</label>
            <input class="input" id="cli-end" type="text" value="${escHtml(c?.endereco || '')}" placeholder="Rua, número - Bairro">
          </div>
          <div class="form-group" style="grid-column:1/-1">
            <label>Observações</label>
            <textarea class="input" id="cli-obs" rows="3" placeholder="Preferências, combinados, anotações gerais...">${escHtml(c?.observacoes || '')}</textarea>
          </div>
        </div>
      `,
      footerHTML: `
        <button class="btn btn-outline" onclick="closeModal()">Cancelar</button>
        <button class="btn btn-primary" onclick="Pages.clientes._save()">
          ${isEdit ? 'Salvar Alterações' : 'Criar Cliente'}
        </button>
      `,
    });

    const cpfInput = document.getElementById('cli-cpf');
    if (cpfInput) applyCPFMask(cpfInput);
  },

  async _save() {
    const nome        = formValue('cli-nome');
    const cpf         = formValue('cli-cpf');
    const email       = formValue('cli-email');
    const endereco    = formValue('cli-end');
    const telefone    = formValue('cli-tel');
    const observacoes = formValue('cli-obs');

    if (!nome || !cpf) { showToast('Nome e CPF são obrigatórios.', 'warning'); return; }

    App.setLoading(true);
    try {
      const body = { nome, cpf, email, endereco, telefone, observacoes };
      if (App.editingId) {
        await apiFetch(`/clientes/${App.editingId}`, { method:'PUT', body: JSON.stringify(body) });
        showToast('Cliente atualizado com sucesso!', 'success');
      } else {
        await apiFetch('/clientes', { method:'POST', body: JSON.stringify(body) });
        showToast('Cliente criado com sucesso!', 'success');
      }
      closeModal();
      this._render();
    } catch (err) {
      showToast(err.message || 'Erro ao salvar cliente.', 'error');
    } finally {
      App.setLoading(false);
    }
  },

  async _verHistorico(id, nome) {
    App.setLoading(true);
    try {
      const vendas = await apiFetch(`/clientes/${id}/historico`);
      const historicoHTML = vendas.length
        ? vendas.map(v => `
            <div class="historico-item">
              <div class="hist-date">${fmtDate(v.dataHora)} · ${badgeHTML(v.formaPagamento)}</div>
              <div class="hist-total">${fmt(v.total)}</div>
              <div class="hist-items">${v.itens.map(i => `${i.quantidade}x ${escHtml(i.nomeProduto)}`).join(', ')}</div>
            </div>
          `).join('')
        : `<div class="empty-state"><i data-lucide="clock"></i><p>Nenhuma compra registrada.</p></div>`;

      openModal({
        title: `Histórico de Compras — ${nome}`,
        width: 580,
        contentHTML: historicoHTML,
        footerHTML: `<button class="btn btn-outline" onclick="closeModal()">Fechar</button>`,
      });
    } catch (err) {
      showToast('Erro ao carregar histórico.', 'error');
    } finally {
      App.setLoading(false);
    }
  },

  _delete(id, nome) {
    confirmDialog({
      title: 'Excluir Cliente',
      description: `Tem certeza que deseja excluir "${nome}"?`,
      confirmLabel: 'Excluir',
      danger: true,
      onConfirm: async () => {
        App.setLoading(true);
        try {
          await apiFetch(`/clientes/${id}`, { method:'DELETE' });
          showToast('Cliente excluído.', 'success');
          this._render();
        } catch (err) {
          showToast(err.message || 'Erro ao excluir cliente.', 'error');
        } finally {
          App.setLoading(false);
        }
      },
    });
  },
};
