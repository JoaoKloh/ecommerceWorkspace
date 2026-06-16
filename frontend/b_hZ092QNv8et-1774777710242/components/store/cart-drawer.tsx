"use client"

import { useState } from "react"
import Link from "next/link"
import Image from "next/image"
import { Minus, Plus, Trash2, ShoppingBag, Loader2 } from "lucide-react"
import { Button } from "@/components/ui/button"
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetFooter,
  SheetDescription,
} from "@/components/ui/sheet"
import { ScrollArea } from "@/components/ui/scroll-area"
import { useCart } from "@/contexts/cart-context"
import api from "@/services/api"
import { toast } from "sonner"

function formatCurrency(value: number) {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(value)
}

export function CartDrawer() {
  const { 
    items, 
    isCartOpen, 
    setIsCartOpen, 
    incrementar, 
    decrementar, 
    removerItem,
    subtotalLocal,
  } = useCart()
  
  const [loading] = useState(false)

  return (
    <Sheet open={isCartOpen} onOpenChange={setIsCartOpen}>
      {/* 🍏 Otimizado para Mobile-First: h-full e w-full nativos, sm:max-w-md para desktop */}
      <SheetContent 
        side="right" 
        className="flex w-full h-full sm:max-w-md flex-col bg-white dark:bg-[#000000] border-none sm:border-l sm:border-border/40 p-4 sm:p-6 gap-0 justify-between"
      >
        <div className="flex flex-col flex-1 min-h-0">
          <SheetHeader className="space-y-1 pb-4 border-b border-border/10">
            <SheetTitle className="flex items-center gap-2 font-sans text-lg sm:text-xl font-bold tracking-tight text-[#1d1d1f] dark:text-[#f5f5f7]">
              <ShoppingBag className="h-5 w-5 stroke-[2.5]" />
              Seu Carrinho
              {loading && <Loader2 className="h-4 w-4 animate-spin text-[#86868b]" />}
            </SheetTitle>
            <SheetDescription className="sr-only">
              Itens no seu carrinho de compras.
            </SheetDescription>
          </SheetHeader>

          {items.length === 0 ? (
            <div className="flex flex-1 flex-col items-center justify-center gap-2 text-center select-none py-12">
              <div className="rounded-full bg-[#f5f5f7] dark:bg-[#161617] p-5 sm:p-6 mb-2">
                <ShoppingBag className="h-7 w-7 sm:h-8 sm:w-8 text-[#86868b]" />
              </div>
              <p className="text-sm font-semibold text-[#1d1d1f] dark:text-[#f5f5f7]">Seu carrinho está vazio</p>
              <p className="text-xs text-[#86868b] max-w-[220px]">Adicione chocolates e doces finos para começar.</p>
              <Button variant="link" className="text-xs text-[#0066cc] dark:text-[#2997ff] mt-2 font-medium" onClick={() => setIsCartOpen(false)} asChild>
                <Link href="/cardapio">Ver nosso cardápio</Link>
              </Button>
            </div>
          ) : (
            /* 🍏 min-h-0 e flex-1 garantem que a rolagem aconteça apenas na lista, sem quebrar o layout */
            <ScrollArea className="flex-1 min-h-0 mt-2">
              <div className="flex flex-col gap-3 py-2 pr-1">
                {items.map((item, index) => {
                  const itemData = item as any
                  const produtoId = item.produto?.id || itemData.produtoId
                  const nome = item.produto?.nome || itemData.nomeProduto
                  const preco = item.produto?.precoComDesconto || itemData.precoUnitario
                  const quantidade = item.quantidade

                  if (!produtoId) return null

                  return (
                    <div
                      key={`${produtoId}-${index}`}
                      className="flex gap-3 sm:gap-4 rounded-2xl bg-[#f5f5f7] dark:bg-[#161617] p-3 transition-colors items-center"
                    >
                      {/* Imagem responsiva baseada em proporções fixas de toque */}
                      <div className="relative h-16 w-16 sm:h-20 sm:w-20 flex-shrink-0 overflow-hidden rounded-xl bg-white dark:bg-[#000000]">
                        <Image
                          src={item.produto?.imagem || "/placeholder.png"}
                          alt={nome || "Produto"}
                          fill
                          className="object-contain p-1.5 sm:p-2 select-none"
                        />
                      </div>

                      <div className="flex flex-1 flex-col min-w-0 justify-between h-16 sm:h-20 py-0.5">
                        <div className="space-y-0.5">
                          <h4 className="text-xs sm:text-sm font-semibold text-[#1d1d1f] dark:text-[#f5f5f7] tracking-tight line-clamp-1">
                            {nome}
                          </h4>
                          <span className="text-xs font-bold text-[#1d1d1f] dark:text-[#f5f5f7] block">
                            {formatCurrency(preco)}
                          </span>
                        </div>

                        <div className="flex items-center justify-between">
                          {/* Pílula de quantidade com áreas de clique mais confortáveis no mobile */}
                          <div className="flex items-center gap-2 sm:gap-3 bg-white dark:bg-[#000000] rounded-full p-0.5 sm:p-1 shadow-2xs border border-border/5">
                            <button
                              className="h-6 w-6 sm:h-7 sm:w-7 flex items-center justify-center rounded-full text-[#1d1d1f] dark:text-[#f5f5f7] hover:bg-[#f5f5f7] dark:hover:bg-[#161617] active:scale-90 transition-all"
                              onClick={async () => {
                                try {
                                  await api.decreaseQuantity(produtoId, 1)
                                  decrementar(produtoId)
                                } catch { toast.error("Erro ao atualizar") }
                              }}
                            >
                              <Minus className="h-3 w-3 stroke-[2.5]" />
                            </button>
                            <span className="w-3 sm:w-4 text-center text-xs font-bold text-[#1d1d1f] dark:text-[#f5f5f7]">
                              {quantidade}
                            </span>
                            <button
                              className="h-6 w-6 sm:h-7 sm:w-7 flex items-center justify-center rounded-full text-[#1d1d1f] dark:text-[#f5f5f7] hover:bg-[#f5f5f7] dark:hover:bg-[#161617] active:scale-90 transition-all"
                              onClick={async () => {
                                try {
                                  await api.addToCart(produtoId, 1)
                                  incrementar(produtoId)
                                } catch { toast.error("Erro ao atualizar") }
                              }}
                            >
                              <Plus className="h-3 w-3 stroke-[2.5]" />
                            </button>
                          </div>

                          <Button
                            variant="ghost"
                            size="icon"
                            className="h-7 w-7 sm:h-8 sm:w-8 rounded-full text-[#86868b] hover:text-destructive hover:bg-destructive/10 transition-colors"
                            onClick={async () => {
                              try {
                                await api.removeFromCart(produtoId)
                                removerItem(produtoId)
                              } catch { toast.error("Erro ao remover") }
                            }}
                          >
                            <Trash2 className="h-3.5 w-3.5 sm:h-4 sm:w-4" />
                          </Button>
                        </div>
                      </div>
                    </div>
                  )
                })}
              </div>
            </ScrollArea>
          )}
        </div>

        {/* 🍏 Rodapé Dinâmico: Preso na base da tela independente do tamanho do dispositivo */}
        {items.length > 0 && (
          <div className="pt-4 border-t border-border/40 bg-white dark:bg-[#000000] mt-auto">
            <div className="flex items-center justify-between text-sm sm:text-base font-semibold tracking-tight text-[#1d1d1f] dark:text-[#f5f5f7] mb-4">
              <span>Total</span>
              <span className="text-lg sm:text-xl font-bold">
                {loading ? "..." : formatCurrency(subtotalLocal)}
              </span>
            </div>
            
            <SheetFooter className="w-full flex-col sm:flex-col gap-0">
              <Button 
                asChild 
                className="w-full h-12 sm:h-13 rounded-full text-sm font-semibold bg-primary hover:bg-primary/90 text-primary-foreground shadow-xs transition-transform active:scale-[0.98]"
              >
                <Link href="/checkout" onClick={() => setIsCartOpen(false)}>
                  Finalizar Pedido
                </Link>
              </Button>
            </SheetFooter>
          </div>
        )}
      </SheetContent>
    </Sheet>
  )
}