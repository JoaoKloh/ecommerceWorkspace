"use client"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Loader2, ArrowRight } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { toast } from "sonner"
import api from "@/services/api"

export default function LoginPage() {
  const router = useRouter()
  const [mounted, setMounted] = useState(false)
  const [step, setStep] = useState<'email' | 'code'>('email')
  const [isLoading, setIsLoading] = useState(false)
  const [email, setEmail] = useState("")
  const [code, setCode] = useState("")

  useEffect(() => {
    setMounted(true)
  }, [])

  const handleGoogleLogin = () => {
    window.location.href = `${process.env.NEXT_PUBLIC_API_URL}/oauth2/authorization/google`
  }

  const handleRequestCode = async (e: React.FormEvent) => {
    e.preventDefault()
    const cleanEmail = email.trim()
    if (!cleanEmail) return

    setIsLoading(true)
    try {
      await api.gerarCodigo(cleanEmail)
      toast.success("Código enviado com sucesso.")
      setStep('code')
    } catch (error: any) {
      toast.error("Erro ao processar sua identificação.")
    } finally {
      setIsLoading(false)
    }
  }

  const handleVerifyCode = async (e: React.FormEvent) => {
    e.preventDefault()
    if (code.length < 6) return

    setIsLoading(true)
    try {
      const response = await api.login({ email: email.trim(), codigo: code })
      const data = response.data || response
      
      toast.success("Login realizado com sucesso!")
      const roles = data.authoritities 
      const isAdmin = roles.includes("ROLE_ADMIN")

      if (isAdmin) {
        router.push("/admin")
      } else {
        router.push("/")
      }
      
      router.refresh()
    } catch (error: any) {
      console.error("Erro no login:", error)
      toast.error("Código inválido ou expirado.")
    } finally {
      setIsLoading(false)
    }
  }

  if (!mounted) return <div className="min-h-screen bg-[#fef1f2]" />

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-[#fef1f2] px-6">
      <div className="w-full max-w-[380px] space-y-10">
        <header className="text-center space-y-3">
          <h1 className="font-serif text-4xl tracking-tight text-slate-900">Marie e Anne</h1>
          <div className="space-y-1">
            <h2 className="text-xl font-semibold text-slate-800">Fazer login</h2>
            <p className="text-[10px] uppercase tracking-[0.3em] text-slate-400 font-medium">
              {step === 'email' ? 'Identificação' : 'Verificação por E-mail'}
            </p>
          </div>
        </header>

        <main className="space-y-6">
          {step === 'email' && (
            <>
              <Button 
                onClick={handleGoogleLogin}
                variant="outline"
                className="w-full h-12 rounded-lg border-slate-200 bg-white text-slate-700 font-medium flex items-center justify-center gap-3 hover:bg-slate-50 transition-colors shadow-sm"
              >
                <svg className="h-5 w-5" viewBox="0 0 24 24">
                  <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
                  <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
                  <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l3.66-2.84z" fill="#FBBC05"/>
                  <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
                </svg>
                Continuar com Google
              </Button>

              <div className="relative flex items-center justify-center">
                <div className="absolute inset-0 flex items-center"><span className="w-full border-t border-slate-200"></span></div>
                <span className="relative bg-[#fef1f2] px-4 text-xs text-slate-400 uppercase tracking-widest">ou</span>
              </div>
            </>
          )}

          <div className="bg-[#f8fafc] rounded-xl p-2 shadow-sm border border-slate-100/50">
            {step === 'email' ? (
              <form onSubmit={handleRequestCode} className="relative flex items-center">
                <Input
                  type="email"
                  placeholder="E-mail"
                  required
                  autoFocus
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="h-14 w-full border-0 bg-transparent px-4 text-lg transition-all focus-visible:ring-0 placeholder:text-slate-300 text-slate-700"
                  disabled={isLoading}
                />
                <button
                  type="submit"
                  disabled={isLoading || !email}
                  className="p-3 text-slate-300 hover:text-pink-400 disabled:opacity-20 transition-colors"
                >
                  {isLoading ? <Loader2 className="h-5 w-5 animate-spin" /> : <ArrowRight className="h-6 w-6" />}
                </button>
              </form>
            ) : (
              <div className="animate-in fade-in slide-in-from-right-4 duration-500">
                <form onSubmit={handleVerifyCode} className="relative flex items-center">
                  <Input
                    type="text"
                    placeholder="Código de 6 dígitos"
                    maxLength={6}
                    required
                    autoFocus
                    value={code}
                    onChange={(e) => setCode(e.target.value.replace(/\D/g, ""))}
                    className="h-14 w-full border-0 bg-transparent px-4 text-center font-mono text-2xl tracking-[0.4em] focus-visible:ring-0 text-slate-700"
                    disabled={isLoading}
                  />
                  <button
                    type="submit"
                    disabled={isLoading || code.length < 6}
                    className="p-3 text-slate-300 hover:text-pink-400 disabled:opacity-20 transition-colors"
                  >
                    {isLoading ? <Loader2 className="h-5 w-5 animate-spin" /> : <ArrowRight className="h-6 w-6" />}
                  </button>
                </form>
              </div>
            )}
          </div>
        </main>

        <footer className="pt-8 text-center border-t border-slate-100/50">
          <p className="text-[10px] text-slate-300 uppercase tracking-[0.25em] font-light">
            Marie e Anne &copy; 2026
          </p>
        </footer>
      </div>
    </div>
  )
}

function carregarCarrinhoDoUsuario() {
  throw new Error("Function not implemented.")
}
