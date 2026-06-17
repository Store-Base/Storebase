Pages.orcamentos = {
  _status: '',

  load() {
    this._status = '';
    this._render();
  },

  async _render() {
    App.setLoading(true);
    try {
      const orcamentos = await apiFetch(`/orcamentos?status=${this._status}`);
      const el = document.getElementById('page-orcamentos');
      if (!el) return;
      el.innerHTML = `
        <div class="page-topbar">
          <select class="input" id="orc-status-filter" style="max-width:220px" onchange="Pages.orcamentos._filterChange()">
            <option value="">Todos os status</option>
            <option value="ABERTO"  ${this._status==='ABERTO'?'selected':''}>Aberto</option>
            <option value="FECHADO" ${this._status==='FECHADO'?'selected':''}>Fechado</option>
          </select>
          <button class="btn btn-primary" onclick="Pages.orcamentos._openForm(null)">
            <i data-lucide="plus"></i> Novo Orçamento
          </button>
        </div>
        <div class="card" style="padding:0">
          <div class="table-container">
            ${this._tableHTML(orcamentos)}
          </div>
        </div>
      `;
      lucide.createIcons();
    } catch (err) {
      showToast('Erro ao carregar orçamentos.', 'error');
    } finally {
      App.setLoading(false);
    }
  },

  _filterChange() {
    this._status = document.getElementById('orc-status-filter')?.value || '';
    this._render();
  },

  _tableHTML(orcamentos) {
    if (!orcamentos.length) return `<div class="empty-state"><i data-lucide="file-text"></i><p>Nenhum orçamento encontrado.</p></div>`;
    return `
      <table class="table">
        <thead>
          <tr>
            <th>#</th>
            <th>Data</th>
            <th>Cliente</th>
            <th>Itens</th>
            <th>Total</th>
            <th>Status</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          ${orcamentos.map(o => `
            <tr>
              <td class="text-muted">#${o.id}</td>
              <td>${fmtDateOnly(o.data)}</td>
              <td>${escHtml(o.clienteNome)}</td>
              <td class="text-muted">${o.itens.length} item(s)</td>
              <td class="text-primary text-bold">${fmt(o.total)}</td>
              <td>${badgeHTML(o.status)}</td>
              <td>${this._actionsHTML(o)}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    `;
  },

  _actionsHTML(orc) {
    if (orc.status === 'FECHADO') return '<span style="color:#999;font-size:13px">—</span>';
    return `
      <div class="actions-cell">
        <button onclick="Pages.orcamentos._openForm(${orc.id})" title="Editar">
          <i data-lucide="pencil" style="color:#6c757d"></i>
        </button>
        <button onclick="Pages.orcamentos._converter(${orc.id})" title="Converter em Venda" style="color:#28a745">
          <i data-lucide="circle-plus" style="color:#28a745"></i>
        </button>
        <button onclick="Pages.orcamentos._delete(${orc.id})" title="Excluir">
          <i data-lucide="trash-2" style="color:#dc3545"></i>
        </button>
      </div>
    `;
  },

  async _openForm(id) {
    App.editingId = id;
    const isEdit = id !== null;

    App.setLoading(true);
    let clientes = [];
    let produtos  = [];
    let orc       = null;
    try {
      [clientes, produtos] = await Promise.all([
        apiFetch('/clientes'),
        apiFetch('/produtos'),
      ]);
      if (clientes && clientes.content) clientes = clientes.content;
      if (produtos && produtos.content)  produtos  = produtos.content;
      if (isEdit) orc = await apiFetch(`/orcamentos/${id}`);
    } catch (err) {
      showToast('Erro ao carregar dados do formulário.', 'error');
      App.setLoading(false);
      return;
    } finally {
      App.setLoading(false);
    }

    const clienteOptions = clientes.map(c =>
      `<option value="${c.id}" data-nome="${escHtml(c.nome)}" ${orc?.clienteId === c.id ? 'selected' : ''}>${escHtml(c.nome)}</option>`
    ).join('');

    const produtoOptions = produtos.map(p =>
      `<option value="${p.id}" data-nome="${escHtml(p.nome)}" data-preco="${p.precoVenda}">${escHtml(p.nome)} — ${fmt(p.precoVenda)}</option>`
    ).join('');

    const itensHTML = orc ? orc.itens.map((item, idx) => this._itemRowHTML(item, idx)).join('') : '';

    openModal({
      id: 'main-modal',
      title: isEdit ? 'Editar Orçamento' : 'Novo Orçamento',
      width: 640,
      contentHTML: `
        <div class="form-group" style="margin-bottom:16px">
          <label>Cliente *</label>
          <select class="input" id="orc-cliente">${clienteOptions}</select>
        </div>
        <div style="margin-bottom:8px;font-weight:600;font-size:13px">Itens do Orçamento</div>
        <div id="orc-itens">${itensHTML}</div>
        <div style="display:flex;gap:8px;margin:10px 0 16px;align-items:flex-end">
          <div class="form-group" style="flex:1">
            <label>Produto</label>
            <select class="input" id="orc-novo-produto">${produtoOptions}</select>
          </div>
          <div class="form-group" style="width:80px">
            <label>Qtd</label>
            <input class="input" id="orc-novo-qtd" type="number" min="1" value="1">
          </div>
          <button class="btn btn-outline" onclick="Pages.orcamentos._addItem()">
            <i data-lucide="plus"></i> Add
          </button>
        </div>
        <div id="orc-total-line" style="text-align:right;font-weight:700;font-size:15px;color:var(--loja-primary)"></div>
      `,
      footerHTML: `
        <button class="btn btn-outline" onclick="closeModal()">Cancelar</button>
        <button class="btn btn-primary" onclick="Pages.orcamentos._save()">
          ${isEdit ? 'Salvar Alterações' : 'Criar Orçamento'}
        </button>
      `,
    });

    this._orcItens = orc ? orc.itens.map(item => ({
      produtoId:     item.produto?.id || item.produtoId,
      nomeProduto:   item.produto?.nome || item.nomeProduto || '',
      quantidade:    item.quantidade,
      precoUnitario: item.precoUnitario,
    })) : [];
    this._updateTotal();
  },

  _orcItens: [],

  _itemRowHTML(item, idx) {
    return `
      <div class="cart-item" id="orc-item-${idx}">
        <span class="cart-item-name">${escHtml(item.nomeProduto)}</span>
        <span class="cart-item-price">${fmt(item.precoUnitario)} × ${item.quantidade}</span>
        <span class="cart-item-total">${fmt(item.precoUnitario * item.quantidade)}</span>
        <button class="cart-item-remove" onclick="Pages.orcamentos._removeItem(${idx})">
          <i data-lucide="x"></i>
        </button>
      </div>
    `;
  },

  _addItem() {
    const prodSel = document.getElementById('orc-novo-produto');
    const qtdEl   = document.getElementById('orc-novo-qtd');
    if (!prodSel || !qtdEl) return;
    const opt = prodSel.options[prodSel.selectedIndex];
    const produtoId = Number(prodSel.value);
    const nomeProduto = opt.dataset.nome;
    const precoUnitario = parseFloat(opt.dataset.preco);
    const quantidade = parseInt(qtdEl.value) || 1;

    const existing = this._orcItens.findIndex(i => i.produtoId === produtoId);
    if (existing >= 0) {
      this._orcItens[existing].quantidade += quantidade;
    } else {
      this._orcItens.push({ produtoId, nomeProduto, quantidade, precoUnitario });
    }
    this._refreshItens();
    qtdEl.value = 1;
  },

  _removeItem(idx) {
    this._orcItens.splice(idx, 1);
    this._refreshItens();
  },

  _refreshItens() {
    const container = document.getElementById('orc-itens');
    if (container) {
      container.innerHTML = this._orcItens.map((item, idx) => this._itemRowHTML(item, idx)).join('');
    }
    this._updateTotal();
    lucide.createIcons();
  },

  _updateTotal() {
    const total = this._orcItens.reduce((s, i) => s + i.precoUnitario * i.quantidade, 0);
    const el = document.getElementById('orc-total-line');
    if (el) el.textContent = `Total: ${fmt(total)}`;
  },

  async _save() {
    const clienteEl = document.getElementById('orc-cliente');
    if (!clienteEl) return;
    const clienteId = Number(clienteEl.value);
    const opt = clienteEl.options[clienteEl.selectedIndex];
    const clienteNome = opt?.dataset.nome || opt?.text || '';

    if (!clienteId) { showToast('Selecione um cliente.', 'warning'); return; }
    if (!this._orcItens.length) { showToast('Adicione ao menos um item.', 'warning'); return; }

    const total = this._orcItens.reduce((s, i) => s + i.precoUnitario * i.quantidade, 0);
    const body = { clienteId, clienteNome, usuarioId: App.user?.id || 1, itens: this._orcItens, total };

    App.setLoading(true);
    try {
      if (App.editingId) {
        await apiFetch(`/orcamentos/${App.editingId}`, { method:'PUT', body: JSON.stringify(body) });
        showToast('Orçamento atualizado!', 'success');
      } else {
        await apiFetch('/orcamentos', { method:'POST', body: JSON.stringify(body) });
        showToast('Orçamento criado!', 'success');
      }
      closeModal();
      this._render();
    } catch (err) {
      showToast(err.message || 'Erro ao salvar orçamento.', 'error');
    } finally {
      App.setLoading(false);
    }
  },

  _converter(id) {
    confirmDialog({
      title: 'Converter em Venda',
      description: 'Deseja converter este orçamento em uma venda? O orçamento será fechado.',
      confirmLabel: 'Converter',
      onConfirm: async () => {
        App.setLoading(true);
        try {
          await apiFetch(`/orcamentos/${id}/converter`, { method:'POST', body: JSON.stringify({ formaPagamento:'PIX' }) });
          showToast('Orçamento convertido em venda!', 'success');
          this._render();
        } catch (err) {
          showToast(err.message || 'Erro ao converter orçamento.', 'error');
        } finally {
          App.setLoading(false);
        }
      },
    });
  },

  _delete(id) {
    confirmDialog({
      title: 'Excluir Orçamento',
      description: `Tem certeza que deseja excluir o orçamento #${id}?`,
      confirmLabel: 'Excluir',
      danger: true,
      onConfirm: async () => {
        App.setLoading(true);
        try {
          await apiFetch(`/orcamentos/${id}`, { method:'DELETE' });
          showToast('Orçamento excluído.', 'success');
          this._render();
        } catch (err) {
          showToast(err.message || 'Erro ao excluir orçamento.', 'error');
        } finally {
          App.setLoading(false);
        }
      },
    });
  },
};
