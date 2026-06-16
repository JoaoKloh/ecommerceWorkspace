"use client"

import { useEffect, useState, useRef } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Field, FieldLabel } from "@/components/ui/field"
import { Loader2, Calendar, Clock, User, MapPin } from "lucide-react"
import api from "@/services/api"
import { toast } from "sonner"

interface PedidoData {
  nome: string
  telefone: string
  cpf: string // 🍏 ADICIONADO: Sincronizado com o estado pai
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

export function PedidoStep({ 
  formData, 
  updateFormData, 
  onNext,
  isPending,
  paymentActive 
}: PedidoStepProps) {
  const [loading, setLoading] = useState(false)
  const [fetchingUser, setFetchingUser] = useState(false)
  const hasFetched = useRef(false)

  if (paymentActive) return null

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
            telefone: userData.telefone || "",
            cpf: userData.cpf || "", // 🍏 ADICIONADO: Preenche se o usuário já tiver salvo
            dataNascimento: userData.dataNascimento || "",
            cep: userData.endereco?.cep || "",
            rua: userData.endereco?.rua || "",
            numero: userData.endereco?.numero || "",
            complemento: userData.endereco?.complemento || "",
            bairro: userData.endereco?.bairro || "",
            cidade: userData.endereco?.cidade || "",
            estado: userData.endereco?.estado || "",
          })
        }
      } catch (error) {
        console.log("Usuário deslogado ou erro ao carregar perfil.")
      } finally {
        setFetchingUser(false)
        hasFetched.current = true
      }
    }
    carregarDadosIniciais()
  }, [updateFormData])

  const handleProsseguir = async (e: React.FormEvent) => {
    e.preventDefault()
    if (loading || isPending || paymentActive) return
    
    setLoading(true)
    try {
      // Sincroniza o perfil completo incluindo o CPF limpo (apenas números)
      await api.upsertProfile({
        nome: formData.nome,
        telefone: formData.telefone,
        cpf: formData.cpf.replace(/\D/g, ""), // 🍏 Remove máscara se houver antes de salvar
        dataNascimento: formData.dataNascimento,
        endereco: {
          cep: formData.cep,
          rua: formData.rua,
          numero: formData.numero,
          complemento: formData.complemento,
          bairro: formData.bairro,
          cidade: formData.cidade,
          estado: formData.estado
        }
      })

      onNext()
    } catch (error) {
      toast.error("Erro ao processar seus dados. Tente novamente.")
    } finally {
      setLoading(false)
    }
  }

  // 🍏 ADICIONADO: Agora o botão só libera se o CPF estiver preenchido
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

          {/* 🍏 ADICIONADO: Input de CPF adicionado ao layout */}
          <Field>
            <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">CPF (Obrigatório para emissão de PIX)</FieldLabel>
            <Input
              value={formData.cpf}
              placeholder="000.000.000-00"
              maxLength={14}
              onChange={(e) => updateFormData({ cpf: e.target.value })}
              className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
              required
            />
          </Field>

          <div className="grid gap-4 grid-cols-1 sm:grid-cols-2">
            <Field>
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Telefone</FieldLabel>
              <Input
                value={formData.telefone}
                onChange={(e) => updateFormData({ telefone: e.target.value })}
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
              <Input
                value={formData.cep}
                onChange={(e) => updateFormData({ cep: e.target.value })}
                className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
                required
              />
            </Field>
            <Field>
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Rua/Logradouro</FieldLabel>
              <Input
                value={formData.rua}
                onChange={(e) => updateFormData({ rua: e.target.value })}
                className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
              />
            </Field>
          </div>
          <div className="grid gap-4 grid-cols-3">
            <Field className="col-span-1">
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Número</FieldLabel>
              <Input
                value={formData.numero}
                onChange={(e) => updateFormData({ numero: e.target.value })}
                className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
              />
            </Field>
            <Field className="col-span-2">
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Complemento</FieldLabel>
              <Input
                value={formData.complemento}
                onChange={(e) => updateFormData({ complemento: e.target.value })}
                className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
              />
            </Field>
          </div>
          <div className="grid gap-4 grid-cols-1 sm:grid-cols-3">
            <Field>
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Bairro</FieldLabel>
              <Input
                value={formData.bairro}
                onChange={(e) => updateFormData({ bairro: e.target.value })}
                className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
              />
            </Field>
            <Field>
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90 mb-1.5">Cidade</FieldLabel>
              <Input
                value={formData.cidade}
                onChange={(e) => updateFormData({ cidade: e.target.value })}
                className="rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
              />
            </Field>
            <Field>
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