Pages.dashboard = {
  _charts: {},

  load() {
    this._destroyCharts();
    if (App.isAdmin())   this._loadAdmin();
    else if (App.isVendedor()) this._loadVendedor();
    else if (App.isEstoque())  this._loadEstoque();
  },

  _destroyCharts() {
    Object.values(this._charts).forEach(c => { try { c.destroy(); } catch {} });
    this._charts = {};
  },

  
  async _loadAdmin() {
    App.setLoading(true);
    try {
      const [stats, grafico, ultimasVendas] = await Promise.all([
        apiFetch('/dashboard/stats'),
        apiFetch('/dashboard/grafico'),
        apiFetch('/dashboard/ultimas-vendas'),
      ]);

      const el = document.getElementById('page-dashboard');
      if (!el) return;

      el.innerHTML = `
        ${this._welcomeHTML('#1E3A5F', 'layout-dashboard', 'Painel Administrativo', 'Visão geral completa do sistema')}

        <!-- 4 stat cards -->
        <div class="grid-4" style="margin-bottom:20px" id="dash-stats"></div>

        <!-- Gráfico + Últimas Vendas -->
        <div class="dashboard-grid" style="margin-bottom:20px">
          <div class="chart-card">
            <div class="chart-card-title">
              <i data-lucide="trending-up"></i> Vendas — Últimos 7 Dias
            </div>
            <canvas id="chart-vendas-semana" height="180"></canvas>
          </div>
          <div class="chart-card">
            <div class="last-sales-header">
              <span>Últimas Vendas</span>
              <a href="#" onclick="Router.navigate('vendas');return false">Ver todas →</a>
            </div>
            <div id="dashboard-ultimas-vendas"></div>
          </div>
        </div>

        <!-- Ações Rápidas -->
        <div class="card">
          <div style="font-size:14px;font-weight:600;margin-bottom:14px">Ações Rápidas</div>
          <div id="dashboard-acoes"></div>
        </div>
      `;

      this._renderStatsAdmin(stats);
      this._renderGrafico('chart-vendas-semana', grafico, '#1E3A5F');
      this._renderUltimasVendas(ultimasVendas);
      this._renderAcoesAdmin();
      lucide.createIcons();
    } catch (err) {
      showToast('Erro ao carregar dashboard.', 'error');
    } finally {
      App.setLoading(false);
    }
  },

  _renderStatsAdmin(stats) {
    document.getElementById('dash-stats').innerHTML = `
      <div class="stat-card">
        <div class="stat-icon" style="background:rgba(30,58,95,0.1)">
          <i data-lucide="shopping-cart" style="color:var(--loja-primary)"></i>
        </div>
        <div class="stat-info">
          <div class="stat-label">Vendas Hoje</div>
          <div class="stat-value">${fmt(stats.vendasHojeTotal)}</div>
          <div class="stat-sub">${stats.vendasHojeQtd} venda(s)</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:rgba(23,162,184,0.1)">
          <i data-lucide="users" style="color:#17a2b8"></i>
        </div>
        <div class="stat-info">
          <div class="stat-label">Total de Clientes</div>
          <div class="stat-value">${stats.totalClientes}</div>
          <div class="stat-sub">${stats.totalFuncionarios} funcionário(s)</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:rgba(220,53,69,0.1)">
          <i data-lucide="alert-triangle" style="color:${stats.alertasEstoque > 0 ? '#dc3545' : 'var(--loja-text-muted)'}"></i>
        </div>
        <div class="stat-info">
          <div class="stat-label">Alertas de Estoque</div>
          <div class="stat-value" style="color:${stats.alertasEstoque > 0 ? '#dc3545' : 'inherit'}">${stats.alertasEstoque}</div>
          <div class="stat-sub">${stats.alertasCriticos} crítico(s) · ${stats.alertasBaixos} baixo(s)</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:rgba(253,126,20,0.1)">
          <i data-lucide="file-text" style="color:#fd7e14"></i>
        </div>
        <div class="stat-info">
          <div class="stat-label">Orçamentos Abertos</div>
          <div class="stat-value">${stats.orcamentosAbertos}</div>
          <div class="stat-sub">Receita total: ${fmt(stats.totalVendasMes)}</div>
        </div>
      </div>
    `;
  },

  _renderAcoesAdmin() {
    document.getElementById('dashboard-acoes').innerHTML = `
      <div class="quick-actions">
        <button class="btn btn-primary" onclick="Router.navigate('nova-venda')"><i data-lucide="plus"></i> Nova Venda</button>
        <button class="btn btn-orange"  onclick="Router.navigate('orcamentos')"><i data-lucide="file-text"></i> Novo Orçamento</button>
        <button class="btn btn-teal"    onclick="Router.navigate('estoque')"><i data-lucide="boxes"></i> Ver Estoque</button>
        <button class="btn btn-green"   onclick="Router.navigate('clientes')"><i data-lucide="users"></i> Clientes</button>
        <button class="btn btn-outline" onclick="Router.navigate('relatorios')"><i data-lucide="bar-chart-2"></i> Relatórios</button>
      </div>
    `;
  },

  
  async _loadVendedor() {
    App.setLoading(true);
    try {
      const id = App.user.id;
      const [stats, grafico, minhasVendas] = await Promise.all([
        apiFetch(`/dashboard/stats-vendedor?funcId=${id}`),
        apiFetch(`/dashboard/grafico-vendedor?funcId=${id}`),
        apiFetch(`/dashboard/minhas-vendas?funcId=${id}`),
      ]);

      const el = document.getElementById('page-dashboard');
      if (!el) return;

      el.innerHTML = `
        ${this._welcomeHTML('#1E3A5F', 'shopping-cart', 'Painel do Vendedor', 'Acompanhe suas vendas e metas')}

        <!-- 3 stat cards -->
        <div class="grid-3" style="margin-bottom:20px" id="dash-stats"></div>

        <!-- Gráfico + Minhas Vendas -->
        <div class="dashboard-grid" style="margin-bottom:20px">
          <div class="chart-card">
            <div class="chart-card-title">
              <i data-lucide="trending-up"></i> Minhas Vendas — Últimos 7 Dias
            </div>
            <canvas id="chart-vendas-semana" height="180"></canvas>
          </div>
          <div class="chart-card">
            <div class="last-sales-header">
              <span>Minhas Últimas Vendas</span>
              <a href="#" onclick="Router.navigate('vendas');return false">Ver todas →</a>
            </div>
            <div id="dashboard-ultimas-vendas"></div>
          </div>
        </div>

        <!-- Ações Rápidas -->
        <div class="card">
          <div style="font-size:14px;font-weight:600;margin-bottom:14px">Ações Rápidas</div>
          <div class="quick-actions">
            <button class="btn btn-primary" onclick="Router.navigate('nova-venda')"><i data-lucide="plus"></i> Nova Venda</button>
            <button class="btn btn-orange"  onclick="Router.navigate('orcamentos')"><i data-lucide="file-text"></i> Orçamentos</button>
            <button class="btn btn-teal"    onclick="Router.navigate('clientes')"><i data-lucide="users"></i> Clientes</button>
          </div>
        </div>
      `;

      this._renderStatsVendedor(stats);
      this._renderGrafico('chart-vendas-semana', grafico, '#28a745');
      this._renderUltimasVendas(minhasVendas);
      lucide.createIcons();
    } catch (err) {
      showToast('Erro ao carregar dashboard.', 'error');
    } finally {
      App.setLoading(false);
    }
  },

  _renderStatsVendedor(stats) {
    document.getElementById('dash-stats').innerHTML = `
      <div class="stat-card">
        <div class="stat-icon" style="background:rgba(40,167,69,0.1)">
          <i data-lucide="shopping-cart" style="color:#28a745"></i>
        </div>
        <div class="stat-info">
          <div class="stat-label">Minhas Vendas Hoje</div>
          <div class="stat-value">${fmt(stats.vendasHojeTotal)}</div>
          <div class="stat-sub">${stats.vendasHojeQtd} venda(s)</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:rgba(23,162,184,0.1)">
          <i data-lucide="users" style="color:#17a2b8"></i>
        </div>
        <div class="stat-info">
          <div class="stat-label">Total de Clientes</div>
          <div class="stat-value">${stats.totalClientes}</div>
          <div class="stat-sub">na base de dados</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:rgba(253,126,20,0.1)">
          <i data-lucide="file-text" style="color:#fd7e14"></i>
        </div>
        <div class="stat-info">
          <div class="stat-label">Orçamentos Abertos</div>
          <div class="stat-value">${stats.meusOrcamentosAbertos}</div>
          <div class="stat-sub">Total acumulado: ${fmt(stats.totalVendasMes)}</div>
        </div>
      </div>
    `;
  },

  
  async _loadEstoque() {
    App.setLoading(true);
    try {
      const stats = await apiFetch('/dashboard/stats-estoque');

      const el = document.getElementById('page-dashboard');
      if (!el) return;

      el.innerHTML = `
        ${this._welcomeHTML('#1E3A5F', 'boxes', 'Painel do Estoque', 'Monitore e controle os níveis de estoque')}

        <!-- 4 stat cards -->
        <div class="grid-4" style="margin-bottom:20px" id="dash-stats"></div>

        <!-- Alertas de estoque -->
        <div id="dash-alertas" style="margin-bottom:20px"></div>

        <!-- Tabela de produtos que precisam de atenção -->
        <div class="card" style="padding:0;margin-bottom:20px">
          <div style="padding:16px 20px;border-bottom:1px solid var(--loja-card-border);font-size:14px;font-weight:600">
            Produtos que Precisam de Atenção
          </div>
          <div class="table-container" id="dash-tabela-alertas"></div>
        </div>

        <!-- Ações Rápidas -->
        <div class="card">
          <div style="font-size:14px;font-weight:600;margin-bottom:14px">Ações Rápidas</div>
          <div class="quick-actions">
            <button class="btn btn-teal"    onclick="Router.navigate('estoque')"><i data-lucide="boxes"></i> Gerenciar Estoque</button>
            <button class="btn btn-outline" onclick="Router.navigate('produtos')"><i data-lucide="package"></i> Ver Produtos</button>
          </div>
        </div>
      `;

      this._renderStatsEstoque(stats);
      this._renderAlertasBanners(stats);
      this._renderTabelaAlertasEstoque(stats.alertas);
      lucide.createIcons();
    } catch (err) {
      showToast('Erro ao carregar dashboard.', 'error');
    } finally {
      App.setLoading(false);
    }
  },

  _renderStatsEstoque(stats) {
    document.getElementById('dash-stats').innerHTML = `
      <div class="stat-card">
        <div class="stat-icon" style="background:rgba(23,162,184,0.1)">
          <i data-lucide="package" style="color:#17a2b8"></i>
        </div>
        <div class="stat-info">
          <div class="stat-label">Total de Produtos</div>
          <div class="stat-value">${stats.totalProdutos}</div>
          <div class="stat-sub">${stats.produtosOK} em nível OK</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:rgba(220,53,69,0.1)">
          <i data-lucide="alert-triangle" style="color:${stats.produtosCriticos > 0 ? '#dc3545' : 'var(--loja-text-muted)'}"></i>
        </div>
        <div class="stat-info">
          <div class="stat-label">Estoque Crítico</div>
          <div class="stat-value" style="color:${stats.produtosCriticos > 0 ? '#dc3545' : 'inherit'}">${stats.produtosCriticos}</div>
          <div class="stat-sub">produto(s) abaixo de 5 un.</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:rgba(255,193,7,0.15)">
          <i data-lucide="alert-circle" style="color:var(--loja-warning-text)"></i>
        </div>
        <div class="stat-info">
          <div class="stat-label">Estoque Baixo</div>
          <div class="stat-value" style="color:${stats.produtosBaixos > 0 ? 'var(--loja-warning-text)' : 'inherit'}">${stats.produtosBaixos}</div>
          <div class="stat-sub">produto(s) entre 5 e 9 un.</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:rgba(40,167,69,0.1)">
          <i data-lucide="dollar-sign" style="color:#28a745"></i>
        </div>
        <div class="stat-info">
          <div class="stat-label">Valor em Estoque</div>
          <div class="stat-value" style="font-size:18px">${fmt(stats.valorTotalEstoque)}</div>
          <div class="stat-sub">custo total</div>
        </div>
      </div>
    `;
  },

  _renderAlertasBanners(stats) {
    const el = document.getElementById('dash-alertas');
    if (!el) return;
    let html = '';
    if (stats.produtosCriticos > 0) {
      const nomes = stats.alertas
        .filter(a => a.status === 'CRITICO')
        .map(a => `<strong>${escHtml(a.nomeProduto)}</strong> (${a.quantidade} un.)`)
        .join(', ');
      html += `
        <div class="alert-banner alert-critico">
          <i data-lucide="alert-triangle"></i>
          <div>
            <strong>${stats.produtosCriticos} produto(s) em estoque CRÍTICO!</strong>
            <div style="font-size:13px;margin-top:4px">${nomes}</div>
          </div>
        </div>
      `;
    }
    if (stats.produtosBaixos > 0) {
      html += `
        <div class="alert-banner alert-baixo">
          <i data-lucide="alert-triangle"></i>
          <strong>${stats.produtosBaixos} produto(s) com estoque baixo — considere reabastecer em breve.</strong>
        </div>
      `;
    }
    if (!html) {
      html = `
        <div class="alert-banner alert-ok">
          <i data-lucide="check-circle"></i>
          <strong>Todos os produtos estão com estoque em nível adequado.</strong>
        </div>
      `;
    }
    el.innerHTML = html;
  },

  _renderTabelaAlertasEstoque(alertas) {
    const el = document.getElementById('dash-tabela-alertas');
    if (!el) return;
    if (!alertas.length) {
      el.innerHTML = `<div class="empty-state"><i data-lucide="check-circle"></i><p>Nenhum produto com alerta de estoque.</p></div>`;
      return;
    }
    el.innerHTML = `
      <table class="table">
        <thead>
          <tr>
            <th>Produto</th>
            <th>Código</th>
            <th>Quantidade</th>
            <th>Status</th>
            <th>Ação</th>
          </tr>
        </thead>
        <tbody>
          ${alertas.map(a => `
            <tr>
              <td class="text-bold">${escHtml(a.nomeProduto)}</td>
              <td><span class="text-mono">${escHtml(a.codigo)}</span></td>
              <td><span style="font-weight:700;color:${a.status === 'CRITICO' ? 'var(--loja-error-text)' : 'var(--loja-warning-text)'}">${a.quantidade} un.</span></td>
              <td>${badgeHTML(a.status)}</td>
              <td>
                <button class="btn-stock-entrada" onclick="Router.navigate('estoque')">
                  <i data-lucide="circle-plus"></i> Ir para Estoque
                </button>
              </td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    `;
  },

  _welcomeHTML(color, icon, titulo, subtitulo) {
    return `
      <div style="
        background: linear-gradient(135deg, ${color} 0%, ${color}cc 100%);
        border-radius: var(--radius);
        padding: 22px 28px;
        color: white;
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 20px;
        box-shadow: var(--shadow);
      ">
        <div style="
          width:52px; height:52px; border-radius:50%;
          background:rgba(255,255,255,0.15);
          display:flex; align-items:center; justify-content:center; flex-shrink:0;
        ">
          <i data-lucide="${icon}" style="width:26px;height:26px"></i>
        </div>
        <div>
          <div style="font-size:11px;font-weight:500;opacity:.8;text-transform:uppercase;letter-spacing:1px;margin-bottom:2px">
            Bem-vindo(a), ${escHtml(App.user.nome)}
          </div>
          <div style="font-size:20px;font-weight:700">${titulo}</div>
          <div style="font-size:13px;opacity:.8;margin-top:2px">${subtitulo}</div>
        </div>
      </div>
    `;
  },

  _renderGrafico(canvasId, grafico, color) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    if (this._charts[canvasId]) { this._charts[canvasId].destroy(); }
    this._charts[canvasId] = new Chart(canvas, {
      type: 'line',
      data: {
        labels: grafico.map(d => d.data),
        datasets: [{
          label: 'Vendas',
          data: grafico.map(d => d.total),
          borderColor: color,
          backgroundColor: color + '14',
          tension: 0.4,
          fill: true,
          pointBackgroundColor: color,
          pointRadius: 5,
        }]
      },
      options: {
        responsive: true,
        plugins: { legend: { display: false } },
        scales: {
          y: { ticks: { callback: v => 'R$' + (v / 1000).toFixed(1) + 'k' } },
          x: { ticks: { font: { size: 11 } } }
        }
      }
    });
  },

  _renderUltimasVendas(vendas) {
    const el = document.getElementById('dashboard-ultimas-vendas');
    if (!el) return;
    if (!vendas.length) {
      el.innerHTML = '<div class="empty-state"><i data-lucide="shopping-cart"></i><p>Nenhuma venda registrada.</p></div>';
      return;
    }
    el.innerHTML = `
      <table class="table">
        <thead>
          <tr><th>#</th><th>Cliente</th><th>Total</th><th>Pagamento</th></tr>
        </thead>
        <tbody>
          ${vendas.map(v => `
            <tr>
              <td class="text-muted">#${v.id}</td>
              <td>${escHtml(v.clienteNome)}</td>
              <td class="text-primary text-bold">${fmt(v.total)}</td>
              <td>${badgeHTML(v.formaPagamento)}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    `;
  },
};
