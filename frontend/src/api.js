import axios from 'axios'

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
})

export function msg(e) {
  const d = e.response?.data
  if (!d) return 'Não foi possível conectar à API'
  const campos = d.campos ? ` (${Object.values(d.campos).join('; ')})` : ''
  return (d.mensagem || 'Erro inesperado') + campos
}

export async function tentar(erro, fn) {
  erro.value = ''
  try {
    await fn()
  } catch (e) {
    erro.value = msg(e)
  }
}