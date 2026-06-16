"use client"

import { useState, useMemo, useEffect, useCallback } from "react"
import { Plus, Search, Pencil, Trash2, MoreHorizontal, Loader2, PackageSearch } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import { Card, CardContent } from "@/components/ui/card"
import { ProductSheet } from "@/components/admin/product-sheet"
import { toast } from "sonner"
import type { Product } from "@/contexts/cart-context"
import api from "@/services/api"

function formatCurrency(value: number) {
  if (value === undefined || value === null) return "R$ 0,00"
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(value)
}

const categorias = ["Founde", "Bolos", "Doces"]

export default function ProdutosPage() {
  const [produtos, setProdutos] = useState<Product[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [busca, setBusca] = useState("")
  const [categoriaFiltro, setCategoriaFiltro] = useState("Todos")
  const [isSheetOpen, setIsSheetOpen] = useState(false)
  const [produtoEditando, setProdutoEditando] = useState<Product | null>(null)
  const [produtoParaDeletar, setProdutoParaDeletar] = useState<Product | null>(null)

  const carregarProdutosFromAPI = useCallback(async () => {
    setIsLoading(true)
    try {
      const data: any = await api.listAllAdmin()
      let listaFinal: Product[] = []

      if (Array.isArray(data)) {
        listaFinal = data
      } else if (data && data.produtos && Array.isArray(data.produtos)) {
        listaFinal = data.produtos
      } else if (data && data.content && Array.isArray(data.content)) {
        listaFinal = data.content
      }

      setProdutos(listaFinal)
    } catch (error: any) {
      console.error("Erro ao carregar produtos:", error)
      toast.error("Erro ao carregar o catálogo.")
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    carregarProdutosFromAPI()
  }, [carregarProdutosFromAPI])

  const produtosFiltrados = useMemo(() => {
    return produtos.filter((produto) => {
      const matchBusca = 
        produto.nome?.toLowerCase().includes(busca.toLowerCase()) ||
        produto.descricao?.toLowerCase().includes(busca.toLowerCase())
      const matchCategoria = categoriaFiltro === "Todos" || produto.categoria === categoriaFiltro
      return matchBusca && matchCategoria
    })
  }, [produtos, busca, categoriaFiltro])

  const handleSalvarProduto = async (values: any, imageFile?: File) => {
    const disponibilidadeReal = values.estaDisponivel === true || values.estaDisponivel === 'true'

    const payload = {
      nome: values.nome,
      descricao: values.descricao,
      preco: Number(values.preco),
      estoque: Number(values.estoque || 0),
      categoria: values.categoria, // Mantido e enviado conforme exigência do backend
      desconto: Number(values.desconto || 0),
      estaDisponivel: disponibilidadeReal
    }

    try {
      if (produtoEditando) {
        await api.updateProduct(Number(produtoEditando.id), payload, imageFile)
        toast.success("Produto updated!")
      } else {
        if (!imageFile) {
          toast.warning("A imagem é obrigatória para novos produtos.")
          return
        }
        await api.createProduct(payload, imageFile, values.idLoja)
        toast.success("Produto criado com sucesso!")
      }
      
      setIsSheetOpen(false)
      setProdutoEditando(null)
      carregarProdutosFromAPI()
    } catch (error: any) {
      const backendMsg = error.response?.data?.[0]?.mensagem || "Erro ao salvar."
      toast.error(backendMsg)
    }
  }

  const handleDeletarProduto = async () => {
    if (!produtoParaDeletar) return
    try {
      await api.deleteProduct(Number(produtoParaDeletar.id))
      toast.success("Produto removido.")
      setProdutoParaDeletar(null)
      carregarProdutosFromAPI()
    } catch (error) {
      toast.error("Erro ao excluir produto.")
    }
  }

  return (
    <div className="w-full space-y-6 p-6">
      
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-foreground md:text-3xl">
            Produtos
          </h1>
          <p className="mt-1 text-sm text-muted-foreground">
            Gerencie o estoque e o catálogo em tempo real
          </p>
        </div>
        
        <Button 
          onClick={() => { setProdutoEditando(null); setIsSheetOpen(true); }}
          className="rounded-xl bg-foreground text-background hover:bg-foreground/90 shadow-sm h-11 px-5 font-medium transition-colors"
        >
          <Plus className="mr-2 h-4 w-4" /> Novo Produto
        </Button>
      </div>

      {/* Card de Filtros */}
      <Card className="rounded-xl border border-border bg-card shadow-sm">
        <CardContent className="p-4 flex flex-col gap-4 sm:flex-row items-center">
          <div className="relative flex-1 w-full">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              placeholder="Buscar produtos..."
              value={busca}
              onChange={(e) => setBusca(e.target.value)}
              className="pl-9 rounded-xl border-border h-11 focus-visible:ring-primary/20 w-full"
            />
          </div>
          <Select value={categoriaFiltro} onValueChange={setCategoriaFiltro}>
            <SelectTrigger className="w-full sm:w-44 rounded-xl border-border h-11 focus:ring-primary/20">
              <SelectValue placeholder="Categoria" />
            </SelectTrigger>
            <SelectContent className="rounded-xl">
              {categorias.map(cat => <SelectItem key={cat} value={cat}>{cat}</SelectItem>)}
            </SelectContent>
          </Select>
        </CardContent>
      </Card>

      {/* Card da Tabela */}
      <Card className="rounded-xl border border-border bg-card shadow-sm overflow-hidden">
        <CardContent className="p-6 overflow-x-auto">
          <Table>
            <TableHeader>
              <TableRow className="hover:bg-transparent border-b border-border/60">
                <TableHead className="text-xs font-semibold uppercase tracking-wider text-muted-foreground h-11">Produto</TableHead>
                <TableHead className="text-xs font-semibold uppercase tracking-wider text-muted-foreground h-11">Estoque</TableHead>
                <TableHead className="text-xs font-semibold uppercase tracking-wider text-muted-foreground h-11">Preço</TableHead>
                <TableHead className="text-xs font-semibold uppercase tracking-wider text-muted-foreground h-11">Status</TableHead>
                <TableHead className="w-[60px] h-11"></TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={5} className="h-48 text-center">
                    <div className="flex flex-col items-center justify-center gap-2 text-muted-foreground">
                      <Loader2 className="animate-spin h-6 w-6 text-primary" />
                      <span className="text-xs font-medium">Carregando catálogo...</span>
                    </div>
                  </TableCell>
                </TableRow>
              ) : produtosFiltrados.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5} className="h-48 text-center text-sm text-muted-foreground">
                    Nenhum produto encontrado para os filtros aplicados.
                  </TableCell>
                </TableRow>
              ) : (
                produtosFiltrados.map((produto) => (
                  <TableRow key={produto.id} className="hover:bg-secondary/20 transition-colors border-b border-border/40">
                    <TableCell className="py-4">
                      <div className="flex items-center gap-4 min-w-[260px]">
                        <div className="h-11 w-11 flex-shrink-0 overflow-hidden rounded-xl border border-border bg-muted flex items-center justify-center shadow-sm">
                          {produto.imagem ? (
                            <img src={produto.imagem} className="h-full w-full object-cover" alt={produto.nome} />
                          ) : (
                            <PackageSearch className="h-5 w-5 text-muted-foreground" />
                          )}
                        </div>
                        <div className="overflow-hidden space-y-0.5">
                          <p className="font-semibold text-foreground text-sm tracking-tight line-clamp-1">{produto.nome}</p>
                          <p className="text-xs font-medium text-muted-foreground/80">{produto.categoria}</p>
                        </div>
                      </div>
                    </TableCell>

                    <TableCell className="text-sm font-medium text-foreground py-4">
                      {Number(produto.estoque) || 0} un.
                    </TableCell>

                    <TableCell className="font-semibold text-sm text-foreground py-4">
                      {formatCurrency(produto.preco)}
                    </TableCell>

                    <TableCell className="py-4">
                      <Badge 
                        variant="outline"
                        className={`rounded-lg px-2.5 py-0.5 text-xs font-medium tracking-wide shadow-none transition-none ${
                          produto.estaDisponivel && (Number(produto.estoque) > 0)
                            ? "bg-emerald-500/10 text-emerald-600 hover:bg-emerald-500/10 border-emerald-500/20" 
                            : "bg-destructive/10 text-destructive hover:bg-destructive/10 border-destructive/20"
                        }`}
                      >
                        {produto.estaDisponivel && (Number(produto.estoque) > 0) ? "Disponível" : "Indisponível"}
                      </Badge>
                    </TableCell>

                    <TableCell className="py-4">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon" className="h-9 w-9 text-muted-foreground hover:text-foreground rounded-xl">
                            <MoreHorizontal className="h-5 w-5" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end" className="rounded-xl border border-border shadow-md">
                          <DropdownMenuItem 
                            onClick={() => { setProdutoEditando(produto); setIsSheetOpen(true); }}
                            className="rounded-lg cursor-pointer text-sm font-medium"
                          >
                            <Pencil className="mr-2 h-4 w-4" /> Editar
                          </DropdownMenuItem>
                          <DropdownMenuItem 
                            onClick={() => setProdutoParaDeletar(produto)} 
                            className="text-destructive rounded-lg cursor-pointer text-sm font-medium focus:bg-destructive/5 focus:text-destructive"
                          >
                            <Trash2 className="mr-2 h-4 w-4" /> Excluir
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      {/* Componente Lateral de Edição/Criação */}
      <ProductSheet
        isOpen={isSheetOpen}
        onClose={() => setIsSheetOpen(false)}
        produto={produtoEditando}
        onSave={handleSalvarProduto}
      />

      {/* Modal de Alerta de Deleção */}
      <AlertDialog open={!!produtoParaDeletar} onOpenChange={() => setProdutoParaDeletar(null)}>
        <AlertDialogContent className="rounded-xl border border-border">
          <AlertDialogHeader>
            <AlertDialogTitle className="text-lg font-bold tracking-tight">Excluir produto?</AlertDialogTitle>
            <AlertDialogDescription className="text-sm text-muted-foreground">
              Deseja realmente remover permanentemente o produto <strong>{produtoParaDeletar?.nome}</strong> do seu catálogo?
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter className="gap-2 sm:gap-0">
            <AlertDialogCancel className="rounded-xl border border-border text-muted-foreground hover:bg-secondary">
              Cancelar
            </AlertDialogCancel>
            <AlertDialogAction 
              onClick={handleDeletarProduto} 
              className="bg-destructive text-white hover:bg-destructive/90 rounded-xl font-medium transition-colors"
            >
              Excluir Produto
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}