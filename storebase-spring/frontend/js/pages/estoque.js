Pages.estoque = {
  load() { this._render(); },

  async _render() {
    App.setLoading(true);
    try {
      const data = await apiFetch('/estoque');
      const el = document.getElementById('page-estoque');
      if (!el) return;
      el.innerHTML = `
        ${this._alertasHTML(data.alertas)}
        <div class="card" style="padding:0">
          <div class="table-container">
            ${this._tableHTML(data.items)}
          </div>
        </div>
      `;
      lucide.createIcons();
    } catch (err) {
      showToast('Erro ao carregar estoque.', 'error');
    } finally {
      App.setLoading(false);
    }
  },

  _alertasHTML(alertas) {
    if (!alertas.length) return '';
    const criticos = alertas.filter(a => a.status === 'CRITICO');
    const baixos   = alertas.filter(a => a.status === 'BAIXO');
    let html = '';

    if (criticos.length) {
      const nomes = criticos.map(a => escHtml(a.nomeProduto)).join(', ');
      html += `
        <div class="alert-banner alert-critico" style="margin-bottom:10px">
          <i data-lucide="alert-triangle"></i>
          <div>
            <strong>${criticos.length} produto(s) em estoque CRÍTICO!</strong>
            <div style="font-size:13px;margin-top:2px">${nomes}</div>
          </div>
        </div>
      `;
    }

    if (baixos.length) {
      html += `
        <div class="alert-banner alert-baixo" style="margin-bottom:10px">
          <i data-lucide="alert-triangle"></i>
          <strong>${baixos.length} produto(s) com estoque baixo.</strong>
        </div>
      `;
    }

    return html;
  },

  _tableHTML(items) {
    if (!items.length) return `<div class="empty-state"><i data-lucide="boxes"></i><p>Nenhum item no estoque.</p></div>`;
    return `
      <table class="table">
        <thead>
          <tr>
            <th>Produto</th>
            <th>Código</th>
            <th>Categoria</th>
            <th>Quantidade</th>
            <th>Status</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          ${items.map(item => `
            <tr>
              <td>${escHtml(item.nomeProduto)}</td>
              <td><span class="text-mono">${escHtml(item.codigo)}</span></td>
              <td>${escHtml(item.categoria)}</td>
              <td>
                <span style="font-weight:600;color:${item.status === 'OK' ? '#155724' : item.status === 'BAIXO' ? '#856404' : '#721c24'}">
                  ${item.quantidade}
                </span>
                <span style="color:#888;font-size:12px"> un.</span>
              </td>
              <td>${badgeHTML(item.status)}</td>
              <td>
                <div style="display:flex;gap:6px">
                  <button class="btn-stock-entrada" onclick="Pages.estoque._abrirEntrada(${item.produtoId},'${escHtml(item.nomeProduto)}')">
                    <i data-lucide="circle-plus"></i> Entrada
                  </button>
                  <button class="btn-stock-ajuste" onclick="Pages.estoque._abrirAjuste(${item.produtoId},'${escHtml(item.nomeProduto)}',${item.quantidade})">
                    <i data-lucide="settings"></i> Ajuste
                  </button>
                </div>
              </td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    `;
  },

  _abrirEntrada(produtoId, nomeProduto) {
    openModal({
      title: 'Entrada de Estoque',
      width: 420,
      contentHTML: `
        <p style="margin-bottom:14px;color:#555">Produto: <strong>${escHtml(nomeProduto)}</strong></p>
        <div class="form-group">
          <label>Quantidade a Adicionar *</label>
          <input class="input" id="estoque-qtd" type="number" min="1" placeholder="Ex: 10">
        </div>
        <div class="form-group" style="margin-top:12px">
          <label>Observação</label>
          <input class="input" id="estoque-obs" type="text" placeholder="Motivo da entrada (opcional)">
        </div>
      `,
      footerHTML: `
        <button class="btn btn-outline" onclick="closeModal()">Cancelar</button>
        <button class="btn btn-green" onclick="Pages.estoque._confirmarEntrada(${produtoId})">
          <i data-lucide="circle-plus"></i> Confirmar Entrada
        </button>
      `,
    });
  },

  async _confirmarEntrada(produtoId) {
    const qtd = parseInt(formValue('estoque-qtd')) || 0;
    if (qtd <= 0) { showToast('Informe uma quantidade válida.', 'warning'); return; }
    App.setLoading(true);
    try {
      await apiFetch('/estoque/entrada', { method:'POST', body: JSON.stringify({ produtoId, quantidade: qtd }) });
      showToast(`Entrada de ${qtd} unidades registrada.`, 'success');
      closeModal();
      this._render();
    } catch (err) {
      showToast(err.message || 'Erro ao registrar entrada.', 'error');
    } finally {
      App.setLoading(false);
    }
  },

  _abrirAjuste(produtoId, nomeProduto, quantidadeAtual) {
    openModal({
      title: 'Ajuste de Estoque',
      width: 420,
      contentHTML: `
        <p style="margin-bottom:14px;color:#555">Produto: <strong>${escHtml(nomeProduto)}</strong></p>
        <p style="margin-bottom:14px;font-size:13px;color:#888">Quantidade atual: <strong>${quantidadeAtual} un.</strong></p>
        <div class="form-group">
          <label>Nova Quantidade *</label>
          <input class="input" id="estoque-nova-qtd" type="number" min="0" value="${quantidadeAtual}">
        </div>
        <div class="form-group" style="margin-top:12px">
          <label>Motivo do Ajuste</label>
          <input class="input" id="estoque-motivo" type="text" placeholder="Ex: Inventário, perda, etc.">
        </div>
      `,
      footerHTML: `
        <button class="btn btn-outline" onclick="closeModal()">Cancelar</button>
        <button class="btn btn-primary" onclick="Pages.estoque._confirmarAjuste(${produtoId})">
          <i data-lucide="settings"></i> Confirmar Ajuste
        </button>
      `,
    });
  },

  async _confirmarAjuste(produtoId) {
    const qtd = parseInt(formValue('estoque-nova-qtd'));
    if (isNaN(qtd) || qtd < 0) { showToast('Informe uma quantidade válida.', 'warning'); return; }
    App.setLoading(true);
    try {
      await apiFetch('/estoque/ajuste', { method:'POST', body: JSON.stringify({ produtoId, quantidade: qtd }) });
      showToast('Estoque ajustado com sucesso.', 'success');
      closeModal();
      this._render();
    } catch (err) {
      showToast(err.message || 'Erro ao ajustar estoque.', 'error');
    } finally {
      App.setLoading(false);
    }
  },
};
