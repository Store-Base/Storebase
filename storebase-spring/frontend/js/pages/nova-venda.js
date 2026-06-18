Pages['nova-venda'] = {
  _venda: null,
  _searchDebounce: null,

  load() {
    this._venda = {
      step: 0,
      cliente: null,
      avulso: false,
      itens: [],
      desconto: 0,
      descontoTipo: 'R$',
      formaPagamento: 'PIX',
      parcelas: 1,
      juros: 0,
    };
    this._render();
  },

  _parcelavel() {
    return this._venda.formaPagamento === 'CARTAO_CREDITO'
        || this._venda.formaPagamento === 'BOLETO';
  },

  _calcTotais() {
    const subtotal = this._venda.itens.reduce((s, i) => s + i.precoUnitario * i.quantidade, 0);
    const descontoVal = this._venda.descontoTipo === '%'
      ? (subtotal * this._venda.desconto) / 100
      : this._venda.desconto;
    const base = Math.max(0, subtotal - descontoVal);

    const parcelas = this._parcelavel() ? (this._venda.parcelas || 1) : 1;
    const juros    = this._parcelavel() ? (this._venda.juros || 0) : 0;
    // Juros simples por parcela: total = base * (1 + (juros% * nº de parcelas))
    const jurosVal = parcelas > 1 ? base * (juros / 100) * parcelas : 0;
    const total = base + jurosVal;
    const valorParcela = parcelas > 0 ? total / parcelas : total;

    return { subtotal, descontoVal, base, jurosVal, total, parcelas, valorParcela };
  },

  _render() {
    const el = document.getElementById('page-nova-venda');
    if (!el) return;
    el.innerHTML = `
      <div class="wizard-card">
        ${this._stepBarHTML()}
        <div id="wizard-body">
          ${this._renderStep()}
        </div>
      </div>
    `;
    this._attachStepListeners();
    lucide.createIcons();
  },

  _stepBarHTML() {
    const steps = ['Cliente', 'Produtos', 'Pagamento', 'Confirmação'];
    const current = this._venda.step;
    let html = '<div class="step-bar">';
    steps.forEach((label, idx) => {
      const isDone = idx < current;
      const isActive = idx === current;
      html += `
        <div class="step-item">
          <div class="step-circle ${isDone ? 'done' : isActive ? 'active' : ''}">
            ${isDone ? '<i data-lucide="check-circle" style="width:18px;height:18px"></i>' : idx + 1}
          </div>
          <span class="step-label ${isDone ? 'done' : isActive ? 'active' : ''}">${label}</span>
        </div>
      `;
      if (idx < steps.length - 1) html += `<div class="step-line ${idx < current ? 'done' : ''}"></div>`;
    });
    html += '</div>';
    return html;
  },

  _renderStep() {
    switch (this._venda.step) {
      case 0: return this._step1HTML();
      case 1: return this._step2HTML();
      case 2: return this._step3HTML();
      case 3: return this._step4HTML();
      default: return '';
    }
  },

  // ---- STEP 1: Cliente ----
  _step1HTML() {
    return `
      <h3 style="font-size:15px;font-weight:600;margin-bottom:16px">Selecionar Cliente</h3>
      <div class="form-group" style="margin-bottom:14px">
        <label>Buscar Cliente</label>
        <div style="position:relative">
          <i data-lucide="search" style="position:absolute;left:10px;top:50%;transform:translateY(-50%);width:15px;height:15px;color:#adb5bd"></i>
          <input class="input" id="cli-busca" type="text" style="padding-left:34px"
            placeholder="Nome, CPF ou email..."
            value="${this._venda.cliente && !this._venda.avulso ? this._venda.cliente.nome : ''}">
        </div>
      </div>
      <div id="cli-resultados" style="margin-bottom:14px"></div>
      ${this._venda.cliente && !this._venda.avulso ? `
        <div class="alert-info alert-banner" style="margin-bottom:14px">
          <i data-lucide="check-circle" style="color:#004085"></i>
          <span>Cliente selecionado: <strong>${escHtml(this._venda.cliente.nome)}</strong></span>
        </div>
      ` : ''}
      <div style="display:flex;align-items:center;gap:10px;margin-bottom:20px">
        <hr style="flex:1;border:none;border-top:1px solid #e0e0e0">
        <span style="color:#888;font-size:12px">ou</span>
        <hr style="flex:1;border:none;border-top:1px solid #e0e0e0">
      </div>
      <button class="btn ${this._venda.avulso ? 'btn-primary' : 'btn-outline'}" onclick="Pages['nova-venda']._selectAvulso()" style="width:100%">
        <i data-lucide="user-x"></i> ${this._venda.avulso ? '✓ Venda Avulsa selecionada' : 'Continuar como Venda Avulsa (sem cliente)'}
      </button>
      <div style="display:flex;justify-content:flex-end;margin-top:20px">
        <button class="btn btn-primary" onclick="Pages['nova-venda']._nextStep()"
          ${!this._venda.cliente && !this._venda.avulso ? 'disabled style="opacity:.5;cursor:not-allowed"' : ''}>
          Próximo <i data-lucide="chevron-right"></i>
        </button>
      </div>
    `;
  },

  _selectAvulso() {
    this._venda.avulso = true;
    this._venda.cliente = null;
    this._renderStep1Refresh();
  },

  _renderStep1Refresh() {
    const body = document.getElementById('wizard-body');
    if (body) { body.innerHTML = this._step1HTML(); this._attachStepListeners(); lucide.createIcons(); }
  },

  _attachStepListeners() {
    if (this._venda.step === 0) {
      const input = document.getElementById('cli-busca');
      if (input) {
        input.oninput = debounce(async (e) => {
          const q = e.target.value.trim();
          if (!q) { document.getElementById('cli-resultados').innerHTML = ''; return; }
          try {
            const data = await apiFetch(`/clientes?search=${encodeURIComponent(q)}&size=5`);
            const resultados = document.getElementById('cli-resultados');
            if (!resultados) return;
            if (!data.content.length) { resultados.innerHTML = '<p style="color:#888;font-size:13px">Nenhum cliente encontrado.</p>'; return; }
            resultados.innerHTML = `<div style="display:flex;flex-direction:column;gap:6px">
              ${data.content.map(c => `
                <button class="btn btn-outline" style="text-align:left;justify-content:flex-start"
                  onclick="Pages['nova-venda']._selectCliente(${c.id},'${escHtml(c.nome)}','${escHtml(c.cpf)}')">
                  <i data-lucide="user"></i>
                  <span><strong>${escHtml(c.nome)}</strong> — <span class="font-mono">${escHtml(c.cpf)}</span></span>
                </button>
              `).join('')}
            </div>`;
            lucide.createIcons();
          } catch {}
        }, 300);
      }
    }

    if (this._venda.step === 1) {
      const input = document.getElementById('prod-busca-venda');
      if (input) {
        input.oninput = debounce(async (e) => {
          const q = e.target.value.trim();
          const resultados = document.getElementById('prod-resultados-venda');
          if (!q) { if (resultados) resultados.innerHTML = ''; return; }
          try {
            const data = await apiFetch(`/produtos?search=${encodeURIComponent(q)}&size=5`);
            if (!resultados) return;
            resultados.innerHTML = `<div style="display:flex;flex-direction:column;gap:6px">
              ${data.content.map(p => `
                <button class="btn btn-outline" style="text-align:left;justify-content:flex-start"
                  onclick="Pages['nova-venda']._addProduto(${p.id},'${escHtml(p.nome)}',${p.precoVenda},${p.quantidadeEstoque})">
                  <i data-lucide="package"></i>
                  <span><strong>${escHtml(p.nome)}</strong> — ${fmt(p.precoVenda)} (Estq: ${p.quantidadeEstoque})</span>
                </button>
              `).join('')}
            </div>`;
            lucide.createIcons();
          } catch {}
        }, 300);
      }
    }

    if (this._venda.step === 2) {
      const descInput = document.getElementById('venda-desconto');
      if (descInput) {
        descInput.oninput = () => {
          this._venda.desconto = parseFloat(descInput.value) || 0;
          this._updateTotaisDisplay();
        };
      }
      const tipoSelect = document.getElementById('venda-desconto-tipo');
      if (tipoSelect) {
        tipoSelect.onchange = () => {
          this._venda.descontoTipo = tipoSelect.value;
          this._updateTotaisDisplay();
        };
      }
      const parcelasSelect = document.getElementById('venda-parcelas');
      if (parcelasSelect) {
        parcelasSelect.onchange = () => {
          this._venda.parcelas = parseInt(parcelasSelect.value) || 1;
          this._updateTotaisDisplay();
        };
      }
      const jurosInput = document.getElementById('venda-juros');
      if (jurosInput) {
        jurosInput.oninput = () => {
          this._venda.juros = parseFloat(jurosInput.value) || 0;
          this._updateTotaisDisplay();
        };
      }
    }
  },

  _selectCliente(id, nome, cpf) {
    this._venda.cliente = { id, nome, cpf };
    this._venda.avulso = false;
    this._renderStep1Refresh();
  },

  // ---- STEP 2: Produtos ----
  _step2HTML() {
    const itensHTML = this._venda.itens.length
      ? this._venda.itens.map((item, idx) => `
          <div class="cart-item">
            <div class="cart-item-name">
              <div>${escHtml(item.nomeProduto)}</div>
              <div style="font-size:12px;color:#888">Estoque: ${item.estoque} un.</div>
            </div>
            <div class="cart-qty">
              <button onclick="Pages['nova-venda']._changeQty(${idx}, -1)">−</button>
              <span>${item.quantidade}</span>
              <button onclick="Pages['nova-venda']._changeQty(${idx}, 1)">+</button>
            </div>
            <div>
              <input class="input" style="width:100px;text-align:right" type="number" step="0.01" min="0"
                value="${item.precoUnitario}" oninput="Pages['nova-venda']._updatePreco(${idx}, this.value)">
            </div>
            <div class="cart-item-total">${fmt(item.precoUnitario * item.quantidade)}</div>
            <button class="cart-item-remove" onclick="Pages['nova-venda']._removeProduto(${idx})">
              <i data-lucide="x"></i>
            </button>
          </div>
        `).join('')
      : `<div class="empty-state" style="padding:24px"><i data-lucide="package"></i><p>Nenhum produto adicionado.</p></div>`;

    const { subtotal } = this._calcTotais();

    return `
      <h3 style="font-size:15px;font-weight:600;margin-bottom:16px">Adicionar Produtos</h3>
      <div class="form-group" style="margin-bottom:14px">
        <label>Buscar Produto</label>
        <div style="position:relative">
          <i data-lucide="search" style="position:absolute;left:10px;top:50%;transform:translateY(-50%);width:15px;height:15px;color:#adb5bd"></i>
          <input class="input" id="prod-busca-venda" type="text" style="padding-left:34px" placeholder="Nome, código ou categoria...">
        </div>
      </div>
      <div id="prod-resultados-venda" style="margin-bottom:14px"></div>
      <div style="margin:16px 0 8px;font-weight:600;font-size:13px">Carrinho</div>
      <div id="carrinho-items">${itensHTML}</div>
      ${this._venda.itens.length ? `<div style="text-align:right;margin-top:8px;font-size:14px;color:#888">Subtotal: <strong style="color:var(--loja-primary)">${fmt(subtotal)}</strong></div>` : ''}
      <div style="display:flex;justify-content:space-between;margin-top:20px">
        <button class="btn btn-outline" onclick="Pages['nova-venda']._prevStep()">
          <i data-lucide="chevron-left"></i> Voltar
        </button>
        <button class="btn btn-primary" onclick="Pages['nova-venda']._nextStep()"
          ${!this._venda.itens.length ? 'disabled style="opacity:.5;cursor:not-allowed"' : ''}>
          Próximo <i data-lucide="chevron-right"></i>
        </button>
      </div>
    `;
  },

  _addProduto(id, nome, preco, estoque) {
    const existing = this._venda.itens.findIndex(i => i.produtoId === id);
    if (existing >= 0) {
      if (this._venda.itens[existing].quantidade < estoque) {
        this._venda.itens[existing].quantidade++;
      } else {
        showToast('Quantidade máxima disponível atingida.', 'warning');
        return;
      }
    } else {
      this._venda.itens.push({ produtoId: id, nomeProduto: nome, quantidade: 1, precoUnitario: preco, estoque });
    }
    const input = document.getElementById('prod-busca-venda');
    if (input) input.value = '';
    const resultados = document.getElementById('prod-resultados-venda');
    if (resultados) resultados.innerHTML = '';
    this._refreshCarrinho();
    showToast(`${nome} adicionado ao carrinho.`, 'success');
  },

  _changeQty(idx, delta) {
    const item = this._venda.itens[idx];
    const newQty = item.quantidade + delta;
    if (newQty < 1) { this._removeProduto(idx); return; }
    if (newQty > item.estoque) { showToast('Estoque insuficiente.', 'warning'); return; }
    this._venda.itens[idx].quantidade = newQty;
    this._refreshCarrinho();
  },

  _updatePreco(idx, value) {
    this._venda.itens[idx].precoUnitario = parseFloat(value) || 0;
    this._refreshCarrinho();
  },

  _removeProduto(idx) {
    this._venda.itens.splice(idx, 1);
    this._refreshCarrinho();
  },

  _refreshCarrinho() {
    const body = document.getElementById('wizard-body');
    if (body) { body.innerHTML = this._step2HTML(); this._attachStepListeners(); lucide.createIcons(); }
  },

  // ---- STEP 3: Pagamento ----
  _step3HTML() {
    const formas = ['PIX','DINHEIRO','CARTAO_CREDITO','CARTAO_DEBITO','BOLETO'];
    const labels = { PIX:'PIX', DINHEIRO:'Dinheiro', CARTAO_CREDITO:'Cartão Crédito', CARTAO_DEBITO:'Cartão Débito', BOLETO:'Boleto' };
    const { subtotal, descontoVal, jurosVal, total, parcelas, valorParcela } = this._calcTotais();

    return `
      <h3 style="font-size:15px;font-weight:600;margin-bottom:16px">Forma de Pagamento</h3>
      <div class="payment-options">
        ${formas.map(f => `
          <button class="payment-opt ${this._venda.formaPagamento === f ? 'selected' : ''}"
            onclick="Pages['nova-venda']._selectPagamento('${f}')">
            ${labels[f]}
          </button>
        `).join('')}
      </div>
      <div style="display:grid;grid-template-columns:1fr 120px;gap:10px;margin-bottom:16px;align-items:flex-end">
        <div class="form-group">
          <label>Desconto</label>
          <input class="input" id="venda-desconto" type="number" min="0" step="0.01"
            value="${this._venda.desconto || ''}">
        </div>
        <div class="form-group">
          <label>Tipo</label>
          <select class="input" id="venda-desconto-tipo">
            <option value="R$" ${this._venda.descontoTipo==='R$'?'selected':''}>R$</option>
            <option value="%" ${this._venda.descontoTipo==='%'?'selected':''}>%</option>
          </select>
        </div>
      </div>
      ${this._parcelavel() ? `
        <div style="display:grid;grid-template-columns:1fr 1fr;gap:10px;margin-bottom:16px;align-items:flex-end">
          <div class="form-group">
            <label>Parcelas</label>
            <select class="input" id="venda-parcelas">
              ${[1,2,3,4,5,6,7,8,9,10,11,12].map(n =>
                `<option value="${n}" ${this._venda.parcelas===n?'selected':''}>${n}x</option>`).join('')}
            </select>
          </div>
          <div class="form-group">
            <label>Juros por parcela (% a.m.)</label>
            <input class="input" id="venda-juros" type="number" min="0" step="0.01"
              value="${this._venda.juros || ''}" placeholder="Ex: 2.5">
          </div>
        </div>
      ` : ''}
      <div class="totals-box">
        <div class="totals-row">
          <span>Subtotal</span><span>${fmt(subtotal)}</span>
        </div>
        <div class="totals-row" id="desconto-row" style="${descontoVal > 0 ? '' : 'display:none'}">
          <span style="color:#dc3545">Desconto</span><span style="color:#dc3545">-${fmt(descontoVal)}</span>
        </div>
        <div class="totals-row" id="juros-row" style="${jurosVal > 0 ? '' : 'display:none'}">
          <span style="color:#fd7e14">Juros (${parcelas}x)</span><span style="color:#fd7e14">+${fmt(jurosVal)}</span>
        </div>
        <div class="totals-row total">
          <span>Total</span><span id="total-display">${fmt(total)}</span>
        </div>
        <div class="totals-row" id="parcela-info" style="${parcelas > 1 ? '' : 'display:none'};justify-content:flex-end;color:var(--loja-text-muted);font-size:13px">
          ${parcelas > 1 ? `${parcelas}x de ${fmt(valorParcela)}` : ''}
        </div>
      </div>
      <div style="display:flex;justify-content:space-between;margin-top:20px">
        <button class="btn btn-outline" onclick="Pages['nova-venda']._prevStep()">
          <i data-lucide="chevron-left"></i> Voltar
        </button>
        <button class="btn btn-primary" onclick="Pages['nova-venda']._nextStep()">
          Próximo <i data-lucide="chevron-right"></i>
        </button>
      </div>
    `;
  },

  _selectPagamento(forma) {
    this._venda.formaPagamento = forma;
    // Parcelamento só vale para crédito/boleto — zera ao trocar para as demais
    if (!this._parcelavel()) { this._venda.parcelas = 1; this._venda.juros = 0; }
    const body = document.getElementById('wizard-body');
    if (body) { body.innerHTML = this._step3HTML(); this._attachStepListeners(); lucide.createIcons(); }
  },

  _updateTotaisDisplay() {
    const { descontoVal, jurosVal, total, parcelas, valorParcela } = this._calcTotais();

    const totalEl = document.getElementById('total-display');
    if (totalEl) totalEl.textContent = fmt(total);

    const descontoRow = document.getElementById('desconto-row');
    if (descontoRow) {
      descontoRow.style.display = descontoVal > 0 ? '' : 'none';
      const cells = descontoRow.querySelectorAll('span');
      if (cells[1]) cells[1].textContent = '-' + fmt(descontoVal);
    }

    const jurosRow = document.getElementById('juros-row');
    if (jurosRow) {
      jurosRow.style.display = jurosVal > 0 ? '' : 'none';
      const cells = jurosRow.querySelectorAll('span');
      if (cells[0]) cells[0].textContent = `Juros (${parcelas}x)`;
      if (cells[1]) cells[1].textContent = '+' + fmt(jurosVal);
    }

    const parcelaInfo = document.getElementById('parcela-info');
    if (parcelaInfo) {
      parcelaInfo.style.display = parcelas > 1 ? '' : 'none';
      parcelaInfo.textContent = parcelas > 1 ? `${parcelas}x de ${fmt(valorParcela)}` : '';
    }
  },

  // ---- STEP 4: Confirmação ----
  _step4HTML() {
    const { subtotal, descontoVal, jurosVal, total, parcelas, valorParcela } = this._calcTotais();
    const formaLabels = { PIX:'PIX', DINHEIRO:'Dinheiro', CARTAO_CREDITO:'Cartão de Crédito', CARTAO_DEBITO:'Cartão de Débito', BOLETO:'Boleto' };

    return `
      <div style="text-align:center;margin-bottom:20px">
        <i data-lucide="check-circle" style="width:48px;height:48px;color:var(--loja-success)"></i>
        <h3 style="font-size:16px;font-weight:700;margin-top:8px">Confirmar Venda</h3>
      </div>
      <div class="confirmation-box">
        <div class="confirmation-row">
          <span>Cliente</span>
          <span>${this._venda.avulso ? 'Avulso' : escHtml(this._venda.cliente?.nome || '')}</span>
        </div>
        <div class="confirmation-row">
          <span>Pagamento</span>
          <span>${formaLabels[this._venda.formaPagamento] || this._venda.formaPagamento}</span>
        </div>
        <div class="confirmation-row">
          <span>Itens</span>
          <span>${this._venda.itens.length} produto(s)</span>
        </div>
        ${this._venda.itens.map(i => `
          <div class="confirmation-row">
            <span>${escHtml(i.nomeProduto)} × ${i.quantidade}</span>
            <span>${fmt(i.precoUnitario * i.quantidade)}</span>
          </div>
        `).join('')}
        <div class="confirmation-row">
          <span>Subtotal</span><span>${fmt(subtotal)}</span>
        </div>
        ${descontoVal > 0 ? `<div class="confirmation-row"><span style="color:#dc3545">Desconto</span><span style="color:#dc3545">-${fmt(descontoVal)}</span></div>` : ''}
        ${jurosVal > 0 ? `<div class="confirmation-row"><span style="color:#fd7e14">Juros (${parcelas}x)</span><span style="color:#fd7e14">+${fmt(jurosVal)}</span></div>` : ''}
        <div class="confirmation-row highlight">
          <span>Total</span><span>${fmt(total)}</span>
        </div>
        ${parcelas > 1 ? `<div class="confirmation-row"><span>Parcelamento</span><span>${parcelas}x de ${fmt(valorParcela)}</span></div>` : ''}
      </div>
      <div style="display:flex;justify-content:space-between;margin-top:20px;gap:10px">
        <button class="btn btn-outline" onclick="Pages['nova-venda']._prevStep()">
          <i data-lucide="chevron-left"></i> Voltar
        </button>
        <div style="display:flex;gap:10px">
          <button class="btn btn-outline" onclick="window.print()">
            <i data-lucide="printer"></i> Imprimir
          </button>
          <button class="btn btn-green" onclick="Pages['nova-venda']._finalizar()">
            <i data-lucide="check-circle"></i> Finalizar Venda
          </button>
        </div>
      </div>
    `;
  },

  _nextStep() {
    if (this._venda.step < 3) {
      this._venda.step++;
      this._render();
    }
  },

  _prevStep() {
    if (this._venda.step > 0) {
      this._venda.step--;
      this._render();
    }
  },

  async _finalizar() {
    const { total, descontoVal, parcelas } = this._calcTotais();
    App.setLoading(true);
    try {
      await apiFetch('/vendas', {
        method: 'POST',
        body: JSON.stringify({
          clienteId:      this._venda.avulso ? null : this._venda.cliente?.id,
          clienteNome:    this._venda.avulso ? 'Avulso' : this._venda.cliente?.nome,
          funcionarioId:  App.user?.id,
          itens:          this._venda.itens.map(i => ({ produtoId: i.produtoId, quantidade: i.quantidade })),
          total,
          desconto:       descontoVal,
          formaPagamento: this._venda.formaPagamento,
          parcelas,
          taxaJuros:      this._parcelavel() ? (this._venda.juros || 0) : 0,
        }),
      });
      showToast('Venda finalizada com sucesso!', 'success');
      Router.navigate('vendas');
    } catch (err) {
      showToast(err.message || 'Erro ao finalizar venda.', 'error');
    } finally {
      App.setLoading(false);
    }
  },
};
