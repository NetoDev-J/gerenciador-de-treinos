<script setup>
import { ref, onMounted } from 'vue'
import { api, tentar } from '../api'

const DIAS = ['SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO', 'DOMINGO']

const erro = ref('')
const divisoes = ref([])
const exercicios = ref([])
const dias = ref([])
const itens = ref([])
const divSel = ref(null)
const diaSel = ref(null)
const novaDivisao = ref('')
const novoDia = ref({ nome: '', grupoMuscular: '', diaSemana: 'SEGUNDA' })
const novoItem = ref({ exercicioId: '', series: 4, repeticoes: 10, descansoSegundos: 90 })

const carregarDivisoes = async () => { divisoes.value = (await api.get('/divisoes')).data }
const carregarDias = async () => { dias.value = (await api.get(`/divisoes/${divSel.value.id}/dias`)).data }
const carregarItens = async () => { itens.value = (await api.get(`/dias/${diaSel.value.id}/itens`)).data }

const criarDivisao = () => tentar(erro, async () => {
  await api.post('/divisoes', { nome: novaDivisao.value })
  novaDivisao.value = ''
  await carregarDivisoes()
})

const ativar = (id) => tentar(erro, async () => {
  await api.patch(`/divisoes/${id}/ativar`)
  await carregarDivisoes()
})

const excluirDivisao = (id) => tentar(erro, async () => {
  await api.delete(`/divisoes/${id}`)
  if (divSel.value?.id === id) {
    divSel.value = null; diaSel.value = null; dias.value = []; itens.value = []
  }
  await carregarDivisoes()
})

const selecionarDivisao = (d) => tentar(erro, async () => {
  divSel.value = d; diaSel.value = null; itens.value = []
  await carregarDias()
})

const criarDia = () => tentar(erro, async () => {
  await api.post(`/divisoes/${divSel.value.id}/dias`, novoDia.value)
  novoDia.value = { nome: '', grupoMuscular: '', diaSemana: 'SEGUNDA' }
  await carregarDias()
})

const excluirDia = (id) => tentar(erro, async () => {
  await api.delete(`/dias/${id}`)
  if (diaSel.value?.id === id) { diaSel.value = null; itens.value = [] }
  await carregarDias()
})

const selecionarDia = (d) => tentar(erro, async () => {
  diaSel.value = d
  await carregarItens()
})

const criarItem = () => tentar(erro, async () => {
  await api.post(`/dias/${diaSel.value.id}/itens`, novoItem.value)
  novoItem.value.exercicioId = ''
  await carregarItens()
})

const excluirItem = (id) => tentar(erro, async () => {
  await api.delete(`/itens/${id}`)
  await carregarItens()
})

onMounted(() => tentar(erro, async () => {
  await carregarDivisoes()
  exercicios.value = (await api.get('/exercicios')).data
}))
</script>

<template>
  <p v-if="erro" class="erro">{{ erro }}</p>
  <div class="colunas">
    <section>
      <h2>Divisões</h2>
      <form @submit.prevent="criarDivisao">
        <input v-model="novaDivisao" placeholder="Nome (ex.: ABC)" required />
        <button>Criar</button>
      </form>
      <ul>
        <li v-for="d in divisoes" :key="d.id" :class="{ sel: divSel?.id === d.id }">
          <a href="#" @click.prevent="selecionarDivisao(d)">{{ d.nome }}</a>
          <span v-if="d.ativa" class="tag">ativa</span>
          <button v-else class="dir" @click="ativar(d.id)">Ativar</button>
          <button class="perigo" @click="excluirDivisao(d.id)">✕</button>
        </li>
      </ul>
    </section>

    <section v-if="divSel">
      <h2>Dias de {{ divSel.nome }}</h2>
      <form @submit.prevent="criarDia">
        <input v-model="novoDia.nome" placeholder="Nome (ex.: Treino A)" required />
        <input v-model="novoDia.grupoMuscular" placeholder="Grupos (ex.: Peito e Tríceps)" required />
        <select v-model="novoDia.diaSemana">
          <option v-for="d in DIAS" :key="d">{{ d }}</option>
        </select>
        <button>Adicionar dia</button>
      </form>
      <ul>
        <li v-for="d in dias" :key="d.id" :class="{ sel: diaSel?.id === d.id }">
          <a href="#" @click.prevent="selecionarDia(d)">{{ d.diaSemana }} — {{ d.nome }}</a>
          <small>({{ d.grupoMuscular }})</small>
          <button class="perigo dir" @click="excluirDia(d.id)">✕</button>
        </li>
      </ul>
    </section>

    <section v-if="diaSel">
      <h2>Exercícios de {{ diaSel.nome }}</h2>
      <form @submit.prevent="criarItem">
        <select v-model.number="novoItem.exercicioId" required>
          <option value="" disabled>Exercício…</option>
          <option v-for="e in exercicios" :key="e.id" :value="e.id">{{ e.nome }}</option>
        </select>
        <input v-model.number="novoItem.series" type="number" min="1" title="Séries" required />
        <input v-model.number="novoItem.repeticoes" type="number" min="1" title="Repetições" required />
        <input v-model.number="novoItem.descansoSegundos" type="number" min="10" max="600" title="Descanso (s)" required />
        <button>Adicionar</button>
      </form>
      <ul>
        <li v-for="i in itens" :key="i.id">
          {{ i.ordem }}. {{ i.exercicioNome }} — {{ i.series }}x{{ i.repeticoes }} · {{ i.descansoSegundos }}s
          <button class="perigo dir" @click="excluirItem(i.id)">✕</button>
        </li>
      </ul>
    </section>
  </div>
</template>