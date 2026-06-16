"use client"

import { createContext, useContext, useState, useCallback, type ReactNode, useEffect } from "react"
import { usePathname } from "next/navigation"
import { toast } from "@/components/ui/use-toast"
import { api } from "@/services/api"

export interface Product {
  id: number
  idLoja: number
  nome: string
  descricao: string
  categoria: string
  imagem: string
  preco: number
  precoComDesconto: number
  desconto: number
  estoque: number
  estaDisponivel: boolean
  endereco: string
}

export interface CartItem {
  produto: Product
  quantidade: number
}

interface CartContextType {
  items: CartItem[]
  adicionarItem: (produto: Product, quantidade?: number) => void
  removerItem: (produtoId: number) => void
  incrementar: (produtoId: number) => void
  decrementar: (produtoId: number) => void
  limparCarrinho: () => void
  totalItens: number
  subtotalLocal: number
  isCartOpen: boolean
  setIsCartOpen: (open: boolean) => void
  carregarCarrinhoDoUsuario: () => Promise<void> 
}

const CartContext = createContext<CartContextType | undefined>(undefined)

export function CartProvider({ children }: { children: ReactNode }) {
  const [items, setItems] = useState<CartItem[]>([])
  const [isCartOpen, setIsCartOpen] = useState(false)
  const pathname = usePathname()

  /**
   * Busca o carrinho no servidor e formata os dados
   * Adicionado tratamento silencioso para erro 401 (Não autenticado)
   */
  const carregarCarrinhoDoUsuario = useCallback(async () => {
    try {
      const data = await api.getCart()
      
      if (data && data.itens) {
        const itensFormatados = data.itens.map((itemJava: any) => ({
          quantidade: itemJava.quantidade,
          produto: {
            id: itemJava.produtoId,
            nome: itemJava.nomeProduto,
            preco: itemJava.precoUnitario,
            precoComDesconto: itemJava.precoUnitario, 
            imagem: itemJava.imagemUrl || "/placeholder.png",
          }
        }))
        setItems(itensFormatados)
      }
    } catch (error: any) {
      // Se for 401, o usuário não está logado. Limpamos o estado sem erro no console.
      if (error.response?.status === 401) {
        setItems([])
        return
      }
      console.error("Erro ao carregar carrinho:", error)
    }
  }, [])

  /**
   * 🔥 Efeito ONIPRESENTE de Sincronização
   * Dispara em absolutamente qualquer página acessada (sempre que o pathname mudar)
   */
  useEffect(() => {
    const rotasBloqueadas = ["/auth/login", "/auth/register"]
    const isAuthPage = rotasBloqueadas.includes(pathname)
    
    // Se não for página de Auth, força a requisição do carrinho no banco de dados
    if (!isAuthPage) {
      carregarCarrinhoDoUsuario()
    }
  }, [pathname, carregarCarrinhoDoUsuario])

  const adicionarItem = useCallback((produto: Product, quantidade = 1) => {
    setItems((prevItems) => {
      const existingItem = prevItems.find((item) => item.produto.id === produto.id)
      if (existingItem) {
        return prevItems.map((item) =>
          item.produto.id === produto.id
            ? { ...item, quantidade: item.quantidade + quantidade }
            : item
        )
      }
      return [...prevItems, { produto, quantidade }]
    })
    
    setIsCartOpen(true)
  }, [])

  const removerItem = useCallback(async (produtoId: number) => {
    setItems((prevItems) => prevItems.filter((item) => item.produto.id !== produtoId))
    // Opcional: Se seu backend tiver a rota pronta, você pode descomentar abaixo:
    // await api.removeFromCart(produtoId)
  }, [])

  const incrementar = useCallback((produtoId: number) => {
    setItems((prevItems) =>
      prevItems.map((item) =>
        item.produto.id === produtoId
          ? { ...item, quantidade: item.quantidade + 1 }
          : item
      )
    )
  }, [])

  const decrementar = useCallback((produtoId: number) => {
    setItems((prevItems) =>
      prevItems
        .map((item) =>
          item.produto.id === produtoId
            ? { ...item, quantidade: Math.max(0, item.quantidade - 1) }
            : item
        )
        .filter((item) => item.quantidade > 0)
    )
  }, [])

  const limparCarrinho = useCallback(() => {
    try {
      api.clearCart()
    } catch (error) {
      console.error("Erro ao limpar carrinho no servidor:", error)
    } finally {
      setItems([])
    }
  }, [])

  const totalItens = items.reduce((total, item) => total + item.quantidade, 0)
  const subtotalLocal = items.reduce((total, item) => {
    return total + (item.produto.precoComDesconto * item.quantidade)
  }, 0)

  return (
    <CartContext.Provider
      value={{
        items,
        adicionarItem,
        removerItem,
        incrementar,
        decrementar,
        limparCarrinho,
        totalItens,
        subtotalLocal,
        isCartOpen,
        setIsCartOpen,
        carregarCarrinhoDoUsuario
      }}
    >
      {children}
    </CartContext.Provider>
  )
}

export function useCart() {
  const context = useContext(CartContext)
  if (context === undefined) {
    throw new Error("useCart must be used within a CartProvider")
  }
  return context
}