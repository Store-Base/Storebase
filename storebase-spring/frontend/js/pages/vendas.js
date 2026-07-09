Pages.vendas = {
  _filtros: { dataInicio:'', dataFim:'', formaPagamento:'' },

  load() {
    this._filtros = { dataInicio:'', dataFim:'', formaPagamento:'' };
    this._render();
  },

  async _render() {
    App.setLoading(true);
    try {
      const { dataInicio, dataFim, formaPagamento } = this._filtros;
      const params = new URLSearchParams();
      if (dataInicio) params.set('dataInicio', dataInicio);
      if (dataFim)    params.set('dataFim', dataFim);
      if (formaPagamento) params.set('formaPagamento', formaPagamento);

      const data = await apiFetch(`/vendas?${params}`);
      const el = document.getElementById('page-vendas');
      if (!el) return;
      el.innerHTML = `
        ${this._filtrosHTML()}
        ${this._resumoHTML(data.totalVendas, data.receitaPeriodo)}
        <div class="card" style="padding:0">
          <div class="table-container">
            ${this._tableHTML(data.vendas)}
          </div>
        </div>
      `;
      lucide.createIcons();
    } catch (err) {
      showToast('Erro ao carregar vendas.', 'error');
    } finally {
      App.setLoading(false);
    }
  },

  _filtrosHTML() {
    return `
      <div class="card filter-bar" style="margin-bottom:16px">
        <div class="filter-group">
          <label>Data Início</label>
          <input class="input" id="venda-dt-inicio" type="date" value="${this._filtros.dataInicio}">
        </div>
        <div class="filter-group">
          <label>Data Fim</label>
          <input class="input" id="venda-dt-fim" type="date" value="${this._filtros.dataFim}">
        </div>
        <div class="filter-group">
          <label>Forma de Pagamento</label>
          <select class="input" id="venda-forma">
            <option value="">Todas</option>
            <option value="PIX"           ${this._filtros.formaPagamento==='PIX'?'selected':''}>PIX</option>
            <option value="DINHEIRO"      ${this._filtros.formaPagamento==='DINHEIRO'?'selected':''}>Dinheiro</option>
            <option value="CARTAO_CREDITO"${this._filtros.formaPagamento==='CARTAO_CREDITO'?'selected':''}>Cartão Crédito</option>
            <option value="CARTAO_DEBITO" ${this._filtros.formaPagamento==='CARTAO_DEBITO'?'selected':''}>Cartão Débito</option>
            <option value="BOLETO"        ${this._filtros.formaPagamento==='BOLETO'?'selected':''}>Boleto</option>
          </select>
        </div>
        <button class="btn btn-primary" onclick="Pages.vendas._filtrar()">
          <i data-lucide="search"></i> Filtrar
        </button>
        ${App.hasRole('ADMINISTRADOR','VENDEDOR') ? `
          <button class="btn btn-green" onclick="Router.navigate('nova-venda')" style="margin-left:auto">
            <i data-lucide="plus"></i> Nova Venda
          </button>
        ` : ''}
      </div>
    `;
  },

  _resumoHTML(totalVendas, receitaPeriodo) {
    return `
      <div class="grid-2" style="margin-bottom:16px">
        <div class="card">
          <div style="font-size:13px;color:var(--loja-text-muted);margin-bottom:6px">Total de Vendas</div>
          <div style="font-size:28px;font-weight:700;color:var(--loja-text)">${totalVendas}</div>
        </div>
        <div class="card">
          <div style="font-size:13px;color:var(--loja-text-muted);margin-bottom:6px">Receita no Período</div>
          <div style="font-size:28px;font-weight:700;color:var(--loja-primary-text)">${fmt(receitaPeriodo)}</div>
        </div>
      </div>
    `;
  },

  _tableHTML(vendas) {
    if (!vendas.length) return `<div class="empty-state"><i data-lucide="shopping-cart"></i><p>Nenhuma venda encontrada.</p></div>`;
    return `
      <table class="table">
        <thead>
          <tr>
            <th>#</th>
            <th>Data/Hora</th>
            <th>Cliente</th>
            <th>Funcionário</th>
            <th>Total</th>
            <th>Pagamento</th>
            <th>Ação</th>
          </tr>
        </thead>
        <tbody>
          ${vendas.map(v => `
            <tr>
              <td class="text-muted">#${v.id}</td>
              <td>${fmtDataHora(v.dataHora)}</td>
              <td>${escHtml(v.clienteNome)}</td>
              <td>${escHtml(v.funcionarioNome)}</td>
              <td class="text-primary text-bold">${fmt(v.total)}</td>
              <td>${badgeHTML(v.formaPagamento)}</td>
              <td>
                <div class="actions-cell">
                  <button onclick="Pages.vendas._verDetalhe(${v.id})" title="Ver detalhes">
                    <i data-lucide="eye" style="color:var(--loja-text-muted)"></i>
                  </button>
                </div>
              </td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    `;
  },

  _filtrar() {
    this._filtros.dataInicio    = document.getElementById('venda-dt-inicio')?.value || '';
    this._filtros.dataFim       = document.getElementById('venda-dt-fim')?.value || '';
    this._filtros.formaPagamento= document.getElementById('venda-forma')?.value || '';
    this._render();
  },

  async _verDetalhe(id) {
    App.setLoading(true);
    try {
      const v = await apiFetch(`/vendas/${id}`);
      openModal({
        title: `Venda #${v.id}`,
        width: 560,
        contentHTML: `
          <div style="display:flex;flex-direction:column;gap:4px">
            <div class="venda-detail-row"><span class="label">Data/Hora</span><span>${fmtDate(v.dataHora)}</span></div>
            <div class="venda-detail-row"><span class="label">Cliente</span><span>${escHtml(v.clienteNome)}</span></div>
            <div class="venda-detail-row"><span class="label">Funcionário</span><span>${escHtml(v.funcionarioNome)}</span></div>
            <div class="venda-detail-row"><span class="label">Pagamento</span><span>${badgeHTML(v.formaPagamento)}</span></div>
          </div>
          <div style="margin:16px 0 8px;font-weight:600;font-size:13px">Itens</div>
          <table class="table">
            <thead><tr><th>Produto</th><th>Qtd</th><th>Unit.</th><th>Total</th></tr></thead>
            <tbody>
              ${v.itens.map(i => `
                <tr>
                  <td>${escHtml(i.nomeProduto)}</td>
                  <td>${i.quantidade}</td>
                  <td>${fmt(i.precoUnitario)}</td>
                  <td class="text-primary text-bold">${fmt(i.precoUnitario * i.quantidade)}</td>
                </tr>
              `).join('')}
            </tbody>
          </table>
          <div style="text-align:right;margin-top:12px">
            ${v.desconto > 0 ? `<div style="color:var(--loja-text-muted);font-size:13px">Desconto: -${fmt(v.desconto)}</div>` : ''}
            <div style="font-size:18px;font-weight:700;color:var(--loja-primary-text)">Total: ${fmt(v.total)}</div>
            ${v.parcelas > 1 ? `<div style="color:var(--loja-text-muted);font-size:13px;margin-top:2px">${v.parcelas}x de ${fmt(v.valorParcela)}${v.taxaJuros > 0 ? ` · juros ${v.taxaJuros}% por parcela` : ''}</div>` : ''}
          </div>
        `,
        footerHTML: `<button class="btn btn-outline" onclick="closeModal()">Fechar</button>`,
      });
    } catch (err) {
      showToast('Erro ao carregar venda.', 'error');
    } finally {
      App.setLoading(false);
    }
  },
};
