"use client"

import Image from "next/image"
import { Plus } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { useCart, type Product } from "@/contexts/cart-context"
import { useRouter, usePathname } from "next/navigation" // 🍏 ADICIONADO: usePathname
import { toast } from "sonner"
import api from "@/services/api"

function formatCurrency(value: number) {
  if (value === undefined || value === null) return "R$ 0,00"
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(value)
}

interface ProductCardProps {
  produto: Product
  estaAutenticado?: boolean 
}

export function ProductCard({ produto, estaAutenticado = false }: ProductCardProps) {
  const router = useRouter()
  const pathname = usePathname() // 🍏 Captura a rota atual (/ ou /cardapio, etc.)
  const { adicionarItem } = useCart()

  const estoqueAtual = produto.estoque ?? 0
  const temEstoque = Number(estoqueAtual) > 0
  const disponivelNoSistema = produto.estaDisponivel === true || (produto.estaDisponivel as any) === "true"
  const podeComprar = temEstoque && disponivelNoSistema

  // Identifica se o componente está sendo exibido na Home Page de forma estrita
  const ehHomePage = pathname === "/"

  const handleNavigateToDetails = (e: React.MouseEvent) => {
    const target = e.target as HTMLElement
    
    if (target.closest('.btn-add-to-cart-action')) {
      return
    }
    
    // 🛡️ Regra M&A: Se estiver na Home e logado, vai pro cardápio escolher a loja
    if (ehHomePage && estaAutenticado) {
      router.push("/cardapio")
      return
    }

    router.push(`/produtos/${produto.id}`)
  }

  const handleAddToCart = async (e: React.MouseEvent) => {
    e.stopPropagation() 
    if (!podeComprar) return

    // 🛡️ TRAVA CRUCIAL: Se o card estiver na HOME PAGE, o botão de compra rápida
    // é anulado e o usuário é jogado para a seleção de loja no cardápio.
    if (ehHomePage) {
      router.push("/cardapio")
      return // 🍏 Bloqueio absoluto do fluxo de carrinho na home
    }

    // 🛒 Este bloco de inserção real SÓ será lido dentro da página do /cardapio ou interna:
    try {
      await api.addToCart(produto.id, 1)
      adicionarItem(produto, 1)
      toast.success(`${produto.nome} adicionado ao carrinho!`)
    } catch (error: any) {
      if (error.response?.status === 401 || error.response?.status === 403) {
        toast.error("Você precisa estar logado para comprar.")
        router.push("/auth/login")
        return
      }
      console.error("Erro ao adicionar:", error)
      toast.error("Erro ao processar sua solicitação.")
    }
  }

  return (
    <div 
      onClick={handleNavigateToDetails}
      className="group relative flex flex-col overflow-hidden rounded-2xl border border-border/40 bg-white dark:bg-[#161617] shadow-sm transition-all duration-300 hover:shadow-md cursor-pointer select-none"
    >
      <div className="relative aspect-square overflow-hidden bg-[#f5f5f7] dark:bg-[#000000]">
        <Image
          src={produto.imagem || "/placeholder-product.png"}
          alt={produto.nome}
          fill
          sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
          className={`object-contain p-6 transition-transform duration-500 group-hover:scale-103 ${
            !podeComprar ? 'grayscale opacity-50' : ''
          }`}
        />

        <div className="absolute left-3 top-3 flex flex-col gap-2">
          {produto.desconto > 0 && podeComprar && (
            <Badge className="bg-primary text-primary-foreground font-semibold border-none rounded-full px-2.5 py-0.5 text-[10px]">
              -{produto.desconto}%
            </Badge>
          )}
          
          {!podeComprar && (
            <Badge variant="secondary" className="bg-destructive text-destructive-foreground shadow-xs font-semibold rounded-full px-2.5 py-0.5 text-[10px]">
              {!temEstoque ? "Esgotado" : "Indisponível"}
            </Badge>
          )}
        </div>
      </div>

      <div className="flex flex-1 flex-col p-4">
        <span className="text-[10px] font-bold uppercase tracking-widest text-[#86868b]">
          {produto.categoria}
        </span>

        <h3 className="mt-1 font-sans text-base font-semibold text-[#1d1d1f] dark:text-[#f5f5f7] tracking-tight line-clamp-1">
          {produto.nome}
        </h3>
        <p className="mt-0.5 text-xs text-[#86868b] leading-normal line-clamp-2">
          {produto.descricao}
        </p>

        <div className="mt-auto flex items-end justify-between pt-4">
          <div className="flex flex-col">
            {produto.desconto > 0 && (
              <span className="text-xs text-[#86868b] line-through decoration-muted-foreground/60">
                {formatCurrency(produto.preco)}
              </span>
            )}
            <span className="text-lg font-bold text-[#1d1d1f] dark:text-[#f5f5f7] tracking-tight">
              {formatCurrency(produto.precoComDesconto)}
            </span>
          </div>

          <Button
            size="icon"
            className="btn-add-to-cart-action h-9 w-9 rounded-full bg-primary hover:bg-primary/90 text-primary-foreground transition-transform active:scale-90 shadow-xs"
            disabled={!podeComprar}
            onClick={handleAddToCart}
          >
            <Plus className="h-4 w-4" />
            <span className="sr-only">Adicionar ao carrinho</span>
          </Button>
        </div>
      </div>
    </div>
  )
}