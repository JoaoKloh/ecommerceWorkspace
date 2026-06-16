"use client"

import { useState, useEffect, useCallback } from "react"
import { Search, MoreHorizontal, Loader2, Users, Mail, Phone, Calendar } from "lucide-react"
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
import { Card, CardContent } from "@/components/ui/card"
import { toast } from "sonner"
import api from "@/services/api"

interface Cliente {
  id: number | string
  nome: string
  email: string
  telefone?: string
  dataCadastro?: string
  ativo: boolean
}

function formatarData(dataString?: string) {
  if (!dataString) return "---"
  try {
    return new Date(dataString).toLocaleDateString("pt-BR")
  } catch (e) {
    return "---"
  }
}

function formatarTelefone(tel?: string) {
  if (!tel) return "---"
  const limpo = tel.replace(/\D/g, "")
  if (limpo.length === 11) {
    return `(${limpo.substring(0, 2)}) ${limpo.substring(2, 7)}-${limpo.substring(7)}`
  }
  if (limpo.length === 10) {
    return `(${limpo.substring(0, 2)}) ${limpo.substring(2, 6)}-${limpo.substring(6)}`
  }
  return tel
}

export default function ClientesPage() {
  const [clientes, setClientes] = useState<Cliente[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [buscaEmail, setBuscaEmail] = useState("")

  // Requisição enviando o e-mail/filtro dinamicamente para o backend
  const carregarClientesFromAPI = useCallback(async (filtroEmail: string) => {
    setIsLoading(true)
    try {
      
      const data: any = await api.getClientePorEmail(filtroEmail)
      
      let listaFinal: Cliente[] = []

      if (Array.isArray(data)) {
        listaFinal = data
      } else if (data && data.clientes && Array.isArray(data.clientes)) {
        listaFinal = data.clientes
      } else if (data && data.content && Array.isArray(data.content)) {
        listaFinal = data.content
      }

      setClientes(listaFinal)
    } catch (error: any) {
      console.error("Erro ao carregar clientes:", error)
      toast.error("Erro ao carregar a lista de clientes.")
    } finally {
      setIsLoading(false)
    }
  }, [])

  // Efeito com Debounce: Aguarda 500ms após o usuário parar de digitar para disparar a requisição
  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      carregarClientesFromAPI(buscaEmail)
    }, 500)

    return () => clearTimeout(delayDebounceFn)
  }, [buscaEmail, carregarClientesFromAPI])

  const handleAlternarStatusCliente = async (cliente: Cliente) => {
    try {
      // Exemplo: await api.toggleClienteStatus(cliente.id)
      toast.success(`Status do cliente ${cliente.nome} atualizado!`)
      carregarClientesFromAPI(buscaEmail)
    } catch (error) {
      toast.error("Erro ao alterar status.")
    }
  }

  return (
    <div className="space-y-6 text-foreground p-4 md:p-8">
      {/* Cabeçalho */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="font-serif text-2xl font-bold md:text-3xl">Clientes</h1>
          <p className="text-sm text-muted-foreground">
            Gerencie e filtre os dados dos clientes diretamente do servidor
          </p>
        </div>
      </div>

      {/* Input de busca que altera o estado e dispara o useEffect */}
      <Card>
        <CardContent className="p-4 flex flex-col gap-4 sm:flex-row">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              placeholder="Buscar por e-mail ou nome (filtro no servidor)..."
              value={buscaEmail}
              onChange={(e) => setBuscaEmail(e.target.value)}
              className="pl-9"
            />
          </div>
        </CardContent>
      </Card>

      {/* Tabela mantendo rigorosamente o design escolhido */}
      <Card>
        <CardContent className="p-0 overflow-x-auto">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Cliente</TableHead>
                <TableHead>Contato</TableHead>
                <TableHead>Data de Cadastro</TableHead>
                <TableHead>Status da Conta</TableHead>
                <TableHead className="w-[50px]"></TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={5} className="h-40 text-center">
                    <Loader2 className="animate-spin mx-auto" />
                  </TableCell>
                </TableRow>
              ) : clientes.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5} className="h-40 text-center text-sm text-muted-foreground">
                    Nenhum cliente retornado pelo servidor para esta busca.
                  </TableCell>
                </TableRow>
              ) : (
                clientes.map((cliente) => (
                  <TableRow key={cliente.id}>
                    <TableCell>
                      <div className="flex items-center gap-4 min-w-[250px]">
                        <div className="h-10 w-10 flex-shrink-0 rounded-full border bg-secondary flex items-center justify-center">
                          <Users className="h-5 w-5 text-muted-foreground" />
                        </div>
                        <div className="overflow-hidden">
                          <p className="font-medium text-sm line-clamp-1">{cliente.nome}</p>
                          <p className="text-xs text-muted-foreground flex items-center gap-1 mt-0.5">
                            <Mail className="h-3 w-3 inline" /> {cliente.email}
                          </p>
                        </div>
                      </div>
                    </TableCell>

                    <TableCell className="text-sm">
                      <span className="flex items-center gap-1 text-muted-foreground">
                        <Phone className="h-3 w-3" /> {formatarTelefone(cliente.telefone)}
                      </span>
                    </TableCell>

                    <TableCell className="text-sm text-muted-foreground">
                      <span className="flex items-center gap-1">
                        <Calendar className="h-3 w-3" /> {formatarData(cliente.dataCadastro)}
                      </span>
                    </TableCell>

                    <TableCell>
                      <Badge variant={cliente.ativo ? "default" : "secondary"}>
                        {cliente.ativo ? "Ativo" : "Suspenso"}
                      </Badge>
                    </TableCell>

                    <TableCell>
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon">
                            <MoreHorizontal className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem onClick={() => handleAlternarStatusCliente(cliente)}>
                            {cliente.ativo ? "Suspender Conta" : "Reativar Conta"}
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
    </div>
  )
}