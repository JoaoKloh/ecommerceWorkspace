"use client"

import { useState, useEffect, useRef } from "react"
import { UploadCloud, X, Loader2 } from "lucide-react"
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetDescription,
  SheetFooter,
} from "@/components/ui/sheet"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Switch } from "@/components/ui/switch"
import { Label } from "@/components/ui/label"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { FieldGroup, Field, FieldLabel } from "@/components/ui/field"
import type { Product } from "@/contexts/cart-context"
import api from "@/services/api"

interface Loja {
  id: number
  nome: string
}

interface ProductSheetProps {
  isOpen: boolean
  onClose: () => void
  produto: Product | null
  onSave: (values: any, imageFile?: File) => Promise<void>
}

// Lista estática de categorias exigida pelo seu backend
const categorias = ["Bolos", "Doces", "Founde"]

export function ProductSheet({
  isOpen,
  onClose,
  produto,
  onSave,
}: ProductSheetProps) {
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [selectedFile, setSelectedFile] = useState<File | null>(null)
  const [previewUrl, setPreviewUrl] = useState<string | null>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const [lojas, setLojas] = useState<Loja[]>([])

  const [nome, setNome] = useState("")
  const [descricao, setDescricao] = useState("")
  const [preco, setPreco] = useState("")
  const [idLoja, setIdLoja] = useState("")
  const [categoria, setCategoria] = useState("Bolos") // Reintroduzido o estado da categoria
  const [estoque, setEstoque] = useState("")
  const [desconto, setDesconto] = useState("")
  const [estaDisponivel, setEstaDisponivel] = useState(true)

  // Carrega as lojas dinamicamente do banco
  useEffect(() => {
    if (isOpen) {
      const carregarLojas = async () => {
        try {
          const data = await api.getLojas()
          setLojas(data || [])
          
          if (data && data.length > 0 && !produto) {
            setIdLoja(data[0].id.toString())
          }
        } catch (error) {
          console.error("Erro ao carregar lojas no formulário de produto:", error)
        }
      }
      carregarLojas()
    }
  }, [isOpen, produto])

  useEffect(() => {
    if (produto && isOpen) {
      const produtoFlexivel = produto as any
      setNome(produto.nome || "")
      setDescricao(produto.descricao || "")
      setPreco(produto.preco?.toString() || "")
      setCategoria(produto.categoria || "Bolos") // Restaura o valor ao editar
      setIdLoja(produtoFlexivel.idLoja?.toString() || produtoFlexivel.loja?.id?.toString() || "")
      setEstoque(produto.estoque?.toString() || "0")
      setDesconto(produto.desconto?.toString() || "0")
      setEstaDisponivel(produto.estaDisponivel ?? true)
      setPreviewUrl(produto.imagem || null)
      setSelectedFile(null)
    } else if (isOpen) {
      resetForm()
    }
  }, [produto, isOpen])

  const resetForm = () => {
    setNome("")
    setDescricao("")
    setPreco("")
    setCategoria("Bolos")
    setIdLoja(lojas[0]?.id?.toString() || "")
    setEstoque("0")
    setDesconto("0")
    setEstaDisponivel(true)
    setPreviewUrl(null)
    setSelectedFile(null)
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      setSelectedFile(file)
      setPreviewUrl(URL.createObjectURL(file))
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsSubmitting(true)
    
    const values = {
      nome,
      descricao,
      preco,
      categoria, // Enviado dentro do objeto para o DTO
      idLoja: Number(idLoja), // Capturado para ser usado como Query Param
      estoque,
      desconto,
      estaDisponivel: estaDisponivel
    }

    try {
      await onSave(values, selectedFile || undefined)
      onClose()
    } finally {
      setIsSubmitting(false)
    }
  }

  const isValid = nome.trim() !== "" && descricao.trim() !== "" && Number(preco) > 0 && idLoja !== "" && categoria !== ""

  return (
    <Sheet open={isOpen} onOpenChange={onClose}>
      <SheetContent className="flex w-full flex-col sm:max-w-lg overflow-y-auto border-l border-border bg-card p-6">
        
        <SheetHeader className="space-y-1">
          <SheetTitle className="text-xl font-bold tracking-tight text-foreground">
            {produto ? "Editar Produto" : "Novo Produto"}
          </SheetTitle>
          <SheetDescription className="text-sm text-muted-foreground">
            Insira os dados do produto, determine a categoria e associe à filial correspondente.
          </SheetDescription>
        </SheetHeader>

        <form onSubmit={handleSubmit} className="flex flex-1 flex-col space-y-5 py-4">
          <FieldGroup className="space-y-5">
            
            {/* Imagem do Produto */}
            <Field className="space-y-2">
              <FieldLabel className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Imagem do Produto</FieldLabel>
              <div 
                className="flex flex-col items-center justify-center gap-4 rounded-xl border border-dashed border-border/80 bg-secondary/10 p-6 hover:bg-secondary/20 transition-colors cursor-pointer"
                onClick={() => !previewUrl && fileInputRef.current?.click()}
              >
                {previewUrl ? (
                  <div className="relative h-36 w-36 shadow-sm">
                    <img src={previewUrl} alt="Preview" className="h-full w-full rounded-xl object-cover" />
                    <Button
                      type="button"
                      variant="destructive"
                      size="icon"
                      className="absolute -right-2 -top-2 h-6 w-6 rounded-full shadow-md"
                      onClick={(e) => {
                        e.stopPropagation()
                        setPreviewUrl(null)
                        setSelectedFile(null)
                      }}
                    >
                      <X className="h-3.5 w-3.5" />
                    </Button>
                  </div>
                ) : (
                  <div className="text-center space-y-1">
                    <UploadCloud className="mx-auto h-8 w-8 text-muted-foreground/80" />
                    <p className="text-xs font-medium text-muted-foreground">Clique para carregar uma imagem</p>
                  </div>
                )}
                <input
                  type="file"
                  ref={fileInputRef}
                  className="hidden"
                  accept="image/*"
                  onChange={handleFileChange}
                />
              </div>
            </Field>

            <Field className="space-y-2">
              <FieldLabel htmlFor="nome" className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Nome do produto *</FieldLabel>
              <Input 
                id="nome" 
                className="rounded-xl h-11 border-border focus-visible:ring-primary/20" 
                placeholder="Ex: Bolo Red Velvet"
                value={nome} 
                onChange={(e) => setNome(e.target.value)} 
                required 
              />
            </Field>

            <Field className="space-y-2">
              <FieldLabel htmlFor="descricao" className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Descrição *</FieldLabel>
              <Textarea 
                id="descricao" 
                className="rounded-xl border-border focus-visible:ring-primary/20 p-3 resize-none" 
                placeholder="Detalhes sobre os ingredientes..."
                value={descricao} 
                onChange={(e) => setDescricao(e.target.value)} 
                rows={3} 
                required 
              />
            </Field>

            <div className="grid gap-4 sm:grid-cols-3">
              <Field className="space-y-2">
                <FieldLabel htmlFor="preco" className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Preço *</FieldLabel>
                <Input 
                  id="preco" 
                  type="number" 
                  step="0.01" 
                  className="rounded-xl h-11 border-border focus-visible:ring-primary/20" 
                  placeholder="0,00"
                  value={preco} 
                  onChange={(e) => setPreco(e.target.value)} 
                  required 
                />
              </Field>
              <Field className="space-y-2">
                <FieldLabel htmlFor="estoque" className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Estoque *</FieldLabel>
                <Input 
                  id="estoque" 
                  type="number" 
                  className="rounded-xl h-11 border-border focus-visible:ring-primary/20" 
                  placeholder="0"
                  value={estoque} 
                  onChange={(e) => setEstoque(e.target.value)} 
                  required 
                />
              </Field>
              <Field className="space-y-2">
                <FieldLabel htmlFor="desconto" className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Desconto (%)</FieldLabel>
                <Input 
                  id="desconto" 
                  type="number" 
                  className="rounded-xl h-11 border-border focus-visible:ring-primary/20" 
                  placeholder="0"
                  value={desconto} 
                  onChange={(e) => setDesconto(e.target.value)} 
                />
              </Field>
            </div>

            <div className="grid gap-4 sm:grid-cols-2">
              {/* DROPDOWN 1: Seleção de Loja */}
              <Field className="space-y-2">
                <FieldLabel htmlFor="loja" className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Loja / Filial *</FieldLabel>
                <Select value={idLoja} onValueChange={setIdLoja}>
                  <SelectTrigger className="rounded-xl h-11 border-border focus:ring-primary/20">
                    <SelectValue placeholder="Selecione a loja..." />
                  </SelectTrigger>
                  <SelectContent className="rounded-xl">
                    {lojas.map((loja) => (
                      <SelectItem key={loja.id} value={loja.id.toString()} className="rounded-lg cursor-pointer">
                        {loja.nome}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </Field>

              {/* DROPDOWN 2: Seleção de Categoria (Recuperada) */}
              <Field className="space-y-2">
                <FieldLabel htmlFor="categoria" className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Categoria *</FieldLabel>
                <Select value={categoria} onValueChange={setCategoria}>
                  <SelectTrigger className="rounded-xl h-11 border-border focus:ring-primary/20">
                    <SelectValue placeholder="Selecione a categoria..." />
                  </SelectTrigger>
                  <SelectContent className="rounded-xl">
                    {categorias.map((cat) => (
                      <SelectItem key={cat} value={cat} className="rounded-lg cursor-pointer">
                        {cat}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </Field>
            </div>

            {/* Disponibilidade */}
            <div className="flex items-center justify-between rounded-xl border border-border bg-secondary/10 p-4">
              <div className="space-y-0.5">
                <Label htmlFor="disponivel" className="text-sm font-medium text-foreground cursor-pointer">Disponível para venda</Label>
                <p className="text-xs text-muted-foreground">Visível no catálogo dos clientes</p>
              </div>
              <Switch id="disponivel" checked={estaDisponivel} onCheckedChange={(checked: boolean) => setEstaDisponivel(checked)} />
            </div>
          </FieldGroup>

          {/* Rodapé de Ações */}
          <SheetFooter className="flex-row gap-2 border-t border-border/60 pt-4 sm:space-x-0">
            <Button 
              type="button" 
              variant="outline" 
              onClick={onClose} 
              className="flex-1 rounded-xl border-border h-11 text-muted-foreground hover:bg-secondary"
            >
              Cancelar
            </Button>
            <Button 
              type="submit" 
              disabled={!isValid || isSubmitting} 
              className="flex-1 rounded-xl bg-foreground text-background hover:bg-foreground/90 font-medium h-11 shadow-sm transition-colors"
            >
              {isSubmitting ? (
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              ) : produto ? (
                "Salvar Alterações"
              ) : (
                "Criar Produto"
              )}
            </Button>
          </SheetFooter>
        </form>
      </SheetContent>
    </Sheet>
  )
}