"use client"

import { useEffect, useState } from "react"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Badge } from "@/components/ui/badge"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Separator } from "@/components/ui/separator"
import { 
  Loader2, 
  User, 
  Package, 
  MapPin, 
  Contact2, 
  PackageX 
} from "lucide-react"
import api from "@/services/api"
import { toast } from "sonner"

// Interfaces estritamente alinhadas com o seu JSON do Spring Boot
interface ItemPedidoResponseDTO {
  id: number | null
  productId: number
  productName: string  // Corrigido de produtoNome -> productName
  quantity: number     // Corrigido de quantidade -> quantity
  precoUnitario: number
}

interface PagamentoResponseDTO {
  id: number
  valor: number
  parcelas: number | null
  formaPagamento: string | null
  statusPagamento: string
  dataPagamento: string | null
}

interface PedidoResponseDTO {
  pagamento: PagamentoResponseDTO | null
  quantidadeProdutos: number | null
  produtos: ItemPedidoResponseDTO[]
  dataCriacao: string
  valor: number
  status: string
}

export function UserProfileDialog({ open, onOpenChange }: { open: boolean, onOpenChange: (open: boolean) => void }) {
  const [loading, setLoading] = useState(false)
  const [loadingOrders, setLoadingOrders] = useState(false)
  const [isNewUser, setIsNewUser] = useState(false)
  const [pedidos, setPedidos] = useState<PedidoResponseDTO[]>([])
  
  const [userData, setUserData] = useState({
    id: null as number | null,
    nome: "",
    telefone: "",
    dataNascimento: "",
    endereco: {
      id: null as number | null,
      rua: "",
      numero: "",
      complemento: "",
      bairro: "",
      cidade: "",
      estado: "",
      cep: ""
    }
  })

  useEffect(() => {
    if (open) {
      carregarPerfil()
      carregarPedidos()
    }
  }, [open])

  const carregarPerfil = async () => {
    try {
      setLoading(true)
      const response = await api.getProfile()
      const data = response.data || response 

      if (data) {
        setUserData({
          id: data.id || null,
          nome: data.nome || "",
          telefone: data.telefone || "",
          dataNascimento: data.dataNascimento ? data.dataNascimento.substring(0, 10) : "",
          endereco: {
            id: data.endereco?.id || null,
            rua: data.endereco?.rua || "",
            numero: data.endereco?.numero || "",
            complemento: data.endereco?.complemento || "",
            bairro: data.endereco?.bairro || "",
            cidade: data.endereco?.cidade || "",
            estado: data.endereco?.estado || "",
            cep: data.endereco?.cep || ""
          }
        })
        setIsNewUser(false)
      }
    } catch (error: any) {
      const status = error.response?.status
      const message = error.response?.data?.message || ""

      if (status === 500 || status === 404 || message.includes("não encontrado")) {
        setIsNewUser(true)
      } else {
        toast.error("Erro ao carregar dados do perfil")
      }
    } finally {
      setLoading(false)
    }
  }

  const carregarPedidos = async () => {
    try {
      setLoadingOrders(true)
      const response = await api.getPedidos()
      const data = response.data || response
      setPedidos(Array.isArray(data) ? data : [])
    } catch (error) {
      console.error("Erro ao buscar pedidos:", error)
    } finally {
      setLoadingOrders(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    const telefoneApenasNumeros = userData.telefone.replace(/\D/g, "")
    userData.telefone = telefoneApenasNumeros
    try {
      if (isNewUser) {
        await api.upsertProfile(userData)
        toast.success("Perfil criado com sucesso!")
        setIsNewUser(false)
      } else {
        await api.upsertProfile(userData) 
        toast.success("Perfil updated com sucesso!")
      }
    } catch (error: any) {
      const errorData = error.response?.data
      const status = error.response?.status

      if (status === 400 && Array.isArray(errorData) && errorData.length > 0) {
        const primeiroErro = errorData[0]
        toast.error(`${primeiroErro.campo}: ${primeiroErro.mensagem}`)
      } else if (status === 400) {
        toast.error("Dados inválidos. Verifique os campos e tente novamente.")
      } else {
        toast.error("Ocorreu um problema técnico. Tente novamente mais tarde.")
      }
    } finally {
      setLoading(false)
    }
  }

  const formatarMoeda = (valor: number | undefined | null) => {
    return (valor ?? 0).toLocaleString("pt-BR", { style: "currency", currency: "BRL" })
  }

  const formatarData = (dataString: string | undefined | null) => {
    if (!dataString) return "---"
    try {
      return new Date(dataString).toLocaleDateString("pt-BR")
    } catch (e) {
      return "---"
    }
  }

  const getStatusBadge = (status: string | undefined) => {
    const s = status?.toUpperCase() || "PENDENTE"
    if (s === "APPROVED" || s === "PAGO" || s === "ENTREGUE") {
      return <Badge className="bg-green-50 text-green-700 hover:bg-green-50 border-green-200/60" variant="outline">Concluído</Badge>
    }
    if (s === "PENDING" || s === "PENDENTE" || s === "EM_PREPARACAO") {
      return <Badge className="bg-amber-50 text-amber-700 hover:bg-amber-50 border-amber-200/60" variant="outline">Pendente</Badge>
    }
    return <Badge className="bg-zinc-50 text-zinc-600 border-zinc-200" variant="outline">{status}</Badge>
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[650px] max-h-[85vh] flex flex-col overflow-hidden">
        <DialogHeader className="flex-shrink-0">
          <DialogTitle className="font-serif text-2xl">Minha Conta</DialogTitle>
          <DialogDescription>
            Gerencie suas informações pessoais e consulte o andamento de suas compras.
          </DialogDescription>
        </DialogHeader>

        <Tabs defaultValue="dados" className="w-full flex-1 flex flex-col overflow-hidden">
          <TabsList className="grid w-full grid-cols-2 flex-shrink-0 mb-2">
            <TabsTrigger value="dados" className="gap-2"><User className="h-4 w-4" /> Meus Dados</TabsTrigger>
            <TabsTrigger value="pedidos" className="gap-2"><Package className="h-4 w-4" /> Meus Pedidos</TabsTrigger>
          </TabsList>

          {/* ABA DADOS */}
          <TabsContent value="dados" className="pt-2 flex-1 overflow-y-auto pr-1">
            {/* ... o código do formulário original permanece idêntico aqui ... */}
            <form onSubmit={handleSubmit} className="space-y-6 pb-2">
              <div className="space-y-4">
                <h3 className="flex items-center gap-2 font-medium text-sm text-primary border-b pb-2">
                  <Contact2 className="h-4 w-4" /> Dados Pessoais
                </h3>
                <div className="grid gap-4 sm:grid-cols-2">
                  <div className="grid gap-2 sm:col-span-2">
                    <Label htmlFor="nome">Nome Completo</Label>
                    <Input id="nome" value={userData.nome} onChange={(e) => setUserData({...userData, nome: e.target.value})} required />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="telefone">Telefone</Label>
                    <Input id="telefone" value={userData.telefone} onChange={(e) => setUserData({...userData, telefone: e.target.value})} required />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="dataNasc">Data de Nascimento</Label>
                    <Input id="dataNasc" type="date" value={userData.dataNascimento} onChange={(e) => setUserData({...userData, dataNascimento: e.target.value})} />
                  </div>
                </div>
              </div>

              <div className="space-y-4 pt-2">
                <h3 className="flex items-center gap-2 font-medium text-sm text-primary border-b pb-2">
                  <MapPin className="h-4 w-4" /> Endereço Principal
                </h3>
                <div className="grid gap-4 sm:grid-cols-3">
                  <div className="grid gap-2">
                    <Label htmlFor="cep">CEP</Label>
                    <Input id="cep" value={userData.endereco.cep} onChange={(e) => setUserData({...userData, endereco: {...userData.endereco, cep: e.target.value}})} required />
                  </div>
                  <div className="grid gap-2 sm:col-span-2">
                    <Label htmlFor="rua">Rua/Logradouro</Label>
                    <Input id="rua" value={userData.endereco.rua} onChange={(e) => setUserData({...userData, endereco: {...userData.endereco, rua: e.target.value}})} required />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="numero">Número</Label>
                    <Input id="numero" value={userData.endereco.numero} onChange={(e) => setUserData({...userData, endereco: {...userData.endereco, numero: e.target.value}})} required />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="complemento">Complemento</Label>
                    <Input id="complemento" value={userData.endereco.complemento} onChange={(e) => setUserData({...userData, endereco: {...userData.endereco, complemento: e.target.value}})} />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="bairro">Bairro</Label>
                    <Input id="bairro" value={userData.endereco.bairro} onChange={(e) => setUserData({...userData, endereco: {...userData.endereco, bairro: e.target.value}})} required />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="cidade">Cidade</Label>
                    <Input id="cidade" value={userData.endereco.cidade} onChange={(e) => setUserData({...userData, endereco: {...userData.endereco, cidade: e.target.value}})} required />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="estado">Estado (UF)</Label>
                    <Input id="estado" maxLength={2} value={userData.endereco.estado} onChange={(e) => setUserData({...userData, endereco: {...userData.endereco, estado: e.target.value.toUpperCase()}})} required />
                  </div>
                </div>
              </div>
              <Button type="submit" className="w-full bg-primary hover:bg-primary/90" disabled={loading}>
                {loading && <Loader2 className="animate-spin mr-2 h-4 w-4" />}
                {isNewUser ? "Criar Perfil" : "Salvar Alterações"}
              </Button>
            </form>
          </TabsContent>

          {/* ABA HISTÓRICO DE PEDIDOS SIMPLIFICADA */}
          <TabsContent value="pedidos" className="pt-2 flex-1 flex flex-col overflow-hidden">
            {loadingOrders ? (
              <div className="flex flex-col items-center justify-center flex-1 py-12">
                <Loader2 className="h-6 w-6 animate-spin text-primary" />
              </div>
            ) : pedidos.length === 0 ? (
              <div className="flex flex-col items-center justify-center flex-1 py-12 text-center">
                <PackageX className="h-8 w-8 text-muted-foreground/40 mb-2" />
                <p className="text-sm text-muted-foreground">Nenhum pedido encontrado.</p>
              </div>
            ) : (
              <ScrollArea className="flex-1 pr-1">
                <div className="space-y-6 py-2">
                  {pedidos.map((pedido, idx) => (
                    <div key={idx} className="text-sm">
                      
                      {/* Linha Principal do Pedido */}
                      <div className="flex items-center justify-between font-medium text-foreground mb-2">
                        <div className="flex items-center gap-3">
                          <span className="font-serif text-base text-zinc-800">
                            Data: {formatarData(pedido.dataCriacao)}
                          </span>
                          {getStatusBadge(pedido.status)}
                        </div>
                        <span className="font-semibold text-zinc-900 text-base">
                          {formatarMoeda(pedido.valor)}
                        </span>
                      </div>

                      {/* Detalhes internos (Produtos e Forma de Pgto) */}
                      <div className="pl-0 space-y-1 text-zinc-600 text-xs">
                        {pedido.produtos?.map((prod, pIdx) => (
                          <div key={pIdx} className="flex justify-between items-center text-muted-foreground bg-zinc-50/60 px-2 py-1.5 rounded">
                            <span>
                              {prod.productName || "Produto"} 
                              <span className="text-zinc-400 font-normal ml-1.5">({prod.quantity}x)</span>
                            </span>
                            <span className="font-medium text-zinc-700">{formatarMoeda(prod.precoUnitario)}</span>
                          </div>
                        ))}
                        
                        <div className="pt-1 flex items-center justify-between text-[11px] text-zinc-400 px-2">
                          <span>Forma de Pagamento: <strong className="text-zinc-500 uppercase">{pedido.pagamento?.formaPagamento || "A definir"}</strong></span>
                          <span>Produtos Totais: <strong className="text-zinc-500">{pedido.quantidadeProdutos ?? pedido.produtos?.reduce((acc, p) => acc + p.quantity, 0)}</strong></span>
                        </div>
                      </div>

                      {idx < pedidos.length - 1 && <Separator className="mt-4 opacity-60" />}
                    </div>
                  ))}
                </div>
              </ScrollArea>
            )}
          </TabsContent>
        </Tabs>
      </DialogContent>
    </Dialog>
  )
}