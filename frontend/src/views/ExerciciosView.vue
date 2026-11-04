<script setup>
import { ref, onMounted } from 'vue'
import { api, tentar } from '../api'

const erro = ref('')
const lista = ref([])
const form = ref({ nome: '', grupoMuscular: '' })
const filtro = ref({ nome: '', grupoMuscular: '' })

const carregar = () => tentar(erro, async () => {
  const params = {
    nome: filtro.value.nome || undefined,
    grupoMuscular: filtro.value.grupoMuscular || undefined,
  }
  lista.value = (await api.get('/exercicios', { params })).data
})

const salvar = () => tentar(erro, async () => {
  await api.post('/exercicios', form.value)
  form.value = { nome: '', grupoMuscular: '' }
  await carregar()
})

const excluir = (id) => tentar(erro, async () => {
  await api.delete(`/exercicios/${id}`)
  await carregar()
})

onMounted(carregar)
</script>

<template>
  <section>
    <h2>Exercícios</h2>
    <p v-if="erro" class="erro">{{ erro }}</p>
    <form @submit.prevent="salvar">
      <input v-model="form.nome" placeholder="Nome" required />
      <input v-model="form.grupoMuscular" placeholder="Grupo muscular" required />
      <button>Cadastrar</button>
    </form>
    <form @submit.prevent="carregar">
      <input v-model="filtro.nome" placeholder="Filtrar por nome" />
      <input v-model="filtro.grupoMuscular" placeholder="Filtrar por grupo" />
      <button>Filtrar</button>
    </form>
    <ul>
      <li v-for="e in lista" :key="e.id">
        <span>{{ e.nome }} <small>({{ e.grupoMuscular }})</small></span>
        <button class="perigo dir" @click="excluir(e.id)">Excluir</button>
      </li>
    </ul>
  </section>
</template>