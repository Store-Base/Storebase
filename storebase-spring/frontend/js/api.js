const API_BASE = 'http://localhost:8080';

async function apiFetch(endpoint, options = {}) {
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

  const data = await res.json();

  // Adaptador: rotas paginadas no frontend, lista simples no backend
  if ((endpoint.startsWith('/produtos') || endpoint.startsWith('/clientes')) &&
      (!options.method || options.method === 'GET') &&
      Array.isArray(data)) {
    const url = new URL('http://x' + endpoint);
    const page   = Number(url.searchParams.get('page') || 0);
    const size   = Number(url.searchParams.get('size') || 10);
    const search = (url.searchParams.get('search') || '').toLowerCase();

    let filtered = data;
    if (search && endpoint.startsWith('/produtos')) {
      filtered = data.filter(p =>
        p.nome?.toLowerCase().includes(search) ||
        p.codigo?.toLowerCase().includes(search) ||
        p.categoria?.toLowerCase().includes(search)
      );
    }
    if (search && endpoint.startsWith('/clientes')) {
      filtered = data.filter(c =>
        c.nome?.toLowerCase().includes(search) ||
        c.cpf?.includes(search) ||
        c.email?.toLowerCase().includes(search)
      );
    }

    const start = page * size;
    return {
      content:       filtered.slice(start, start + size),
      totalElements: filtered.length,
      totalPages:    Math.ceil(filtered.length / size),
      currentPage:   page,
      size,
    };
  }

  return data;
}
