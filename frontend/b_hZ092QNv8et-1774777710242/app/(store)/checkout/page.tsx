"use client"

import { api } from "@/services/api"
import { useState, useEffect, useCallback } from "react"
import { useCart } from "@/contexts/cart-context"
import { PedidoStep } from "@/components/checkout/pedido-step"
import { MercadoPagoBrick } from "@/components/checkout/mercado-pago-bricks"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { ScrollArea } from "@/components/ui/scroll-area"
import { ShoppingBag, Loader2 } from "lucide-react"
import { toast } from "sonner"
import { useRouter } from "next/navigation"
import Image from "next/image"

export default function CheckoutPage() {
  const { items, subtotalLocal, limparCarrinho } = useCart()
  const router = useRouter()
  
  const [formData, setFormData] = useState({
    nome: "",
    telefone: "",
    cpf: "", 
    email: "", // 💡 Adicionado caso capture no mesmo formulário
    dataNascimento: "",
    cep: "",
    rua: "",
    numero: "",
    complemento: "",
    bairro: "",
    cidade: "",
    estado: "",
    dataRetirada: "",
    horarioRetirada: "",
    observacoes: "",
  })

  const [showPayment, setShowPayment] = useState(false)
  const [preferenceId, setPreferenceId] = useState("")
  const [pedidoId, setPedidoId] = useState<string | number>("")
  const [criandoPedido, setCriandoPedido] = useState(false)
  const [amountFromBackend, setAmountFromBackend] = useState(0) 

  const updateFormData = (data: Partial<typeof formData>) => {
    setFormData((prev) => ({ ...prev, ...data }))
  }

  useEffect(() => {
    if (items.length === 0 && !showPayment) {
      router.push("/")
    }
  }, [items, router, showPayment])

  const handleCriarPedidoEObterPreferencia = async () => {
    if (criandoPedido || showPayment) return

    setCriandoPedido(true)
    try {
      const payloadDto = {
        dataRetirada: formData.dataRetirada,    
        horaRetirada: formData.horarioRetirada, 
      }

      const data = await api.createOrder(payloadDto) 
      limparCarrinho()
      const novoPedidoId = data.pedidoId || data.id
      const mercadoPagoPreferenceId = data.preferenceId 
      const valorTotal = data.amount 

      if (!novoPedidoId || !mercadoPagoPreferenceId || !valorTotal) {
        throw new Error("O servidor não retornou todos os dados necessários (id, preferenceId ou amount).")
      }

      setPedidoId(novoPedidoId)
      setPreferenceId(mercadoPagoPreferenceId) 
      setAmountFromBackend(valorTotal)
      
      setShowPayment(true) 

      toast.success("Pedido registrado! Escolha a forma de pagamento.")
    } catch (error) {
      console.error(error)
      toast.error("Erro ao registrar o pedido. Verifique os horários e tente novamente.")
    } finally {
      setCriandoPedido(false)
    }
  }

  const handlePaymentSuccess = useCallback(() => {
    toast.success("Pagamento aprovado e pedido realizado!")
    router.push("/?openProfile=pedidos")
  }, [router])

  const formatarMoeda = (valor: number) => {
    return valor.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })
  }

  return (
    <div className="w-full space-y-5 p-4 sm:p-6 max-w-7xl mx-auto">
      
      <div className="flex flex-col gap-1 sm:flex-row sm:items-center sm:justify-between border-b border-border/40 pb-4">
        <div>
          <h1 className="text-xl font-bold tracking-tight text-foreground sm:text-2xl md:text-3xl">
            {showPayment ? "Realizar Pagamento" : "Finalizar Pedido"}
          </h1>
          <p className="mt-0.5 text-xs sm:text-sm text-muted-foreground">
            {showPayment 
              ? "Seu pedido foi reservado com sucesso. Escolha um método seguro para pagar:" 
              : "Confirme os detalhes da entrega/retirada e realize o pagamento seguro"}
          </p>
        </div>
      </div>

      <div className={`grid grid-cols-1 gap-5 items-start transition-all duration-300 ${
        showPayment ? "md:grid-cols-2 max-w-5xl mx-auto" : "lg:grid-cols-12"
      }`}>
        
        {!showPayment && (
          <div className="lg:col-span-7 space-y-5 order-1">
            <PedidoStep 
              formData={formData} 
              updateFormData={updateFormData} 
              onNext={handleCriarPedidoEObterPreferencia} 
              isPending={criandoPedido}
              paymentActive={showPayment}
            />
          </div>
        )}

        <div className={`space-y-5 order-2 ${
          showPayment ? "w-full animate-in fade-in duration-300" : "lg:col-span-5 lg:sticky lg:top-6"
        }`}>
          
          <Card className="rounded-xl border border-border bg-card shadow-sm overflow-hidden">
            <CardHeader className="p-4 sm:p-6 pb-3">
              <div className="flex items-center gap-2">
                <ShoppingBag className="h-4 w-4 text-muted-foreground" />
                <CardTitle className="text-base sm:text-lg font-bold tracking-tight">Resumo do Pedido</CardTitle>
              </div>
              <CardDescription className="text-xs font-medium text-muted-foreground/80">
                Confira as delícias selecionadas no catálogo
              </CardDescription>
            </CardHeader>
            
            <CardContent className="p-4 sm:p-6 pt-0 space-y-4">
              <ScrollArea className="h-full max-h-[160px] sm:max-h-[320px] pr-1">
                <div className="space-y-3">
                  {items.map((item) => (
                    <div key={item.produto.id} className="flex items-center gap-3 border-b border-border/40 pb-2.5 last:border-0 last:pb-0">
                      
                      <div className="h-10 w-10 flex-shrink-0 overflow-hidden rounded-xl border border-border bg-muted relative shadow-sm">
                        <Image
                          src={item.produto.imagem || "/placeholder.png"}
                          alt={item.produto.nome}
                          fill
                          className="object-cover"
                        />
                      </div>
                      
                      <div className="flex-1 min-w-0 space-y-0.5">
                        <h4 className="font-semibold text-foreground text-xs sm:text-sm tracking-tight truncate">
                          {item.produto.nome}
                        </h4>
                        <p className="text-[11px] sm:text-xs font-medium text-muted-foreground/80">
                          {item.quantidade}x · {formatarMoeda(item.produto.precoComDesconto || item.produto.preco)}
                        </p>
                      </div>
                      
                      <div className="font-semibold text-xs sm:text-sm text-foreground whitespace-nowrap">
                        {formatarMoeda((item.produto.precoComDesconto || item.produto.preco) * item.quantidade)}
                      </div>
                    </div>
                  ))}
                </div>
              </ScrollArea>

              <Separator className="border-border/60" />

              <div className="space-y-1.5 text-xs sm:text-sm font-medium">
                <div className="flex justify-between text-muted-foreground/80">
                  <span>Subtotal</span>
                  <span className="text-foreground">{formatarMoeda(subtotalLocal)}</span>
                </div>
                <div className="flex justify-between items-center text-muted-foreground/80">
                  <span>Retirada no Local</span>
                  <span className="text-emerald-600 bg-emerald-500/10 px-2 py-0.5 rounded-lg text-[11px] font-bold">GRÁTIS</span>
                </div>
                
                <Separator className="my-2 border-border/60" />
                
                <div className="flex justify-between items-center pt-0.5">
                  <span className="font-bold text-foreground">Total Geral</span>
                  <span className="text-lg sm:text-xl font-bold tracking-tight text-foreground">
                    {formatarMoeda(subtotalLocal)}
                  </span>
                </div>
              </div>
            </CardContent>
          </Card>

          {criandoPedido && (
            <Card className="rounded-xl border border-dashed border-border bg-card shadow-sm">
              <CardContent className="p-6 flex flex-col items-center justify-center gap-2 text-muted-foreground">
                <Loader2 className="animate-spin h-5 w-5 text-primary" />
                <span className="text-xs font-medium text-center">Registrando seu pedido no catálogo...</span>
              </CardContent>
            </Card>
          )}
        </div>

        {showPayment && preferenceId && amountFromBackend > 0 && (
          <div className="w-full order-3 animate-in fade-in slide-in-from-bottom-4 duration-500 md:delay-75">
            <div className="rounded-xl overflow-hidden shadow-sm border border-border bg-card">
              {/* 🔥 INJEÇÃO DOS DADOS DO CLIENTE: Populando dinamicamente os parâmetros do Brick */}
              <MercadoPagoBrick 
                preferenceId={preferenceId}
                amount={amountFromBackend} 
                pedidoId={pedidoId}
                onPaymentSuccess={handlePaymentSuccess}
                customerEmail={formData.email} // Se não capturar o e-mail no checkout, passe um valor dinâmico da sessão do usuário
                customerCpf={formData.cpf}
              />
            </div>
          </div>
        )}

      </div>
    </div>
  )
}