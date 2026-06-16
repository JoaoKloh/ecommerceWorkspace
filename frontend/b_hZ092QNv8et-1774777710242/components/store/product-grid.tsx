"use client"

import { useState, useMemo } from "react"
import { Search } from "lucide-react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { ProductCard } from "./product-card"
import type { Product } from "@/contexts/cart-context"

interface ProductGridProps {
  produtos: Product[]
}

const categorias = ["Todos", "Bolos", "Doces"]

export function ProductGrid({ produtos }: ProductGridProps) {
  const [busca, setBusca] = useState("")
  const [categoriaAtiva, setCategoriaAtiva] = useState("Todos")

  const produtosFiltrados = useMemo(() => {
    return produtos.filter((produto) => {
      const matchBusca =
        produto.nome.toLowerCase().includes(busca.toLowerCase()) ||
        produto.descricao.toLowerCase().includes(busca.toLowerCase())
      const matchCategoria =
        categoriaAtiva === "Todos" || produto.categoria === categoriaAtiva

      return matchBusca && matchCategoria
    })
  }, [produtos, busca, categoriaAtiva])

  return (
    <div className="space-y-6">
      {/* Filters */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        {/* Search */}
        <div className="relative w-full sm:max-w-xs">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            type="search"
            placeholder="Buscar produtos..."
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
            className="pl-9"
          />
        </div>

        {/* Category Filter */}
        <div className="flex flex-wrap gap-2">
          {categorias.map((categoria) => (
            <Button
              key={categoria}
              variant={categoriaAtiva === categoria ? "default" : "outline"}
              size="sm"
              onClick={() => setCategoriaAtiva(categoria)}
              className="rounded-full"
            >
              {categoria}
            </Button>
          ))}
        </div>
      </div>

      {/* Results Count */}
      <p className="text-sm text-muted-foreground">
        {produtosFiltrados.length}{" "}
        {produtosFiltrados.length === 1 ? "produto encontrado" : "produtos encontrados"}
      </p>

      {/* Grid */}
      {produtosFiltrados.length > 0 ? (
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {produtosFiltrados.map((produto) => (
            <ProductCard key={produto.id} produto={produto} />
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center py-12 text-center">
          <p className="text-lg font-medium text-foreground">
            Nenhum produto encontrado
          </p>
          <p className="text-sm text-muted-foreground">
            Tente ajustar os filtros ou a busca
          </p>
        </div>
      )}
    </div>
  )
}
