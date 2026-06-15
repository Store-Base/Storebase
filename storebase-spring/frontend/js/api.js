const API_BASE = 'http://localhost:8080/api';
const USE_MOCK = true;


let mockProdutos = [
  { id:1, nome:'Notebook Dell Inspiron 15', codigo:'NB-001', categoria:'Computadores',  precoVenda:3499.00, precoCusto:2600.00, quantidadeEstoque:24 },
  { id:2, nome:'Mouse Logitech MX Master',  codigo:'MS-002', categoria:'Periféricos',   precoVenda:89.90,   precoCusto:55.00,   quantidadeEstoque:45 },
  { id:3, nome:'Teclado Mecânico Redragon', codigo:'TC-003', categoria:'Periféricos',   precoVenda:249.90,  precoCusto:180.00,  quantidadeEstoque:18 },
  { id:4, nome:'Monitor LG 24" Full HD',    codigo:'MN-004', categoria:'Monitores',     precoVenda:899.00,  precoCusto:650.00,  quantidadeEstoque:12 },
  { id:5, nome:'Headset Gamer HyperX',      codigo:'HS-005', categoria:'Áudio',         precoVenda:189.90,  precoCusto:130.00,  quantidadeEstoque:32 },
  { id:6, nome:'HD Externo Seagate 1TB',    codigo:'HD-006', categoria:'Armazenamento', precoVenda:279.90,  precoCusto:200.00,  quantidadeEstoque:8  },
  { id:7, nome:'Webcam Logitech C920',      codigo:'WC-007', categoria:'Acessórios',    precoVenda:159.90,  precoCusto:110.00,  quantidadeEstoque:15 },
  { id:8, nome:'Hub USB-C Anker 7-em-1',    codigo:'HB-008', categoria:'Acessórios',    precoVenda:119.90,  precoCusto:80.00,   quantidadeEstoque:5  },
  { id:9, nome:'SSD Kingston 500GB',        codigo:'SS-009', categoria:'Armazenamento', precoVenda:349.90,  precoCusto:260.00,  quantidadeEstoque:3  },
  { id:10,nome:'Cadeira Gamer DXRacer',     codigo:'CG-010', categoria:'Cadeiras',      precoVenda:1299.00, precoCusto:950.00,  quantidadeEstoque:7  },
];

let mockClientes = [
  { id:1, nome:'Maria Silva',    cpf:'123.456.789-00', email:'maria@email.com',    endereco:'Rua das Flores, 123 - Centro',          telefone:'(43) 99901-1111' },
  { id:2, nome:'João Oliveira',  cpf:'234.567.890-11', email:'joao@email.com',     endereco:'Av. Brasil, 456 - Jardim das Nações',   telefone:'(43) 99902-2222' },
  { id:3, nome:'Ana Costa',      cpf:'345.678.901-22', email:'ana@email.com',      endereco:'Rua XV de Novembro, 789 - Boa Vista',   telefone:'(43) 99903-3333' },
  { id:4, nome:'Pedro Santos',   cpf:'456.789.012-33', email:'pedro@email.com',    endereco:'Av. Paraná, 101 - Vila Nova',           telefone:'(43) 99904-4444' },
  { id:5, nome:'Lucas Ferreira', cpf:'567.890.123-44', email:'lucas@email.com',    endereco:'Rua Sete de Setembro, 202 - Centro',    telefone:'(43) 99905-5555' },
  { id:6, nome:'Carla Mendes',   cpf:'678.901.234-55', email:'carla@email.com',    endereco:'Rua Mal. Deodoro, 303 - Jardim Europa', telefone:'(43) 99906-6666' },
  { id:7, nome:'Roberto Lima',   cpf:'789.012.345-66', email:'roberto@email.com',  endereco:'Av. Santos Dumont, 404 - São João',    telefone:'(43) 99907-7777' },
  { id:8, nome:'Fernanda Souza', cpf:'890.123.456-77', email:'fernanda@email.com', endereco:'Rua Tiradentes, 505 - Vila Operária',   telefone:'(43) 99908-8888' },
];

let mockFuncionarios = [
  { id:1, nome:'Leonardo Marino', cargo:'ADMINISTRADOR',   salario:5800.00, login:'leo.admin',  senha:'admin123'  },
  { id:2, nome:'Vinicius Vendas', cargo:'VENDEDOR',        salario:2800.00, login:'vini.vendas',senha:'vend123'   },
  { id:3, nome:'Carlos Estoque',  cargo:'GERENTE_ESTOQUE', salario:3200.00, login:'car.estoque',senha:'est123'    },
];

let mockVendas = [
  {
    id:7, clienteId:1, clienteNome:'Maria Silva',    funcionarioId:2, funcionarioNome:'Vinicius Vendas',
    itens:[ {produtoId:1,nomeProduto:'Notebook Dell Inspiron 15',quantidade:1,precoUnitario:3499.00} ],
    total:3499.00, desconto:0, formaPagamento:'PIX',           dataHora:'2026-05-21T13:20:00'
  },
  {
    id:6, clienteId:2, clienteNome:'João Oliveira',  funcionarioId:2, funcionarioNome:'Vinicius Vendas',
    itens:[ {produtoId:2,nomeProduto:'Mouse Logitech MX Master',quantidade:2,precoUnitario:89.90},
            {produtoId:5,nomeProduto:'Headset Gamer HyperX',quantidade:1,precoUnitario:189.90} ],
    total:369.70, desconto:0, formaPagamento:'CARTAO_CREDITO',  dataHora:'2026-05-20T10:45:00'
  },
  {
    id:5, clienteId:3, clienteNome:'Ana Costa',      funcionarioId:2, funcionarioNome:'Vinicius Vendas',
    itens:[ {produtoId:4,nomeProduto:'Monitor LG 24" Full HD',quantidade:1,precoUnitario:899.00} ],
    total:854.05, desconto:44.95, formaPagamento:'DINHEIRO',    dataHora:'2026-05-19T15:30:00'
  },
  {
    id:4, clienteId:null, clienteNome:'Avulso',       funcionarioId:2, funcionarioNome:'Vinicius Vendas',
    itens:[ {produtoId:7,nomeProduto:'Webcam Logitech C920',quantidade:1,precoUnitario:159.90} ],
    total:159.90, desconto:0, formaPagamento:'PIX',             dataHora:'2026-05-18T09:10:00'
  },
  {
    id:3, clienteId:4, clienteNome:'Pedro Santos',   funcionarioId:1, funcionarioNome:'Leonardo Marino',
    itens:[ {produtoId:10,nomeProduto:'Cadeira Gamer DXRacer',quantidade:1,precoUnitario:1299.00} ],
    total:1169.10, desconto:129.90, formaPagamento:'BOLETO',   dataHora:'2026-05-17T16:00:00'
  },
  {
    id:2, clienteId:5, clienteNome:'Lucas Ferreira', funcionarioId:2, funcionarioNome:'Vinicius Vendas',
    itens:[ {produtoId:3,nomeProduto:'Teclado Mecânico Redragon',quantidade:1,precoUnitario:249.90},
            {produtoId:2,nomeProduto:'Mouse Logitech MX Master',quantidade:1,precoUnitario:89.90} ],
    total:339.80, desconto:0, formaPagamento:'CARTAO_DEBITO',  dataHora:'2026-05-16T14:22:00'
  },
  {
    id:1, clienteId:6, clienteNome:'Carla Mendes',   funcionarioId:2, funcionarioNome:'Vinicius Vendas',
    itens:[ {produtoId:6,nomeProduto:'HD Externo Seagate 1TB',quantidade:1,precoUnitario:279.90} ],
    total:279.90, desconto:0, formaPagamento:'PIX',            dataHora:'2026-05-15T11:00:00'
  },
];

let mockOrcamentos = [
  { id:5, clienteId:1, clienteNome:'Maria Silva',    itens:[{produtoId:1,nomeProduto:'Notebook Dell Inspiron 15',quantidade:1,precoUnitario:3499}], total:3499.00, status:'ABERTO',  data:'2026-05-21' },
  { id:4, clienteId:2, clienteNome:'João Oliveira',  itens:[{produtoId:2,nomeProduto:'Mouse Logitech MX Master',quantidade:2,precoUnitario:89.90},{produtoId:3,nomeProduto:'Teclado Mecânico Redragon',quantidade:1,precoUnitario:249.90}], total:429.70, status:'ABERTO',  data:'2026-05-20' },
  { id:3, clienteId:3, clienteNome:'Ana Costa',      itens:[{produtoId:4,nomeProduto:'Monitor LG 24" Full HD',quantidade:1,precoUnitario:899}], total:899.00, status:'FECHADO', data:'2026-05-18' },
  { id:2, clienteId:5, clienteNome:'Lucas Ferreira', itens:[{produtoId:5,nomeProduto:'Headset Gamer HyperX',quantidade:1,precoUnitario:189.90}], total:189.90, status:'FECHADO', data:'2026-05-15' },
  { id:1, clienteId:4, clienteNome:'Pedro Santos',   itens:[{produtoId:8,nomeProduto:'Hub USB-C Anker 7-em-1',quantidade:2,precoUnitario:119.90}], total:239.80, status:'ABERTO',  data:'2026-05-14' },
];

let _nextId = { produto:11, cliente:9, funcionario:4, venda:8, orcamento:6 };

function _pagedResult(arr, page, size) {
  const start = page * size;
  return {
    content: arr.slice(start, start + size),
    totalElements: arr.length,
    totalPages: Math.ceil(arr.length / size),
    currentPage: page,
    size,
  };
}


async function mockFetch(endpoint, options = {}) {
  await new Promise(r => setTimeout(r, 80));
  const method = options.method || 'GET';
  const body = options.body ? JSON.parse(options.body) : null;

  
  if (endpoint === '/auth/login' && method === 'POST') {
    const func = mockFuncionarios.find(f => f.login === body.login && f.senha === body.senha);
    if (!func) throw { status: 401, message: 'Usuário ou senha inválidos.' };
    return { id: func.id, nome: func.nome, cargo: func.cargo, token: 'mock-token-' + func.id };
  }

  
  if (endpoint.startsWith('/produtos') && method === 'GET') {
    const url = new URL('http://x' + endpoint);
    const search = (url.searchParams.get('search') || '').toLowerCase();
    const page = Number(url.searchParams.get('page') || 0);
    const size = Number(url.searchParams.get('size') || 10);
    let arr = [...mockProdutos];
    if (search) arr = arr.filter(p => p.nome.toLowerCase().includes(search) || p.codigo.toLowerCase().includes(search) || p.categoria.toLowerCase().includes(search));
    return _pagedResult(arr, page, size);
  }
  if (endpoint === '/produtos' && method === 'POST') {
    const np = { ...body, id: _nextId.produto++ };
    mockProdutos.push(np);
    return np;
  }
  if (endpoint.match(/^\/produtos\/\d+$/) && method === 'PUT') {
    const id = Number(endpoint.split('/')[2]);
    const idx = mockProdutos.findIndex(p => p.id === id);
    if (idx < 0) throw { status: 404, message: 'Produto não encontrado.' };
    mockProdutos[idx] = { ...mockProdutos[idx], ...body, id };
    return mockProdutos[idx];
  }
  if (endpoint.match(/^\/produtos\/\d+$/) && method === 'DELETE') {
    const id = Number(endpoint.split('/')[2]);
    mockProdutos = mockProdutos.filter(p => p.id !== id);
    return {};
  }

  
  if (endpoint.startsWith('/clientes') && !endpoint.includes('/historico') && method === 'GET') {
    const url = new URL('http://x' + endpoint);
    const search = (url.searchParams.get('search') || '').toLowerCase();
    const page = Number(url.searchParams.get('page') || 0);
    const size = Number(url.searchParams.get('size') || 10);
    let arr = [...mockClientes];
    if (search) arr = arr.filter(c => c.nome.toLowerCase().includes(search) || c.cpf.includes(search) || c.email.toLowerCase().includes(search));
    return _pagedResult(arr, page, size);
  }
  if (endpoint.match(/^\/clientes\/\d+\/historico$/) && method === 'GET') {
    const id = Number(endpoint.split('/')[2]);
    const vendas = mockVendas.filter(v => v.clienteId === id);
    return vendas;
  }
  if (endpoint === '/clientes' && method === 'POST') {
    const nc = { ...body, id: _nextId.cliente++ };
    mockClientes.push(nc);
    return nc;
  }
  if (endpoint.match(/^\/clientes\/\d+$/) && method === 'PUT') {
    const id = Number(endpoint.split('/')[2]);
    const idx = mockClientes.findIndex(c => c.id === id);
    if (idx < 0) throw { status: 404, message: 'Cliente não encontrado.' };
    mockClientes[idx] = { ...mockClientes[idx], ...body, id };
    return mockClientes[idx];
  }
  if (endpoint.match(/^\/clientes\/\d+$/) && method === 'DELETE') {
    const id = Number(endpoint.split('/')[2]);
    mockClientes = mockClientes.filter(c => c.id !== id);
    return {};
  }

  
  if (endpoint === '/funcionarios' && method === 'GET') {
    return mockFuncionarios;
  }
  if (endpoint === '/funcionarios' && method === 'POST') {
    const nf = { ...body, id: _nextId.funcionario++ };
    mockFuncionarios.push(nf);
    return nf;
  }
  if (endpoint.match(/^\/funcionarios\/\d+$/) && method === 'PUT') {
    const id = Number(endpoint.split('/')[2]);
    const idx = mockFuncionarios.findIndex(f => f.id === id);
    if (idx < 0) throw { status: 404, message: 'Funcionário não encontrado.' };
    mockFuncionarios[idx] = { ...mockFuncionarios[idx], ...body, id };
    return mockFuncionarios[idx];
  }
  if (endpoint.match(/^\/funcionarios\/\d+$/) && method === 'DELETE') {
    const id = Number(endpoint.split('/')[2]);
    mockFuncionarios = mockFuncionarios.filter(f => f.id !== id);
    return {};
  }

  
  if (endpoint.startsWith('/orcamentos') && !endpoint.includes('/converter') && method === 'GET') {
    const url = new URL('http://x' + endpoint);
    const status = url.searchParams.get('status') || '';
    let arr = [...mockOrcamentos].sort((a,b) => b.id - a.id);
    if (status) arr = arr.filter(o => o.status === status);
    return arr;
  }
  if (endpoint === '/orcamentos' && method === 'POST') {
    const no = { ...body, id: _nextId.orcamento++, status: 'ABERTO', data: todayISO() };
    mockOrcamentos.push(no);
    return no;
  }
  if (endpoint.match(/^\/orcamentos\/\d+$/) && method === 'PUT') {
    const id = Number(endpoint.split('/')[2]);
    const idx = mockOrcamentos.findIndex(o => o.id === id);
    if (idx < 0) throw { status: 404, message: 'Orçamento não encontrado.' };
    mockOrcamentos[idx] = { ...mockOrcamentos[idx], ...body, id };
    return mockOrcamentos[idx];
  }
  if (endpoint.match(/^\/orcamentos\/\d+$/) && method === 'DELETE') {
    const id = Number(endpoint.split('/')[2]);
    mockOrcamentos = mockOrcamentos.filter(o => o.id !== id);
    return {};
  }
  if (endpoint.match(/^\/orcamentos\/\d+\/converter$/) && method === 'POST') {
    const id = Number(endpoint.split('/')[2]);
    const orc = mockOrcamentos.find(o => o.id === id);
    if (!orc) throw { status: 404, message: 'Orçamento não encontrado.' };
    const novaVenda = {
      id: _nextId.venda++,
      clienteId: orc.clienteId,
      clienteNome: orc.clienteNome,
      funcionarioId: App.user.id,
      funcionarioNome: App.user.nome,
      itens: orc.itens,
      total: orc.total,
      desconto: 0,
      formaPagamento: body.formaPagamento || 'PIX',
      dataHora: new Date().toISOString(),
    };
    mockVendas.unshift(novaVenda);
    const idx = mockOrcamentos.findIndex(o => o.id === id);
    mockOrcamentos[idx].status = 'FECHADO';
    return novaVenda;
  }

  
  if (endpoint.startsWith('/vendas') && !endpoint.match(/\/\d+$/) && method === 'GET') {
    const url = new URL('http://x' + endpoint);
    const di = url.searchParams.get('dataInicio') || '';
    const df = url.searchParams.get('dataFim') || '';
    const fp = url.searchParams.get('formaPagamento') || '';
    let arr = [...mockVendas];
    if (di) arr = arr.filter(v => v.dataHora.slice(0,10) >= di);
    if (df) arr = arr.filter(v => v.dataHora.slice(0,10) <= df);
    if (fp) arr = arr.filter(v => v.formaPagamento === fp);
    const totalVendas = arr.length;
    const receitaPeriodo = arr.reduce((s, v) => s + v.total, 0);
    return { vendas: arr, totalVendas, receitaPeriodo };
  }
  if (endpoint.match(/^\/vendas\/\d+$/) && method === 'GET') {
    const id = Number(endpoint.split('/')[2]);
    const v = mockVendas.find(v => v.id === id);
    if (!v) throw { status: 404, message: 'Venda não encontrada.' };
    return v;
  }
  if (endpoint === '/vendas' && method === 'POST') {
    
    for (const item of body.itens) {
      const p = mockProdutos.find(p => p.id === item.produtoId);
      if (p) p.quantidadeEstoque = Math.max(0, p.quantidadeEstoque - item.quantidade);
    }
    const nv = { ...body, id: _nextId.venda++, dataHora: new Date().toISOString(),
                  funcionarioId: App.user.id, funcionarioNome: App.user.nome };
    mockVendas.unshift(nv);
    return nv;
  }

  
  if (endpoint === '/estoque' && method === 'GET') {
    const items = mockProdutos.map(p => {
      let status = 'OK';
      if (p.quantidadeEstoque < 5)  status = 'CRITICO';
      else if (p.quantidadeEstoque < 10) status = 'BAIXO';
      return { produtoId: p.id, nomeProduto: p.nome, codigo: p.codigo, categoria: p.categoria, quantidade: p.quantidadeEstoque, status };
    });
    const alertas = items.filter(i => i.status !== 'OK');
    return { items, alertas };
  }
  if (endpoint === '/estoque/entrada' && method === 'POST') {
    const p = mockProdutos.find(p => p.id === body.produtoId);
    if (!p) throw { status: 404, message: 'Produto não encontrado.' };
    p.quantidadeEstoque += body.quantidade;
    return { produtoId: p.id, novaQuantidade: p.quantidadeEstoque };
  }
  if (endpoint === '/estoque/ajuste' && method === 'POST') {
    const p = mockProdutos.find(p => p.id === body.produtoId);
    if (!p) throw { status: 404, message: 'Produto não encontrado.' };
    p.quantidadeEstoque = body.quantidade;
    return { produtoId: p.id, novaQuantidade: p.quantidadeEstoque };
  }

  
  if (endpoint === '/dashboard/stats' && method === 'GET') {
    const hoje = todayISO();
    const vendasHoje = mockVendas.filter(v => v.dataHora.startsWith(hoje));
    const totalHoje = vendasHoje.reduce((s,v) => s+v.total, 0);
    const alertasCriticos = mockProdutos.filter(p => p.quantidadeEstoque < 5).length;
    const alertasBaixos   = mockProdutos.filter(p => p.quantidadeEstoque >= 5 && p.quantidadeEstoque < 10).length;
    const orcAbertos = mockOrcamentos.filter(o => o.status === 'ABERTO').length;
    const totalVendasMes = mockVendas.reduce((s,v) => s+v.total, 0);
    return {
      vendasHojeTotal: totalHoje,
      vendasHojeQtd: vendasHoje.length,
      totalClientes: mockClientes.length,
      alertasEstoque: alertasCriticos + alertasBaixos,
      alertasCriticos,
      alertasBaixos,
      orcamentosAbertos: orcAbertos,
      totalVendasMes,
      totalFuncionarios: mockFuncionarios.length,
    };
  }
  if (endpoint === '/dashboard/grafico' && method === 'GET') {
    const days = [];
    for (let i = 6; i >= 0; i--) {
      const d = new Date();
      d.setDate(d.getDate() - i);
      const ds = d.toISOString().slice(0,10);
      const total = mockVendas.filter(v => v.dataHora.startsWith(ds)).reduce((s,v) => s+v.total, 0);
      days.push({ data: ds.slice(5), total: total || Math.floor(Math.random()*5000+2000) });
    }
    return days;
  }
  if (endpoint === '/dashboard/ultimas-vendas' && method === 'GET') {
    return mockVendas.slice(0, 5);
  }

  
  if (endpoint === '/dashboard/stats-vendedor' && method === 'GET') {
    const url = new URL('http://x' + endpoint);
    const funcId = Number(url.searchParams.get('funcId'));
    const hoje = todayISO();
    const minhasVendas   = mockVendas.filter(v => v.funcionarioId === funcId);
    const vendasHoje     = minhasVendas.filter(v => v.dataHora.startsWith(hoje));
    const totalHoje      = vendasHoje.reduce((s,v) => s+v.total, 0);
    const totalMes       = minhasVendas.reduce((s,v) => s+v.total, 0);
    const meusOrcamentos = mockOrcamentos.filter(o => o.status === 'ABERTO').length;
    return {
      vendasHojeTotal: totalHoje,
      vendasHojeQtd: vendasHoje.length,
      totalClientes: mockClientes.length,
      meusOrcamentosAbertos: meusOrcamentos,
      totalVendasMes: totalMes,
      totalVendasQtd: minhasVendas.length,
    };
  }
  if (endpoint === '/dashboard/minhas-vendas' && method === 'GET') {
    const url = new URL('http://x' + endpoint);
    const funcId = Number(url.searchParams.get('funcId'));
    return mockVendas.filter(v => v.funcionarioId === funcId).slice(0, 5);
  }
  if (endpoint === '/dashboard/grafico-vendedor' && method === 'GET') {
    const url = new URL('http://x' + endpoint);
    const funcId = Number(url.searchParams.get('funcId'));
    const days = [];
    for (let i = 6; i >= 0; i--) {
      const d = new Date();
      d.setDate(d.getDate() - i);
      const ds = d.toISOString().slice(0,10);
      const total = mockVendas
        .filter(v => v.funcionarioId === funcId && v.dataHora.startsWith(ds))
        .reduce((s,v) => s+v.total, 0);
      days.push({ data: ds.slice(5), total: total || Math.floor(Math.random()*3000+500) });
    }
    return days;
  }

  
  if (endpoint === '/dashboard/stats-estoque' && method === 'GET') {
    const criticos = mockProdutos.filter(p => p.quantidadeEstoque < 5);
    const baixos   = mockProdutos.filter(p => p.quantidadeEstoque >= 5 && p.quantidadeEstoque < 10);
    const ok       = mockProdutos.filter(p => p.quantidadeEstoque >= 10);
    const totalVal = mockProdutos.reduce((s,p) => s + p.quantidadeEstoque * p.precoCusto, 0);
    return {
      totalProdutos: mockProdutos.length,
      produtosCriticos: criticos.length,
      produtosBaixos: baixos.length,
      produtosOK: ok.length,
      valorTotalEstoque: totalVal,
      alertas: [...criticos, ...baixos].map(p => ({
        produtoId: p.id,
        nomeProduto: p.nome,
        codigo: p.codigo,
        quantidade: p.quantidadeEstoque,
        status: p.quantidadeEstoque < 5 ? 'CRITICO' : 'BAIXO',
      })),
    };
  }

  
  if (endpoint.startsWith('/relatorios/vendas') && method === 'GET') {
    const totalGeral = mockVendas.reduce((s,v) => s+v.total, 0);
    const quantidadeTotal = mockVendas.length;
    const porPagamento = {};
    mockVendas.forEach(v => {
      porPagamento[v.formaPagamento] = (porPagamento[v.formaPagamento] || 0) + v.total;
    });
    return { totalGeral, quantidadeTotal, porPagamento };
  }
  if (endpoint.startsWith('/relatorios/produtos') && method === 'GET') {
    const contagem = {};
    mockVendas.forEach(v => v.itens.forEach(i => {
      contagem[i.nomeProduto] = (contagem[i.nomeProduto] || 0) + i.quantidade;
    }));
    const sorted = Object.entries(contagem).map(([produto, quantidadeVendida]) => ({ produto, quantidadeVendida }))
      .sort((a,b) => b.quantidadeVendida - a.quantidadeVendida);
    return sorted;
  }
  if (endpoint === '/relatorios/estoque' && method === 'GET') {
    return mockProdutos.map(p => ({
      nomeProduto: p.nome,
      codigo: p.codigo,
      quantidade: p.quantidadeEstoque,
      status: p.quantidadeEstoque < 5 ? 'CRITICO' : p.quantidadeEstoque < 10 ? 'BAIXO' : 'OK',
    }));
  }
  if (endpoint === '/relatorios/clientes' && method === 'GET') {
    return mockClientes.map(c => {
      const compras = mockVendas.filter(v => v.clienteId === c.id);
      return { ...c, totalCompras: compras.reduce((s,v)=>s+v.total,0), qtdCompras: compras.length };
    }).sort((a,b) => b.totalCompras - a.totalCompras);
  }
  if (endpoint.startsWith('/relatorios/faturamento') && method === 'GET') {
    const meses = ['Jan','Fev','Mar','Abr','Mai','Jun','Jul','Ago','Set','Out','Nov','Dez'];
    return meses.map((mes, idx) => {
      const receitaBruta  = Math.floor(Math.random() * 20000 + 10000);
      const receitaLiquida = Math.floor(receitaBruta * 0.65);
      return { mes, receitaBruta, receitaLiquida };
    });
  }

  throw { status: 404, message: 'Endpoint não encontrado: ' + endpoint };
}

async function apiFetch(endpoint, options = {}) {
  if (USE_MOCK) return mockFetch(endpoint, options);

  const token = localStorage.getItem('token');
  const res = await fetch(API_BASE + endpoint, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: 'Bearer ' + token } : {}),
      ...options.headers,
    },
  });

  if (res.status === 401) { App.logout(); return null; }
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw { status: res.status, message: err.message || 'Erro no servidor.' };
  }
  return res.json();
}
