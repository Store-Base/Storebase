Pages.relatorios = {
  _tab: 'vendas',
  _charts: {},
  _filtros: { dataInicio: '2026-01-01', dataFim: '2026-12-31', ano: 2026 },

  load() {
    const el = document.getElementById('page-relatorios');
    if (!el) return;

    if (!App.isAdmin()) {
      el.innerHTML = `
        <div class="access-denied">
          <i data-lucide="shield-alert"></i>
          <h2>Acesso Negado</h2>
          <p>Esta seção é restrita ao perfil <strong>Administrador</strong>. Entre em contato com seu gestor.</p>
        </div>
      `;
      lucide.createIcons();
      return;
    }

    this._tab = 'vendas';
    this._renderShell();
    this._loadTab('vendas');
  },

  _destroyCharts() {
    Object.values(this._charts).forEach(c => { try { c.destroy(); } catch {} });
    this._charts = {};
  },

  _renderShell() {
    const el = document.getElementById('page-relatorios');
    if (!el) return;
    const tabs = [
      { key:'vendas',      label:'Vendas'         },
      { key:'produtos',    label:'Mais Vendidos'  },
      { key:'estoque',     label:'Estoque'        },
      { key:'clientes',    label:'Clientes'       },
      { key:'faturamento', label:'Faturamento'    },
    ];
    el.innerHTML = `
      <div class="card filter-bar" style="margin-bottom:20px">
        <div class="filter-group">
          <label>Data Início</label>
          <input class="input" id="rel-dt-inicio" type="date" value="${this._filtros.dataInicio}">
        </div>
        <div class="filter-group">
          <label>Data Fim</label>
          <input class="input" id="rel-dt-fim" type="date" value="${this._filtros.dataFim}">
        </div>
        <div class="filter-group">
          <label>Ano</label>
          <input class="input" id="rel-ano" type="number" value="${this._filtros.ano}" style="width:90px">
        </div>
        <button class="btn btn-primary" onclick="Pages.relatorios._aplicarFiltros()">
          <i data-lucide="search"></i> Aplicar
        </button>
        <button class="btn btn-outline-primary" onclick="exportarPDF()" style="margin-left:auto">
          <i data-lucide="printer"></i> Exportar PDF
        </button>
      </div>
      <div class="tab-bar">
        ${tabs.map(t => `
          <button class="tab-btn ${this._tab === t.key ? 'active' : ''}" data-tab="${t.key}"
            onclick="Pages.relatorios._switchTab('${t.key}')">
            ${t.label}
          </button>
        `).join('')}
      </div>
      <div id="rel-panel"></div>
    `;
    lucide.createIcons();
  },

  _switchTab(key) {
    this._tab = key;
    document.querySelectorAll('#page-relatorios .tab-btn').forEach(btn => {
      btn.classList.toggle('active', btn.dataset.tab === key);
    });
    this._loadTab(key);
  },

  _aplicarFiltros() {
    this._filtros.dataInicio = document.getElementById('rel-dt-inicio')?.value || '';
    this._filtros.dataFim    = document.getElementById('rel-dt-fim')?.value || '';
    this._filtros.ano        = parseInt(document.getElementById('rel-ano')?.value) || 2026;
    this._loadTab(this._tab);
  },

  async _loadTab(key) {
    this._destroyCharts();
    const panel = document.getElementById('rel-panel');
    if (!panel) return;
    App.setLoading(true);
    try {
      switch (key) {
        case 'vendas':      await this._tabVendas(panel); break;
        case 'produtos':    await this._tabProdutos(panel); break;
        case 'estoque':     await this._tabEstoque(panel); break;
        case 'clientes':    await this._tabClientes(panel); break;
        case 'faturamento': await this._tabFaturamento(panel); break;
      }
      lucide.createIcons();
    } catch (err) {
      panel.innerHTML = `<div class="empty-state"><p>Erro ao carregar relatório.</p></div>`;
    } finally {
      App.setLoading(false);
    }
  },

  async _tabVendas(panel) {
    const data = await apiFetch(`/relatorios/vendas?dataInicio=${this._filtros.dataInicio}&dataFim=${this._filtros.dataFim}`);
    const formaKeys = Object.keys(data.porPagamento || {});
    panel.innerHTML = `
      <div class="grid-2" style="margin-bottom:20px">
        <div class="kpi-card">
          <div class="kpi-label">Total de Vendas</div>
          <div class="kpi-value">${data.quantidadeTotal}</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-label">Receita Total</div>
          <div class="kpi-value primary">${fmt(data.totalGeral)}</div>
        </div>
      </div>
      ${formaKeys.length ? `
        <div class="card" style="margin-bottom:20px">
          <div class="chart-card-title"><i data-lucide="pie-chart"></i> Por Forma de Pagamento</div>
          <table class="table">
            <thead><tr><th>Forma</th><th>Total</th></tr></thead>
            <tbody>
              ${formaKeys.map(k => `
                <tr>
                  <td>${badgeHTML(k)}</td>
                  <td class="text-primary text-bold">${fmt(data.porPagamento[k])}</td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
      ` : ''}
    `;
  },

  async _tabProdutos(panel) {
    const produtos = await apiFetch(`/relatorios/produtos?dataInicio=${this._filtros.dataInicio}&dataFim=${this._filtros.dataFim}`);
    panel.innerHTML = `
      <div class="card" style="margin-bottom:20px">
        <div class="chart-card-title"><i data-lucide="bar-chart-2"></i> Produtos Mais Vendidos</div>
        <canvas id="chart-mais-vendidos" height="200"></canvas>
      </div>
      <div class="card" style="padding:0">
        <div class="table-container">
          <table class="table">
            <thead><tr><th>Produto</th><th>Qtd. Vendida</th></tr></thead>
            <tbody>
              ${produtos.map(p => `
                <tr>
                  <td>${escHtml(p.produto)}</td>
                  <td class="text-primary text-bold">${p.quantidadeVendida}</td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
      </div>
    `;
    const canvas = document.getElementById('chart-mais-vendidos');
    if (canvas && produtos.length) {
      const theme = chartThemeOptions();
      this._charts.maisVendidos = new Chart(canvas, {
        type: 'bar',
        data: {
          labels: produtos.map(p => p.produto),
          datasets: [{ label: 'Quantidade Vendida', data: produtos.map(p => p.quantidadeVendida), backgroundColor: theme.primary }],
        },
        options: {
          indexAxis: 'y',
          responsive: true,
          plugins: { legend: { display: false } },
          scales: {
            x: { ticks: { color: theme.muted }, grid: { color: theme.grid } },
            y: { ticks: { color: theme.muted }, grid: { color: theme.grid } },
          },
        },
      });
    }
  },

  async _tabEstoque(panel) {
    const items = await apiFetch('/relatorios/estoque');
    const ok = items.filter(i => i.status === 'OK').length;
    const baixo = items.filter(i => i.status === 'BAIXO').length;
    const critico = items.filter(i => i.status === 'CRITICO').length;
    panel.innerHTML = `
      <div class="grid-3" style="margin-bottom:20px">
        <div class="kpi-card"><div class="kpi-label">Estoque OK</div><div class="kpi-value" style="color:var(--loja-success-text)">${ok}</div></div>
        <div class="kpi-card"><div class="kpi-label">Estoque Baixo</div><div class="kpi-value" style="color:var(--loja-warning-text)">${baixo}</div></div>
        <div class="kpi-card"><div class="kpi-label">Estoque Crítico</div><div class="kpi-value" style="color:var(--loja-error-text)">${critico}</div></div>
      </div>
      <div class="card" style="padding:0">
        <div class="table-container">
          <table class="table">
            <thead><tr><th>Produto</th><th>Código</th><th>Quantidade</th><th>Status</th></tr></thead>
            <tbody>
              ${items.map(i => `
                <tr>
                  <td>${escHtml(i.nomeProduto)}</td>
                  <td><span class="text-mono">${escHtml(i.codigo)}</span></td>
                  <td><strong>${i.quantidade}</strong> un.</td>
                  <td>${badgeHTML(i.status)}</td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
      </div>
    `;
  },

  async _tabClientes(panel) {
    const clientes = await apiFetch('/relatorios/clientes');
    panel.innerHTML = `
      <div class="card" style="padding:0">
        <div class="table-container">
          <table class="table">
            <thead><tr><th>Cliente</th><th>CPF</th><th>Qtd. Compras</th><th>Total Gasto</th></tr></thead>
            <tbody>
              ${clientes.map(c => `
                <tr>
                  <td class="text-bold">${escHtml(c.nome)}</td>
                  <td><span class="text-mono">${escHtml(c.cpf)}</span></td>
                  <td>${c.qtdCompras}</td>
                  <td class="text-primary text-bold">${fmt(c.totalCompras)}</td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
      </div>
    `;
  },

  async _tabFaturamento(panel) {
    const faturamento = await apiFetch(`/relatorios/faturamento?ano=${this._filtros.ano}`);
    const totalBruto  = faturamento.reduce((s, f) => s + f.receitaBruta, 0);
    const totalLiquido = faturamento.reduce((s, f) => s + f.receitaLiquida, 0);
    panel.innerHTML = `
      <div class="grid-2" style="margin-bottom:20px">
        <div class="kpi-card">
          <div class="kpi-label">Receita Bruta Total (${this._filtros.ano})</div>
          <div class="kpi-value primary">${fmt(totalBruto)}</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-label">Receita Líquida Total (${this._filtros.ano})</div>
          <div class="kpi-value" style="color:var(--loja-success)">${fmt(totalLiquido)}</div>
        </div>
      </div>
      <div class="card">
        <div class="chart-card-title"><i data-lucide="trending-up"></i> Faturamento Mensal ${this._filtros.ano}</div>
        <canvas id="chart-faturamento" height="140"></canvas>
      </div>
    `;
    const canvas = document.getElementById('chart-faturamento');
    if (canvas) {
      const theme = chartThemeOptions();
      this._charts.faturamento = new Chart(canvas, {
        type: 'line',
        data: {
          labels: faturamento.map(f => f.mes),
          datasets: [
            { label:'Receita Bruta',   data: faturamento.map(f => f.receitaBruta),   borderColor:'#28A745', backgroundColor:'rgba(40,167,69,0.06)', tension:0.3, fill:true },
            { label:'Receita Líquida', data: faturamento.map(f => f.receitaLiquida), borderColor:'#1E3A5F', backgroundColor:'rgba(30,58,95,0.06)',  tension:0.3, fill:true },
          ],
        },
        options: {
          responsive:true,
          plugins:{ legend:{ position:'top', labels:{ color: theme.text } } },
          scales: {
            y: { ticks:{ color: theme.muted, callback: v => 'R$' + (v/1000).toFixed(0)+'k' }, grid:{ color: theme.grid } },
            x: { ticks:{ color: theme.muted }, grid:{ color: theme.grid } },
          },
        },
      });
    }
  },
};

function exportarPDF() {
  window.print();
}
