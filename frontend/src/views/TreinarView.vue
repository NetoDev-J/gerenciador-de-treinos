<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { api, tentar } from '../api'

const NOMES = ['DOMINGO', 'SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO']

const erro = ref('')
const carregado = ref(false)
const divisao = ref(null)
const dias = ref([])
const diaId = ref(null)
const itens = ref([])
const sessao = ref(null)
const series = ref([])
const entradas = ref({})

function hoje() {
  const d = new Date()
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}`
}

async function abrirDia() {
  cancelar()
  sessao.value = null
  series.value = []
  itens.value = (await api.get(`/dias/${diaId.value}/itens`)).data
  entradas.value = Object.fromEntries(
    itens.value.map((i) => [i.exercicioId, { repeticoes: i.repeticoes, carga: 0 }]))
  const params = { diaTreinoId: diaId.value, dataInicio: hoje(), dataFim: hoje() }
  const { data } = await api.get('/sessoes', { params })
  if (data.length) {
    sessao.value = data[0]
    await carregarSeries()
  }
}

const carregarSeries = async () => {
  series.value = (await api.get(`/sessoes/${sessao.value.id}/series`)).data
}

const trocarDia = () => tentar(erro, abrirDia)

const iniciar = () => tentar(erro, async () => {
  sessao.value = (await api.post('/sessoes', { diaTreinoId: diaId.value, data: hoje() })).data
  await carregarSeries()
})

const registrar = (item) => tentar(erro, async () => {
  prepararAudio() // precisa rodar dentro do clique para o navegador liberar o som
  const e = entradas.value[item.exercicioId]
  await api.post(`/sessoes/${sessao.value.id}/series`, {
    exercicioId: item.exercicioId,
    repeticoes: e.repeticoes,
    carga: e.carga,
  })
  await carregarSeries()
  iniciarDescanso(item.descansoSegundos)
})

const seriesDe = (item) => series.value.filter((s) => s.exercicioId === item.exercicioId)

// --- Cronômetro de descanso ---
const restante = ref(0)
const terminou = ref(false)
let intervalo = null
let fim = 0
let audio = null

const relogio = computed(() => {
  const m = String(Math.floor(restante.value / 60)).padStart(2, '0')
  const s = String(restante.value % 60).padStart(2, '0')
  return `${m}:${s}`
})

function prepararAudio() {
  audio ??= new (window.AudioContext || window.webkitAudioContext)()
  if (audio.state === 'suspended') audio.resume()
}

function bipe() {
  if (!audio) return
  for (let i = 0; i < 3; i++) {
    const osc = audio.createOscillator()
    osc.frequency.value = 880
    osc.connect(audio.destination)
    osc.start(audio.currentTime + i * 0.35)
    osc.stop(audio.currentTime + i * 0.35 + 0.2)
  }
  navigator.vibrate?.([300, 150, 300])
}

function iniciarDescanso(segundos) {
  clearInterval(intervalo)
  terminou.value = false
  fim = Date.now() + segundos * 1000
  restante.value = segundos
  intervalo = setInterval(() => {
    restante.value = Math.max(0, Math.ceil((fim - Date.now()) / 1000))
    if (restante.value === 0) {
      clearInterval(intervalo)
      terminou.value = true
      bipe()
    }
  }, 250)
}

function cancelar() {
  clearInterval(intervalo)
  restante.value = 0
  terminou.value = false
}

onMounted(() => tentar(erro, async () => {
  const { data } = await api.get('/divisoes', { params: { ativa: true } })
  divisao.value = data[0] ?? null
  if (divisao.value) {
    dias.value = (await api.get(`/divisoes/${divisao.value.id}/dias`)).data
    const doDia = dias.value.find((d) => d.diaSemana === NOMES[new Date().getDay()])
    diaId.value = (doDia ?? dias.value[0])?.id ?? null
    if (diaId.value) await abrirDia()
  }
  carregado.value = true
}))

onUnmounted(cancelar)
</script>

<template>
  <p v-if="erro" class="erro">{{ erro }}</p>
  <section v-if="carregado && !divisao">
    Nenhuma divisão ativa. Ative uma em <RouterLink to="/divisoes">Divisões</RouterLink>.
  </section>
  <section v-else-if="carregado && !dias.length">
    A divisão <strong>{{ divisao.nome }}</strong> ainda não tem dias.
    Monte o treino em <RouterLink to="/divisoes">Divisões</RouterLink>.
  </section>

  <template v-if="divisao && dias.length">
    <section>
      <h2>Divisão {{ divisao.nome }}</h2>
      <select v-model.number="diaId" @change="trocarDia">
        <option v-for="d in dias" :key="d.id" :value="d.id">
          {{ d.diaSemana }} — {{ d.nome }} ({{ d.grupoMuscular }})
        </option>
      </select>
      <button v-if="!sessao && itens.length" style="margin-left: .5rem" @click="iniciar">
        Iniciar treino de hoje
      </button>
      <p v-if="!itens.length">Este dia ainda não tem exercícios.</p>
    </section>

    <template v-if="sessao">
      <div v-for="i in itens" :key="i.id" class="card">
        <h2>{{ i.exercicioNome }}</h2>
        <p class="plano">Plano: {{ i.series }}x{{ i.repeticoes }} · descanso {{ i.descansoSegundos }}s</p>
        <ul>
          <li v-for="s in seriesDe(i)" :key="s.id">
            Série {{ s.numeroSerie }}: {{ s.repeticoes }} reps × {{ s.carga }} kg
          </li>
        </ul>
        <form style="margin: .75rem 0 0" @submit.prevent="registrar(i)">
          <input v-model.number="entradas[i.exercicioId].repeticoes" type="number" min="1" title="Repetições" required />
          <input v-model.number="entradas[i.exercicioId].carga" type="number" min="0" step="0.5" title="Carga (kg)" required />
          <button>Registrar série</button>
        </form>
      </div>
    </template>
  </template>

  <div v-if="restante > 0 || terminou" class="cronometro" :class="{ fim: terminou }">
    <span class="tempo">{{ relogio }}</span>
    <span>{{ terminou ? 'Descanso encerrado! Próxima série.' : 'Descansando…' }}</span>
    <button class="dir" @click="cancelar">{{ terminou ? 'OK' : 'Pular' }}</button>
  </div>
</template>