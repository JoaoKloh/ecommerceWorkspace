"use client"

import { useEffect, useState, useRef } from "react"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Loader2, CreditCard, Copy, Check } from "lucide-react"
import { api } from "@/services/api"
import { toast } from "sonner"
import { Button } from "@/components/ui/button"

interface MercadoPagoBrickProps {
  preferenceId: string
  amount: number
  pedidoId: string | number
  onPaymentSuccess: () => void
  customerEmail: string
  customerCpf: string
}

// Interface para tipar os dados do Pix que vêm do backend
interface PixData {
  qrCode: string
  qrCodeBase64: string
}

export function MercadoPagoBrick({ 
  preferenceId,
  amount,
  pedidoId, 
  onPaymentSuccess,
  customerEmail, // 👑 CORREÇÃO: Desestruturado aqui
  customerCpf    // 👑 CORREÇÃO: Desestruturado aqui
}: MercadoPagoBrickProps) {
  const [loadingSDK, setLoadingSDK] = useState(true)
  const [pixData, setPixData] = useState<PixData | null>(null) // 🔥 Estado para armazenar dados do PIX
  const [copiado, setCopiado] = useState(false)
  const brickControllerRef = useRef<any>(null)
  const pedidoIdRef = useRef(pedidoId)

  useEffect(() => {
    pedidoIdRef.current = pedidoId
  }, [pedidoId])

  // Função para copiar o código Pix para o clipboard do usuário
  const handleCopiarPix = async (codigo: string) => {
    try {
      await navigator.clipboard.writeText(codigo)
      setCopiado(true)
      toast.success("Código Pix copiado para a área de transferência!")
      setTimeout(() => setCopiado(false), 3000)
    } catch (err) {
      toast.error("Não foi possível copiar automaticamente. Selecione o texto e copie.")
    }
  }

  useEffect(() => {
    if (!preferenceId || !amount || amount <= 0) return

    let ignorar = false
    let timeoutId: NodeJS.Timeout

    const inicializarBrick = async () => {
      if (ignorar) return
      
      const container = document.getElementById("paymentBrick_container")
      if (!container) {
        timeoutId = setTimeout(verificarEcarregar, 50)
        return
      }

      container.innerHTML = "" 

      try {
        const mpInstance = new (window as any).MercadoPago("TEST-36737fd2-3f26-4149-95af-dfaec01c788f", {
          locale: "pt-BR",
        })
        
        const bricksBuilder = mpInstance.bricks()
        const settings = {
          initialization: {
            amount: amount,
            // 🔥 Bloco injetado dinamicamente com as propriedades do componente
            payer: {
              email: customerEmail,
              identification: {
                type: "CPF",
                number: customerCpf ? customerCpf.replace(/[^0-9]/g, "") : "", // Prevenção contra undefined
              },
            },
          },
          customization: {
            visual: { 
              theme: "flat",
              variables: {
                colorPrimary: "#0bac26",      
                colorPrimaryDark: "#0a923c",  
              }
            },
            paymentMethods: {
              creditCard: "all",
              bankTransfer: ["pix"]
            },
          },
          callbacks: {
            onReady: () => {
              if (!ignorar) setLoadingSDK(false)
            },
            onSubmit: ({ selectedPaymentMethod, formData }: any) => {
              return new Promise<void>(async (resolve, reject) => {
                try {
                  console.log("Meio selecionado:", selectedPaymentMethod)
                  const idAtualizado = Number(pedidoIdRef.current)

                  if (!idAtualizado) {
                    throw new Error("O ID do pedido não foi localizado na memória.")
                  }

                  const dadosParaEnvio = {
                    token: formData.token || null,
                    description: formData.description || `Pagamento do Pedido #${idAtualizado}`,
                    paymentMethodId: formData.payment_method_id,
                    idPedido: idAtualizado 
                  }

                  console.log("=== ENVIANDO AO BACKEND ===", dadosParaEnvio)
                  
                  // 🔥 CAPTURA: Armazenamos a resposta do endpoint do seu backend
                  const resposta = await api.solicitarPagamento(dadosParaEnvio)

                  // Se o método escolhido foi Pix, o backend trará o bloco transaction_data mapeado
                  if (selectedPaymentMethod === "bank_transfer" || formData.payment_method_id === "pix") {
                    const txData = resposta?.point_of_interaction?.transaction_data
                    
                    if (txData?.qr_code && txData?.qr_code_base64) {
                      setPixData({
                        qrCode: txData.qr_code,
                        qrCodeBase64: txData.qr_code_base64
                      })
                      toast.success("QR Code do Pix gerado com sucesso!")
                      resolve()
                      return
                    }
                  }

                  // Fluxo normal para cartão de crédito
                  toast.success("Pagamento enviado para processamento!")
                  onPaymentSuccess() 
                  resolve() 
                } catch (error: any) {
                  console.error(error)
                  const msgErro = error.response?.data?.message || error.message || "Falha ao processar o pagamento."
                  toast.error(msgErro)
                  reject() 
                }
              })
            },
            onError: (error: any) => {
              console.error("Erro interno no Brick:", error)
              if (!ignorar) setLoadingSDK(false)
            },
          },
        }

        if (brickControllerRef.current && typeof brickControllerRef.current.unmount === "function") {
          try {
            brickControllerRef.current.unmount()
          } catch (err) {
            console.warn("Aviso ao remover brick órfão:", err)
          }
        }

        if (!ignorar) {
          const controller = await bricksBuilder.create("payment", "paymentBrick_container", settings)
          brickControllerRef.current = controller
        }
      } catch (err) {
        console.error("Erro ao construir o Brick:", err)
      }
    }

    const verificarEcarregar = () => {
      if (ignorar) return
      if (typeof window !== "undefined" && (window as any).MercadoPago) {
        inicializarBrick()
      } else {
        timeoutId = setTimeout(verificarEcarregar, 100)
      }
    }

    const scriptId = "mercadopago-sdk"
    let script = document.getElementById(scriptId) as HTMLScriptElement

    if (!script) {
      script = document.createElement("script")
      script.id = scriptId
      script.src = "https://sdk.mercadopago.com/js/v2"
      script.async = true
      document.body.appendChild(script)
    }

    verificarEcarregar()

    return () => {
      ignorar = true
      clearTimeout(timeoutId)
      
      if (brickControllerRef.current && typeof brickControllerRef.current.unmount === "function") {
        try {
          brickControllerRef.current.unmount() 
        } catch (err) {
          console.warn("Aviso ao desmontar o Brick:", err)
        }
        brickControllerRef.current = null
      }
    }
    // 👑 CORREÇÃO: Inclusão de customerEmail e customerCpf nas dependências do hook
  }, [preferenceId, amount, onPaymentSuccess, customerEmail, customerCpf])

  const dadosProntos = !!(preferenceId && amount && amount > 0)

  return (
    <Card className="border-none shadow-sm bg-muted/10">
      <CardHeader>
        <div className="flex items-center gap-2">
          <CreditCard className="h-5 w-5 text-primary" />
          <CardTitle className="font-serif text-xl">
            {pixData ? "Pague com seu Pix" : "Pagamento Seguro"}
          </CardTitle>
        </div>
        <CardDescription>
          {pixData ? "Escaneie o código ou copie a linha de transferência abaixo" : "Escolha entre PIX ou Cartão de Crédito"}
        </CardDescription>
      </CardHeader>
      <CardContent>
        {/* Loader de Inicialização */}
        {(!dadosProntos || loadingSDK) && !pixData && (
          <div className="flex flex-col items-center justify-center py-8">
            <Loader2 className="h-8 w-8 animate-spin text-primary" />
            <p className="mt-2 text-sm text-muted-foreground">Carregando módulos de pagamento...</p>
          </div>
        )}
        
        {/* 🔥 RENDERIZAÇÃO DO PIX: Se pixData possuir valores, renderiza a tela do QR Code */}
        {pixData ? (
          <div className="flex flex-col items-center justify-center space-y-6 py-4 animate-in fade-in duration-300">
            
            {/* Imagem do QR Code convertida de Base64 */}
            <div className="bg-white p-4 rounded-xl border shadow-inner max-w-[240px] w-full aspect-square flex items-center justify-center">
              <img 
                src={`data:image/png;base64,${pixData.qrCodeBase64}`} 
                alt="QR Code Pix"
                className="w-full h-full object-contain"
              />
            </div>

            {/* Caixa do Pix Copia e Cola */}
            <div className="w-full space-y-2">
              <label className="text-xs font-semibold text-muted-foreground uppercase tracking-wider">
                Pix Copia e Cola
              </label>
              <div className="flex items-center gap-2 bg-background p-2 rounded-lg border">
                <input 
                  type="text" 
                  readOnly 
                  value={pixData.qrCode}
                  className="bg-transparent text-xs text-muted-foreground w-full focus:outline-none select-all font-mono truncate px-1"
                />
                <Button
                  size="sm"
                  variant="outline"
                  onClick={() => handleCopiarPix(pixData.qrCode)}
                  className="h-8 px-3 shrink-0"
                >
                  {copiado ? (
                    <Check className="h-4 w-4 text-green-600 animate-in zoom-in-50" />
                  ) : (
                    <Copy className="h-4 w-4" />
                  )}
                </Button>
              </div>
            </div>

            {/* Mensagem informativa para o cliente */}
            <div className="bg-primary/5 border border-primary/10 rounded-lg p-3 text-center w-full">
              <p className="text-xs text-muted-foreground leading-relaxed">
                Após efetuar a transferência no app do seu banco, o pedido será atualizado automaticamente pelo nosso sistema.
              </p>
            </div>
          </div>
        ) : (
          /* Container Nativo do Mercado Pago (Escondido se o Pix estiver ativo) */
          <div 
            id="paymentBrick_container" 
            className={(!dadosProntos || loadingSDK || pixData) ? "hidden" : "block"} 
          />
        )}
      </CardContent>
    </Card>
  )
}