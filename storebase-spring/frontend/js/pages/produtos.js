Pages.produtos = {
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
      const data = await apiFetch(`/produtos?page=${this._page}&size=10&search=${encodeURIComponent(this._search)}`);
      const el = document.getElementById('page-produtos');
      if (!el) return;
      el.innerHTML = `
        <div class="page-topbar">
          <div class="search-wrap">
            <i data-lucide="search"></i>
            <input class="input" id="prod-search" type="text" placeholder="Buscar por nome, código ou categoria..." value="${escHtml(this._search)}">
          </div>
          <button class="btn btn-primary" onclick="Pages.produtos._openForm(null)">
            <i data-lucide="plus"></i> Novo Produto
          </button>
        </div>
        <div class="card" style="padding:0">
          <div class="table-container">
            ${this._tableHTML(data.content)}
          </div>
          ${paginationHTML({ currentPage: data.currentPage, totalPages: data.totalPages, onPage: 'p => { Pages.produtos._page = p; Pages.produtos._render(); }' })}
        </div>
      `;
      const searchInput = document.getElementById('prod-search');
      if (searchInput) {
        const debouncedSearch = debounce(v => { this._search = v; this._page = 0; this._render(); }, 350);
        searchInput.addEventListener('input', e => debouncedSearch(e.target.value));
      }
      lucide.createIcons();
    } catch (err) {
      showToast('Erro ao carregar produtos.', 'error');
    } finally {
      App.setLoading(false);
    }
  },

  _tableHTML(produtos) {
    if (!produtos.length) return `<div class="empty-state"><i data-lucide="package"></i><p>Nenhum produto encontrado.</p></div>`;
    return `
      <table class="table">
        <thead>
          <tr>
            <th>Nome</th>
            <th>Código</th>
            <th>Categoria</th>
            <th>Preço Venda</th>
            <th>Custo</th>
            <th>Estoque</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          ${produtos.map(p => `
            <tr>
              <td>
                <div class="product-name-cell">
                  <i data-lucide="package"></i>
                  <span>${escHtml(p.nome)}</span>
                </div>
              </td>
              <td><span class="text-mono">${escHtml(p.codigo)}</span></td>
              <td>${escHtml(p.categoria)}</td>
              <td class="text-primary text-bold">${fmt(p.precoVenda)}</td>
              <td class="text-muted">${fmt(p.precoCusto)}</td>
              <td>${stockBadgeHTML(p.quantidadeEstoque)}</td>
              <td>
                <div class="actions-cell">
                  <button onclick="Pages.produtos._openForm(${p.id})" title="Editar">
                    <i data-lucide="pencil" style="color:#6c757d"></i>
                  </button>
                  <button onclick="Pages.produtos._delete(${p.id},'${escHtml(p.nome)}')" title="Excluir">
                    <i data-lucide="trash-2" style="color:#dc3545"></i>
                  </button>
                </div>
              </td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    `;
  },

  _openForm(id) {
    App.editingId = id;
    const isEdit = id !== null;
    const p = isEdit ? mockProdutos.find(x => x.id === id) : null;

    openModal({
      title: isEdit ? 'Editar Produto' : 'Novo Produto',
      width: 560,
      contentHTML: `
        <div style="display:grid;grid-template-columns:1fr 1fr;gap:14px">
          <div class="form-group" style="grid-column:1/-1">
            <label>Nome *</label>
            <input class="input" id="prod-nome" type="text" value="${escHtml(p?.nome || '')}" placeholder="Nome do produto">
          </div>
          <div class="form-group">
            <label>Código *</label>
            <input class="input" id="prod-codigo" type="text" value="${escHtml(p?.codigo || '')}" placeholder="Ex: NB-001">
          </div>
          <div class="form-group">
            <label>Categoria</label>
            <input class="input" id="prod-categoria" type="text" value="${escHtml(p?.categoria || '')}" placeholder="Ex: Computadores">
          </div>
          <div class="form-group">
            <label>Preço de Venda (R$) *</label>
            <input class="input" id="prod-preco" type="number" step="0.01" min="0" value="${p?.precoVenda || ''}">
          </div>
          <div class="form-group">
            <label>Preço de Custo (R$)</label>
            <input class="input" id="prod-custo" type="number" step="0.01" min="0" value="${p?.precoCusto || ''}">
          </div>
          <div class="form-group">
            <label>Quantidade em Estoque</label>
            <input class="input" id="prod-qtd" type="number" min="0" value="${p?.quantidadeEstoque ?? 0}">
          </div>
        </div>
      `,
      footerHTML: `
        <button class="btn btn-outline" onclick="closeModal()">Cancelar</button>
        <button class="btn btn-primary" onclick="Pages.produtos._save()">
          ${isEdit ? 'Salvar Alterações' : 'Criar Produto'}
        </button>
      `,
    });
  },

  async _save() {
    const nome      = formValue('prod-nome');
    const codigo    = formValue('prod-codigo');
    const categoria = formValue('prod-categoria');
    const precoVenda= parseFloat(formValue('prod-preco')) || 0;
    const precoCusto= parseFloat(formValue('prod-custo')) || 0;
    const qtd       = parseInt(formValue('prod-qtd')) || 0;

    if (!nome || !codigo || precoVenda <= 0) {
      showToast('Preencha Nome, Código e Preço de Venda.', 'warning');
      return;
    }

    App.setLoading(true);
    try {
      const body = { nome, codigo, categoria, precoVenda, precoCusto, quantidadeEstoque: qtd };
      if (App.editingId) {
        await apiFetch(`/produtos/${App.editingId}`, { method:'PUT', body: JSON.stringify(body) });
        showToast('Produto atualizado com sucesso!', 'success');
      } else {
        await apiFetch('/produtos', { method:'POST', body: JSON.stringify(body) });
        showToast('Produto criado com sucesso!', 'success');
      }
      closeModal();
      this._render();
    } catch (err) {
      showToast(err.message || 'Erro ao salvar produto.', 'error');
    } finally {
      App.setLoading(false);
    }
  },

  _delete(id, nome) {
    confirmDialog({
      title: 'Excluir Produto',
      description: `Tem certeza que deseja excluir "${nome}"? Esta ação não pode ser desfeita.`,
      confirmLabel: 'Excluir',
      danger: true,
      onConfirm: async () => {
        App.setLoading(true);
        try {
          await apiFetch(`/produtos/${id}`, { method:'DELETE' });
          showToast('Produto excluído.', 'success');
          this._render();
        } catch (err) {
          showToast(err.message || 'Erro ao excluir produto.', 'error');
        } finally {
          App.setLoading(false);
        }
      },
    });
  },
};
