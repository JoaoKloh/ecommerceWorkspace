"use client"

import { useEffect, useState } from "react"
import { ProductGrid } from "@/components/store/product-grid"
import api from "@/services/api" 
import { type Product } from "@/contexts/cart-context"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Button } from "@/components/ui/button"
import { Loader2, Store, MapPin, ChevronRight } from "lucide-react"

// 🛠️ Interface alinhada exatamente com a estrutura do seu backend Java
interface Endereco {
  id: number
  cep: string
  rua: string
  numero: string
  complemento?: string
  bairro: string
  cidade: string
  estado: string
}

interface Loja {
  id: string | number
  nome: string
  endereco: Endereco // Objeto aninhado vindo do banco
}

export default function CardapioPage() {
  const [lojas, setLojas] = useState<Loja[]>([])
  const [lojaSelecionada, setLojaSelecionada] = useState<string>("")
  const [lojaConfirmada, setLojaConfirmada] = useState<boolean>(false)
  
  const [produtos, setProdutos] = useState<Product[]>([])
  const [carregandoLojas, setCarregandoLojas] = useState(true)
  const [carregandoProdutos, setCarregandoProdutos] = useState(false)
  const [erro, setErro] = useState(false)

  // 1. Carrega a lista de lojas disponíveis
  useEffect(() => {
    const buscarLojas = async () => {
      try {
        setCarregandoLojas(true)
        const response = await api.getLojas()
        setLojas(response || [])
      } catch (error) {
        console.error("Erro ao buscar lojas do backend:", error)
      
      } finally {
        setCarregandoLojas(false)
      }
    }
    buscarLojas()
  }, [])

  // 2. Carrega os produtos da loja
  const carregarCardapioDaLoja = async (idDaLoja: string) => {
    try {
      setCarregandoProdutos(true)
      setErro(false)
      const response = await api.getProducts(Number(idDaLoja))
      setProdutos(response)
      setLojaConfirmada(true)
    } catch (error) {
      console.error("Erro ao carregar produtos desta loja:", error)
      setErro(true)
    } finally {
      setCarregandoProdutos(false)
    }
  }

  const handleConfirmarLoja = () => {
    if (lojaSelecionada) {
      carregarCardapioDaLoja(lojaSelecionada)
    }
  }

  const handleMudarDeLoja = () => {
    setLojaConfirmada(false)
    setProdutos([])
  }

  const dadosLojaAtual = lojas.find(l => String(l.id) === lojaSelecionada)

  if (carregandoLojas) {
    return (
      <div className="w-full h-[60vh] flex flex-col items-center justify-center gap-2 text-muted-foreground p-4">
        <Loader2 className="animate-spin h-6 w-6 text-primary" />
        <span className="text-xs font-medium">Buscando lojas parceiras...</span>
      </div>
    )
  }

  return (
    <div className="w-full space-y-5 p-4 sm:p-6 max-w-7xl mx-auto">
      
      {/* HEADER DA PÁGINA */}
      {/* 🚀 Ajuste de alinhamento: adicionado 'pt-4 sm:pt-6' para empurrar o bloco ligeiramente para baixo */}
      <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between border-b border-border/40 pb-4 pt-4 sm:pt-6">
        <div>
          
          {/* 🛠️ Correção do TypeScript: Se a loja estiver confirmada, exibe o bloco. 
              Se não estiver, renderiza 'null' de forma limpa, sumindo com o texto antigo. */}
          {lojaConfirmada && dadosLojaAtual ? (
            <div className="mt-0.5 space-y-0.5 text-xs sm:text-sm text-muted-foreground">
              <span className="font-semibold text-foreground">Exibindo o catálogo de {dadosLojaAtual.nome}</span>
              <span className="text-[11px] text-muted-foreground font-normal leading-normal mt-1">
              <p className="text-xs text-muted-foreground/90">
                {dadosLojaAtual.endereco.rua}, {dadosLojaAtual.endereco.numero} 
                {dadosLojaAtual.endereco.complemento ? ` (${dadosLojaAtual.endereco.complemento})` : ""} - {dadosLojaAtual.endereco.bairro}, {dadosLojaAtual.endereco.cidade.toUpperCase()}/{dadosLojaAtual.endereco.estado}
              </p>
              </span>
            </div>
          ) : null}
        </div>

        {lojaConfirmada && (
          <Button 
            variant="outline" 
            onClick={handleMudarDeLoja}
            className="rounded-xl border-border h-10 px-4 text-xs font-medium text-muted-foreground hover:text-foreground self-start sm:self-auto"
          >
            <MapPin className="mr-1.5 h-3.5 w-3.5" /> Mudar de Loja
          </Button>
        )}
      </div>
      {/* FLUXO 1: SELEÇÃO DE LOJA */}
      {!lojaConfirmada ? (
        <div className="max-w-md mx-auto py-8 animate-in fade-in slide-in-from-bottom-4 duration-300">
          <Card className="rounded-xl border border-border bg-card shadow-sm overflow-hidden">
            <CardHeader className="p-5 pb-4 text-center">
              <div className="mx-auto h-12 w-12 rounded-xl bg-secondary/40 border border-border flex items-center justify-center mb-3">
                <Store className="h-6 w-6 text-muted-foreground" />
              </div>
              <CardTitle className="text-lg font-bold tracking-tight">Pronto para conferir as novidades?</CardTitle>
              <CardDescription className="text-xs font-medium text-muted-foreground/80 mt-1">

              </CardDescription>
            </CardHeader>
            
            <CardContent className="p-5 pt-0 space-y-4">
              <div className="space-y-1.5">
  <label className="text-xs font-semibold uppercase tracking-wider text-muted-foreground/90">
    Selecione a Unidade
  </label>
  <Select value={lojaSelecionada} onValueChange={setLojaSelecionada}>
    {/* 📱 MELHORIA DE UX: 
      - Mudamos a altura de h-12 para h-auto com min-h-[64px] (perfeito para dedão no mobile)
      - Adicionamos py-3 e px-4 para dar um respiro excelente entre o Nome e o Endereço
    */}
    <SelectTrigger className="w-full rounded-xl border-border min-h-[64px] h-auto py-3 px-4 focus:ring-primary/20 bg-background text-sm shadow-sm transition-all">
      <SelectValue placeholder="Toque para escolher uma loja..." />
    </SelectTrigger>
    
    <SelectContent className="rounded-xl border-border shadow-md max-w-md">
  {lojas.map((loja) => (
    <SelectItem 
      key={loja.id} 
      value={String(loja.id)}
      className="rounded-lg cursor-pointer text-sm font-medium py-3 px-4"
    >
      <div className="flex flex-col items-start text-left">
        <span className="text-foreground font-semibold">{loja.nome}</span>
        
        {/* 🚀 AJUSTE DE RECUO: Adicionado 'mt-1' para empurrar o texto um pouco para baixo */}
        <span className="text-[11px] text-muted-foreground font-normal leading-normal mt-1">
          {loja.endereco.rua}, {loja.endereco.numero} - {loja.endereco.bairro}, {loja.endereco.cidade.toUpperCase()}
        </span>
      </div>
    </SelectItem>
  ))}
</SelectContent>
  </Select>
</div>

              <Button 
                onClick={handleConfirmarLoja}
                disabled={!lojaSelecionada || carregandoProdutos}
                className="w-full rounded-xl bg-foreground text-background hover:bg-foreground/90 shadow-sm h-12 px-5 font-semibold transition-colors mt-2"
              >
                {carregandoProdutos ? (
                  <>
                    <Loader2 className="animate-spin mr-2 h-4 w-4" /> 
                    Carregando o cardápio...
                  </>
                ) : (
                  <>
                    Acessar Cardápio <ChevronRight className="ml-1.5 h-4 w-4" />
                  </>
                )}
              </Button>
            </CardContent>
          </Card>
        </div>
      ) : (
        /* FLUXO 2: CARDÁPIO LIBERADO */
        <div className="animate-in fade-in duration-300">
          {erro ? (
            <Card className="rounded-xl border border-dashed border-destructive/20 bg-destructive/5 max-w-md mx-auto">
              <CardContent className="p-6 flex flex-col items-center justify-center gap-2 text-destructive text-center">
                <span className="text-sm font-semibold">Erro ao conectar via ngrok</span>
                <span className="text-xs text-muted-foreground">Não foi possível sincronizar os produtos desta unidade com o Spring Boot.</span>
                <Button variant="outline" size="sm" onClick={() => carregarCardapioDaLoja(lojaSelecionada)} className="mt-2 rounded-lg">
                  Tentar Novamente
                </Button>
              </CardContent>
            </Card>
          ) : (
            <ProductGrid produtos={produtos} />
          )}
        </div>
      )}
    </div>
  )
}