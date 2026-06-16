"use client"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Store, MapPin, Loader2, ArrowLeft, Trash2 } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { toast } from "sonner"
import api from "@/services/api"

// Definição das interfaces para manter o código tipado e seguro
interface Endereco {
  id?: number
  rua: string
  numero: string
  complemento: string | null
  bairro: string
  cidade: string
  estado: string
  cep: string
}

interface Loja {
  id?: number
  nome: string
  endereco: Endereco
}

export default function NovaLojaPage() {
  const router = useRouter()
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [lojas, setLojas] = useState<Loja[]>([])

  const [formData, setFormData] = useState({
    nome: "",
    rua: "",
    numero: "",
    complemento: "",
    bairro: "",
    cidade: "",
    estado: "",
    cep: "",
  })

  // Carrega as lojas existentes ao montar o componente
  useEffect(() => {
    carregarLojas()
  }, [])

  const carregarLojas = async () => {
    try {
      // Ajuste o método da API se sua rota de listagem possuir outro nome
      const data = await api.getLojas() 
      setLojas(data || [])
    } catch (error) {
      console.error("Erro ao listar filiais:", error)
    }
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsSubmitting(true)

    const payload = {
      nome: formData.nome,
      endereco: {
        rua: formData.rua,
        numero: formData.numero,
        complemento: formData.complemento || null,
        bairro: formData.bairro,
        cidade: formData.cidade,
        estado: formData.estado,
        cep: formData.cep,
      },
    }

    try {
      await api.criarLoja(payload)
      toast.success("Loja cadastrada com sucesso!")
      
      // Reseta o formulário
      setFormData({
        nome: "",
        rua: "",
        numero: "",
        complemento: "",
        bairro: "",
        cidade: "",
        estado: "",
        cep: "",
      })
      
      // Atualiza a listagem local imediatamente
      carregarLojas()
    } catch (error: any) {
      console.error(error)
      const backendMsg = error.response?.data?.mensagem || "Erro ao salvar a nova filial."
      toast.error(backendMsg)
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleExcluirLoja = async (id: number) => {
    try {
      await api.deletarLoja(id)
      toast.success("Filial removida com sucesso.")
      setLojas((prev) => prev.filter((loja) => loja.id !== id))
    } catch (error: any) {
      console.error(error)
      toast.error("Não foi possível excluir esta filial.")
    }
  }

  return (
    <div className="w-full space-y-6 p-6">
      
      {/* Page Header padronizado com fontes do Dashboard principal */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground md:text-3xl">
            Nova Loja
          </h1>
          <p className="mt-1 text-sm text-muted-foreground">
            Cadastre um novo ponto de retirada para controle de estoque localizado
          </p>
        </div>
        
        <Button 
          variant="outline" 
          size="sm" 
          className="border-border text-muted-foreground hover:bg-secondary rounded-xl transition-colors"
          onClick={() => router.back()}
        >
          <ArrowLeft className="mr-2 h-4 w-4" /> Voltar para a listagem
        </Button>
      </div>

      {/* Bloco 1: Retângulo Único de Preenchimento */}
      <Card className="rounded-xl border border-border bg-card shadow-sm">
        <CardHeader className="p-6 pb-4 border-b border-border/50">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary/5">
              <Store className="h-5 w-5 text-primary" />
            </div>
            <div>
              <CardTitle className="text-lg font-semibold tracking-tight">Dados do Novo Ponto</CardTitle>
              <CardDescription>Insira as informações de identificação e localização para o ponto de retirada.</CardDescription>
            </div>
          </div>
        </CardHeader>
        
        <CardContent className="p-6">
          <form onSubmit={handleSubmit} className="space-y-6">
            
            {/* Seção Interna: Identificação */}
            <div className="space-y-2">
              <label htmlFor="nome" className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                Nome da Loja *
              </label>
              <Input
                id="nome"
                name="nome"
                className="rounded-xl border-border focus-visible:ring-primary/20 h-11"
                placeholder="Ex: Unidade Centro, Marie & Anne Shopping..."
                value={formData.nome}
                onChange={handleChange}
                required
                maxLength={100}
              />
            </div>

            {/* Divisor estético sutil */}
            <div className="h-px bg-border/60 my-2" />

            {/* Seção Interna: Endereço Coeso */}
            <div className="space-y-4">
              <div className="grid gap-4 md:grid-cols-3">
                <div className="space-y-2">
                  <label htmlFor="cep" className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">CEP *</label>
                  <Input id="cep" name="cep" className="rounded-xl h-11 border-border" placeholder="00000-000" value={formData.cep} onChange={handleChange} required />
                </div>
                <div className="space-y-2 md:col-span-2">
                  <label htmlFor="cidade" className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Cidade *</label>
                  <Input id="cidade" name="cidade" className="rounded-xl h-11 border-border" placeholder="Belo Horizonte" value={formData.cidade} onChange={handleChange} required />
                </div>
              </div>

              <div className="grid gap-4 md:grid-cols-4">
                <div className="space-y-2 md:col-span-3">
                  <label htmlFor="rua" className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Rua / Logradouro *</label>
                  <Input id="rua" name="rua" className="rounded-xl h-11 border-border" placeholder="Av. Afonso Pena" value={formData.rua} onChange={handleChange} required />
                </div>
                <div className="space-y-2">
                  <label htmlFor="numero" className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Número *</label>
                  <Input id="numero" name="numero" className="rounded-xl h-11 border-border" placeholder="123" value={formData.numero} onChange={handleChange} required />
                </div>
              </div>

              <div className="grid gap-4 md:grid-cols-2">
                <div className="space-y-2">
                  <label htmlFor="complemento" className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Complemento</label>
                  <Input id="complemento" name="complemento" className="rounded-xl h-11 border-border" placeholder="Loja B, Bloco 2" value={formData.complemento} onChange={handleChange} />
                </div>
                <div className="space-y-2">
                  <label htmlFor="bairro" className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Bairro *</label>
                  <Input id="bairro" name="bairro" className="rounded-xl h-11 border-border" placeholder="Centro" value={formData.bairro} onChange={handleChange} required />
                </div>
              </div>

              <div className="grid gap-4 md:grid-cols-4 items-end">
                <div className="space-y-2 md:col-span-2">
                  <label htmlFor="estado" className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Estado (UF) *</label>
                  <Input id="estado" name="estado" className="rounded-xl h-11 border-border" placeholder="MG" maxLength={2} value={formData.estado} onChange={handleChange} required />
                </div>
                
                {/* Botões empurrados para a ponta direita inferior do retângulo único */}
                <div className="md:col-span-2 flex justify-end gap-2 h-11">
                  <Button 
                    type="button" 
                    variant="ghost" 
                    className="rounded-xl text-muted-foreground hover:bg-secondary px-4 h-full"
                    onClick={() => router.back()}
                    disabled={isSubmitting}
                  >
                    Cancelar
                  </Button>
                  <Button 
                    type="submit" 
                    className="rounded-xl bg-foreground text-background hover:bg-foreground/90 shadow-sm px-6 font-medium h-full transition-colors"
                    disabled={isSubmitting}
                  >
                    {isSubmitting ? (
                      <Loader2 className="h-4 w-4 animate-spin" />
                    ) : (
                      "Salvar Ponto"
                    )}
                  </Button>
                </div>
              </div>
            </div>

          </form>
        </CardContent>
      </Card>

      {/* Bloco 2: Retângulo Inferior com a listagem das Filiais Cadastradas */}
      <Card className="rounded-xl border border-border bg-card shadow-sm">
        <CardHeader className="p-6 pb-4 border-b border-border/50">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary/5">
              <MapPin className="h-5 w-5 text-primary" />
            </div>
            <div>
              <CardTitle className="text-lg font-semibold tracking-tight">Filiais Cadastradas</CardTitle>
              <CardDescription>Ponto de retirada ativos e controle de estoque localizado.</CardDescription>
            </div>
          </div>
        </CardHeader>
        
        <CardContent className="p-0">
          {lojas.length === 0 ? (
            <div className="p-8 text-center text-sm text-muted-foreground">
              Nenhuma filial cadastrada no momento.
            </div>
          ) : (
            <div className="divide-y divide-border/60">
              {lojas.map((loja) => (
                <div key={loja.id} className="flex items-center justify-between p-6 hover:bg-secondary/20 transition-colors">
                  <div className="space-y-1 pr-4">
                    <h4 className="font-medium text-foreground text-sm md:text-base">{loja.nome}</h4>
                    <p className="text-xs md:text-sm text-muted-foreground leading-relaxed">
                      {loja.endereco.rua}, {loja.endereco.numero}
                      {loja.endereco.complemento && ` - ${loja.endereco.complemento}`}
                      {`, ${loja.endereco.bairro}. ${loja.endereco.cidade} - ${loja.endereco.estado}`}
                    </p>
                  </div>
                  
                  {/* Botão de ação com lixeira em vermelho destructivo */}
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-9 w-9 text-destructive hover:bg-destructive/10 rounded-xl transition-colors shrink-0"
                    onClick={() => loja.id && handleExcluirLoja(loja.id)}
                    title="Excluir filial"
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

    </div>
  )
}