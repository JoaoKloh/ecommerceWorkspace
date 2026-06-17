"use client"

import { useEffect, useState, useRef } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Field, FieldLabel } from "@/components/ui/field"
import { Loader2, Calendar, Clock, User, MapPin } from "lucide-react"
import api from "@/services/api"
import axios from "axios"
import { toast } from "sonner"

interface PedidoData {
  nome: string
  telefone: string
  cpf: string 
  dataNascimento: string
  cep: string
  rua: string
  numero: string
  complemento: string
  bairro: string
  cidade: string
  estado: string
  dataRetirada: string
  horarioRetirada: string
  observacoes: string
}

interface PedidoStepProps {
  formData: PedidoData
  updateFormData: (data: Partial<PedidoData>) => void
  onNext: () => void 
  isPending: boolean       
  paymentActive: boolean   
}

// 🍏 Funções auxiliares para aplicação de máscaras em tempo real
const formatarCPF = (value: string) => {
  const limpo = value.replace(/\D/g, "")
  return limpo
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d{1,2})$/, "$1-$2")
}

const formatarCEP = (value: string) => {
  const limpo = value.replace(/\D/g, "")
  return limpo.replace(/^(\d{5})(\d)/, "$1-$2")
}

const formatarTelefone = (value: string) => {
  const limpo = value.replace(/\D/g, "")
  if (limpo.length <= 10) {
    return limpo
      .replace(/(\d{2})(\d)/, "($1) $2")
      .replace(/(\d{4})(\d)/, "$1-$2")
  } else {
    return limpo
      .replace(/(\d{2})(\d)/, "($1) $2")
      .replace(/(\d{5})(\d)/, "$1-$2")
  }
}

export function PedidoStep({ 
  formData, 
  updateFormData, 
  onNext,
  isPending,
  paymentActive 
}: PedidoStepProps) {
  const [loading, setLoading] = useState(false)
  const [fetchingUser, setFetchingUser] = useState(false)
  const [buscarCepLoading, setBuscarCepLoading] = useState(false) 
  const hasFetched = useRef(false)
  
  // 🛡️ Guardião para evitar loops repetidos de requisição no mesmo CEP
  const ultimoCepBuscado = useRef("")

  if (paymentActive) return null

  // 1. Efeito para carregar os dados iniciais do Perfil
  useEffect(() => {
    const carregarDadosIniciais = async () => {
      if (hasFetched.current) return
      try {
        setFetchingUser(true)
        const response = await api.getProfile()
        const userData = response.data ? response.data : response
    
        if (userData) {
          updateFormData({
            nome: userData.nome || "",
            telefone: userData.telefone ? formatarTelefone(userData.telefone) : "",
            cpf: userData.cpf ? formatarCPF(userData.cpf) : "", 
            dataNascimento: userData.dataNascimento || "",
            cep: userData.endereco?.cep ? formatarCEP(userData.endereco.cep) : "",
            rua: userData.endereco?.rua || "",
            numero: userData.endereco?.numero || "",
            complemento: userData.endereco?.complemento || "",
            bairro: userData.endereco?.bairro || "",
            cidade: userData.endereco?.cidade || "",
            estado: userData.endereco?.estado || "",
          })
        }
      } catch (error: any) {
        // Captura a mensagem vinda especificamente do seu backend
        const mensagemDoBackend = error.response?.data?.message 
                               || error.response?.data 
                               || "Erro ao carregar os dados do perfil.";
    
        console.error("Erro do Backend:", mensagemDoBackend);
      } finally {
        setFetchingUser(false)
        hasFetched.current = true
      }
    }

    carregarDadosIniciais()
  }, [updateFormData]) // Executa uma vez ao montar o componente de forma segura


  // 2. Consulta automática ao digitar o CEP (Separado, fora do outro useEffect)
  useEffect(() => {
    const cepLimpo = formData.cep.replace(/\D/g, "")

    // Só dispara se tiver 8 números E se for diferente do último que já buscamos
    if (cepLimpo.length === 8 && cepLimpo !== ultimoCepBuscado.current) {
      const consultarCep = async () => {
        try {
          setBuscarCepLoading(true)
          ultimoCepBuscado.current = cepLimpo // Registra imediatamente para bloquear chamadas paralelas

          const response = await axios.get(`https://brasilapi.com.br/api/cep/v1/${cepLimpo}`)
          
          if (response.data) {
            updateFormData({
              rua: response.data.street || "",
              bairro: response.data.neighborhood || "",
              cidade: response.data.city || "",
              estado: response.data.state || ""
            })
          }
        } catch (error) {
          console.error("Erro ao buscar CEP:", error)
          toast.error("Não conseguimos localizar o CEP informado.")
        } finally {
          setBuscarCepLoading(false)
        }
      }

      consultarCep()
    } else if (cepLimpo.length < 8) {
      // Se o usuário apagar o CEP, limpa a referência para permitir nova busca futuramente
      ultimoCepBuscado.current = ""
    }
  }, [formData.cep, updateFormData])


  // 3. Envio do Formulário tratando mensagens reais do Backend
  const handleProsseguir = async (e: React.FormEvent) => {
    e.preventDefault()
    if (loading || isPending || paymentActive) return
    
    setLoading(true)
    try {
      await api.upsertProfile({
        nome: formData.nome,
        telefone: formData.telefone.replace(/\D/g, ""),
        cpf: formData.cpf.replace(/\D/g, ""), 
        dataNascimento: formData.dataNascimento,
        endereco: {
          cep: formData.cep.replace(/\D/g, ""),
          rua: formData.rua,
          numero: formData.numero,
          complemento: formData.complemento,
          bairro: formData.bairro,
          cidade: formData.cidade,
          estado: formData.estado
        }
      })

      onNext()
    } catch (error: any) {
      // Captura a validação real que o Spring Boot retornou no salvamento
      const erroSalvar = error.response?.data?.message 
                      || error.response?.data 
                      || "Erro ao processar seus dados. Tente novamente.";
      
      console.error("Erro de validação no envio:", erroSalvar);
      toast.error(erroSalvar);
    } finally {
      setLoading(false)
    }
  }

  // 4. Validações e Estados de Renderização (Fora de qualquer Hook)
  const isValid = formData.nome && formData.telefone && formData.cpf && formData.cep && formData.dataRetirada && formData.horarioRetirada

  if (fetchingUser) {
    return (
      <Card className="rounded-xl border border-border bg-card shadow-sm">
        <CardContent className="h-48 flex flex-col items-center justify-center gap-2 text-muted-foreground">
          <Loader2 className="animate-spin h-6 w-6 text-primary" />
          <span className="text-xs font-medium">Sincronizando Marie e Anne...</span>
        </CardContent>
      </Card>
    )
  }

  const isLoadingActive = loading || isPending

  return (
    <div className="space-y-5 animate-in fade-in duration-300">
      
      {/* 1. Dados Pessoais */}
      <Card className="rounded-xl border border-border bg-card shadow-sm overflow-hidden">
        <CardHeader className="p-4 sm:p-6 pb-3">
          <div className="flex items-center gap-2">
            <User className="h-4 w-4 text-muted-foreground" />
            <CardTitle className="text-base sm:text-lg font-bold tracking-tight">Dados Pessoais</CardTitle>
          </div>
        </CardHeader>
        <CardContent className="p-4 sm:p-6 pt-0 space-y-4">
          <Field>
            <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Nome Completo</FieldLabel>
            <Input
              value={formData.nome}
              onChange={(e) => updateFormData({ nome: e.target.value })}
              className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
              required
            />
          </Field>

          <Field>
            <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">CPF (Obrigatório para emissão de PIX)</FieldLabel>
            <Input
              value={formData.cpf}
              placeholder="000.000.000-00"
              maxLength={14}
              onChange={(e) => updateFormData({ cpf: formatarCPF(e.target.value) })}
              className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
              required
            />
          </Field>

          <div className="grid gap-4 grid-cols-1 sm:grid-cols-2">
            <Field>
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Telefone</FieldLabel>
              <Input
                value={formData.telefone}
                placeholder="(00) 00000-0000"
                maxLength={15}
                onChange={(e) => updateFormData({ telefone: formatarTelefone(e.target.value) })}
                className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
                required
              />
            </Field>
            <Field>
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Data de Nascimento</FieldLabel>
              <Input
                type="date"
                value={formData.dataNascimento}
                onChange={(e) => updateFormData({ dataNascimento: e.target.value })}
                className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
              />
            </Field>
          </div>
        </CardContent>
      </Card>

      {/* 2. Endereço Principal */}
      <Card className="rounded-xl border border-border bg-card shadow-sm overflow-hidden">
        <CardHeader className="p-4 sm:p-6 pb-3">
          <div className="flex items-center gap-2">
            <MapPin className="h-4 w-4 text-muted-foreground" />
            <CardTitle className="text-base sm:text-lg font-bold tracking-tight">Endereço Principal</CardTitle>
          </div>
        </CardHeader>
        <CardContent className="p-4 sm:p-6 pt-0 space-y-4">
          
          <div className="grid gap-4 grid-cols-1 sm:grid-cols-2">
            <Field>
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">CEP</FieldLabel>
              <div className="relative w-full">
                <Input
                  value={formData.cep}
                  placeholder="00000-000"
                  maxLength={9}
                  onChange={(e) => updateFormData({ cep: formatarCEP(e.target.value) })}
                  className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full pr-10"
                  required
                />
                {buscarCepLoading && (
                  <Loader2 className="absolute right-3 top-1/2 h-4 w-4 -translate-y-1/2 animate-spin text-muted-foreground" />
                )}
              </div>
            </Field>
            <div className="hidden sm:block" />
          </div>

          <div className="grid gap-4 grid-cols-1 sm:grid-cols-4">
            <Field className="sm:col-span-3">
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Rua/Logradouro</FieldLabel>
              <Input
                value={formData.rua}
                onChange={(e) => updateFormData({ rua: e.target.value })}
                className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
              />
            </Field>
            <Field className="sm:col-span-1">
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Número</FieldLabel>
              <Input
                value={formData.numero}
                onChange={(e) => updateFormData({ numero: e.target.value })}
                className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
              />
            </Field>
          </div>

          <div className="grid gap-4 grid-cols-1 sm:grid-cols-2">
            <Field>
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Complemento</FieldLabel>
              <Input
                value={formData.complemento}
                onChange={(e) => updateFormData({ complemento: e.target.value })}
                className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
              />
            </Field>
            <Field>
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Bairro</FieldLabel>
              <Input
                value={formData.bairro}
                onChange={(e) => updateFormData({ bairro: e.target.value })}
                className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
              />
            </Field>
          </div>

          <div className="grid gap-4 grid-cols-1 sm:grid-cols-3">
            <Field className="sm:col-span-2">
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Cidade</FieldLabel>
              <Input
                value={formData.cidade}
                onChange={(e) => updateFormData({ cidade: e.target.value })}
                className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
              />
            </Field>
            <Field className="sm:col-span-1">
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Estado (UF)</FieldLabel>
              <Input
                value={formData.estado}
                onChange={(e) => updateFormData({ estado: e.target.value })}
                className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
              />
            </Field>
          </div>
        </CardContent>
      </Card>

      {/* 3. Agendamento de Retirada */}
      <Card className="rounded-xl border border-border bg-card shadow-sm overflow-hidden">
        <CardHeader className="p-4 sm:p-6 pb-3">
          <div className="flex items-center gap-2">
            <Calendar className="h-4 w-4 text-muted-foreground" />
            <CardTitle className="text-base sm:text-lg font-bold tracking-tight">Agendamento de Retirada</CardTitle>
          </div>
        </CardHeader>
        <CardContent className="p-4 sm:p-6 pt-0 space-y-4">
          <div className="grid gap-4 grid-cols-1 sm:grid-cols-2">
            <Field>
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Data da Retirada</FieldLabel>
              <div className="relative w-full">
                <Input
                  type="date"
                  className="pl-9 rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
                  value={formData.dataRetirada}
                  onChange={(e) => updateFormData({ dataRetirada: e.target.value })}
                  required
                />
                <Calendar className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground pointer-events-none" />
              </div>
            </Field>
            <Field>
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Horário</FieldLabel>
              <div className="relative w-full">
                <Input
                  type="time"
                  className="pl-9 rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
                  value={formData.horarioRetirada}
                  onChange={(e) => updateFormData({ horarioRetirada: e.target.value })}
                  required
                />
                <Clock className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground pointer-events-none" />
              </div>
            </Field>
          </div>
          <Field>
            <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Observações Adicionais</FieldLabel>
            <Textarea
              placeholder="Ex: Preciso de embalagem para presente..."
              value={formData.observacoes}
              onChange={(e) => updateFormData({ observacoes: e.target.value })}
              className="rounded-xl border-border focus-visible:ring-primary/20 w-full resize-none"
              rows={2}
            />
          </Field>
        </CardContent>
      </Card>

      <div className="pt-2">
        <Button 
          onClick={handleProsseguir} 
          disabled={!isValid || isLoadingActive}
          className="w-full rounded-xl bg-foreground text-background hover:bg-foreground/90 shadow-sm h-12 px-5 font-semibold transition-colors"
        >
          {isLoadingActive ? (
            <>
              <Loader2 className="animate-spin mr-2 h-4 w-4" /> 
              Processando informações...
            </>
          ) : (
            "Prosseguir para Pagamento"
          )}
        </Button>
      </div>
    </div>
  )
}