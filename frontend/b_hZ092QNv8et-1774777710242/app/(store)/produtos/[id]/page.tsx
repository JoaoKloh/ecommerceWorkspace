"use client"

import { useState, useEffect } from "react"
import Image from "next/image"
import { useParams, useRouter } from "next/navigation"
import { ChevronLeft, Minus, Plus, ShoppingBag, Check, Loader2, MapPin } from "lucide-react"
import { Button } from "@/components/ui/button"
import { useCart } from "@/contexts/cart-context"
import api from "@/services/api"
import { type Product } from "@/contexts/cart-context"
import { toast } from "sonner"

function formatCurrency(value: number) {
  if (value === undefined || value === null) return "R$ 0,00"
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(value)
}

export default function ProductPage() {
  const params = useParams()
  const router = useRouter()
  const { adicionarItem } = useCart()
  
  const [produto, setProduto] = useState<Product | null>(null)
  const [quantidade, setQuantidade] = useState(1)
  const [loading, setLoading] = useState(true)
  const [adicionado, setAdicionado] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)

  useEffect(() => {
    const carregarProduto = async () => {
      try {
        setLoading(true)
        const idLoja = 1
        
        const data: Product[] = await api.getProducts(idLoja)
        const produtoEncontrado = data.find((p) => String(p.id) === String(params.id))
        
        setProduto(produtoEncontrado || null)
      } catch (error) {
        console.error("Erro ao carregar produto:", error)
      } finally {
        setLoading(false)
      }
    }
    carregarProduto()
  }, [params.id])

  const handleAumentar = () => setQuantidade(prev => prev + 1)
  const handleDiminuir = () => setQuantidade(prev => (prev > 1 ? prev - 1 : 1))

  const handleAdicionar = async () => {
    if (!produto || isSubmitting) return

    const estoqueAtual = produto.estoque ?? 0
    const disponivelNoSistema = produto.estaDisponivel === true || (produto.estaDisponivel as any) === "true"
    if (Number(estoqueAtual) <= 0 || !disponivelNoSistema) {
      toast.error("Este produto não está disponível no momento.")
      return
    }

    try {
      setIsSubmitting(true)

      await api.addToCart(produto.id, quantidade)
      adicionarItem(produto, quantidade)
      
      setAdicionado(true)
      toast.success(`${quantidade}x ${produto.nome} adicionado(s) ao carrinho!`)
      setTimeout(() => setAdicionado(false), 2000)
      
    } catch (error: any) {
      if (error.response?.status === 401 || error.response?.status === 403) {
        toast.error("Você precisa estar logado para comprar.")
        router.push("/auth/login")
        return
      }

      console.error("Erro ao adicionar item na página de produto:", error)
      toast.error("Erro ao processar sua solicitação.")
    } finally {
      setIsSubmitting(false)
    }
  }

  // 🛡️ Guardrail 1: Carregamento do estado da tela
  if (loading) {
    return (
      <div className="flex min-h-[70vh] items-center justify-center bg-white dark:bg-black">
        <div className="text-xs font-medium text-[#86868b] animate-pulse">Carregando detalhes...</div>
      </div>
    )
  }

  // 🛡️ Guardrail 2: Validação de Nulo (O TypeScript barra a leitura abaixo se for null)
  if (!produto) {
    return (
      <div className="flex min-h-[70vh] flex-col items-center justify-center bg-white dark:bg-black gap-4">
        <div className="text-xs font-medium text-[#86868b]">Produto não encontrado.</div>
        <button onClick={() => router.push("/")} className="text-xs text-[#0066cc] hover:underline">Voltar para o Início</button>
      </div>
    )
  }

  // 🍏 POSIÇÃO CORRIGIDA: Agora declarada com segurança total após as travas de nulo
  const enderecoRetirada = 
    (produto as any).loja?.endereco || 
    (produto as any).endereco || 
    "Retirada no Local"

  return (
    <div className="min-h-screen bg-white dark:bg-black">
      {/* HEADER DE NAVEGAÇÃO INTERNA */}
      <div className="sticky top-16 z-30 w-full bg-white/80 dark:bg-black/80 backdrop-blur-md border-b border-border/40">
        <div className="container mx-auto px-4 h-12 flex items-center">
          <button 
            onClick={() => router.back()}
            className="flex items-center text-xs font-medium text-[#0066cc] dark:text-[#2997ff] hover:underline group"
          >
            <ChevronLeft className="h-4 w-4 mr-1 transition-transform group-hover:-translate-x-0.5" />
            Voltar
          </button>
        </div>
      </div>

      <main className="container mx-auto px-4 py-8 lg:py-16">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-start">
          
          {/* GALERIA DE IMAGEM */}
          <div className="relative aspect-square w-full max-w-2xl mx-auto overflow-hidden rounded-2xl bg-[#f5f5f7] dark:bg-[#161617]">
            <Image
              src={produto.imagem || "/placeholder.svg"}
              alt={produto.nome}
              fill
              className="object-contain p-8 lg:p-12 select-none"
              priority
            />
          </div>

          {/* DETALHES E COMPRA */}
          <div className="flex flex-col space-y-8 max-w-lg">
            <div className="space-y-2">
              <span className="text-xs font-semibold text-[#9e3a00] uppercase tracking-widest">
                Linha Premium
              </span>
              <h1 className="text-3xl lg:text-5xl font-bold tracking-tight text-[#1d1d1f] dark:text-[#f5f5f7]">
                {produto.nome}
              </h1>
              <p className="text-2xl lg:text-3xl font-medium text-[#1d1d1f] dark:text-[#f5f5f7]">
                {formatCurrency(produto.precoComDesconto ?? produto.preco)}
              </p>
            </div>

            <div className="space-y-4">
              <h2 className="text-sm font-semibold text-[#1d1d1f] dark:text-[#f5f5f7]">Descrição</h2>
              <p className="text-base leading-relaxed text-[#515154] dark:text-[#86868b] text-pretty">
                {produto.descricao || "Nossa receita exclusiva preparada com ingredientes selecionados de alta qualidade para proporcionar uma experiência inesquecível."}
              </p>
            </div>

            {/* CONTROLES DE QUANTIDADE E AÇÃO */}
            <div className="pt-6 space-y-6 border-t border-border/40">
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium text-[#1d1d1f] dark:text-[#f5f5f7]">Quantidade</span>
                <div className="flex items-center gap-4 bg-[#f5f5f7] dark:bg-[#161617] rounded-full p-1 border border-border/40">
                  <button 
                    disabled={isSubmitting}
                    onClick={handleDiminuir}
                    className="h-8 w-8 flex items-center justify-center rounded-full bg-white dark:bg-black shadow-sm hover:bg-white/80 active:scale-90 disabled:opacity-50 transition-all"
                  >
                    <Minus className="h-4 w-4 text-[#1d1d1f] dark:text-[#f5f5f7]" />
                  </button>
                  <span className="text-sm font-semibold w-6 text-center">{quantidade}</span>
                  <button 
                    disabled={isSubmitting}
                    onClick={handleAumentar}
                    className="h-8 w-8 flex items-center justify-center rounded-full bg-white dark:bg-black shadow-sm hover:bg-white/80 active:scale-90 disabled:opacity-50 transition-all"
                  >
                    <Plus className="h-4 w-4 text-[#1d1d1f] dark:text-[#f5f5f7]" />
                  </button>
                </div>
              </div>

              <Button 
                disabled={isSubmitting}
                onClick={handleAdicionar}
                className={`w-full h-14 rounded-full text-base font-semibold transition-all duration-300 active:scale-[0.98] ${
                  adicionado 
                  ? "bg-green-600 hover:bg-green-600" 
                  : "bg-primary hover:bg-primary/90"
                }`}
              >
                {isSubmitting ? (
                  <span className="flex items-center gap-2">
                    <Loader2 className="h-5 w-5 animate-spin" /> Adicionando...
                  </span>
                ) : adicionado ? (
                  <span className="flex items-center gap-2">
                    <Check className="h-5 w-5" /> Adicionado
                  </span>
                ) : (
                  <span className="flex items-center gap-2">
                    <ShoppingBag className="h-5 w-5" /> Adicionar ao Carrinho
                  </span>
                )}
              </Button>
            </div>

            {/* INFO ADICIONAL */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-8">
              
              <div className="p-4 rounded-xl border border-border/40 flex flex-col justify-center items-center text-center space-y-1 bg-white dark:bg-[#0b0b0c]">
                <div className="flex items-center gap-1.5 text-[#86868b]">
                  <MapPin className="h-3.5 w-3.5" />
                  <p className="text-[10px] font-bold uppercase tracking-wider">Local de Retirada</p>
                </div>
                <p className="text-xs font-semibold text-[#1d1d1f] dark:text-[#f5f5f7] line-clamp-2 text-pretty px-1">
                  {enderecoRetirada}
                </p>
              </div>

              <div className="p-4 rounded-xl border border-border/40 flex flex-col justify-center items-center text-center space-y-1 bg-white dark:bg-[#0b0b0c]">
                <p className="text-[10px] font-bold uppercase tracking-wider text-[#86868b]">Produção</p>
                <p className="text-xs font-semibold text-[#1d1d1f] dark:text-[#f5f5f7]">100% Artesanal</p>
              </div>

            </div>
          </div>
        </div>
      </main>
    </div>
  )
}