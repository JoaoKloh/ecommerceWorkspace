import { NextResponse } from 'next/server'
import type { NextRequest } from 'next/server'
import { decodeJwt } from 'jose' 

export function middleware(request: NextRequest) {
  // 1. Captura o cookie completo do navegador
  const tokenCookie = request.cookies.get('accessToken')
  // 🍏 RESOLUÇÃO DO ERRO: Declara explicitamente a string do token
  const token = tokenCookie?.value

  // 🚨 LOGS DE DIAGNÓSTICO NO TERMINAL DO SERVIDOR:
  console.log("=== DIAGNÓSTICO DO MIDDLEWARE ===")
  console.log("Todos os cookies disponíveis:", request.cookies.getAll().map(c => c.name))
  console.log("Valor do accessToken encontrado:", token ? "Sim (Existe)" : "Não (Vazio)")

  if (token) {
    try {
      const payload = decodeJwt(token)
      console.log("Payload decodificado com sucesso:", payload)
    } catch (err: any) {
      console.log("Erro ao decodificar com jose:", err.message)
    }
  }

  const isAdminRoute = request.nextUrl.pathname.startsWith('/admin')

  // Se tentar acessar o painel admin sem o cookie do token, barra imediatamente
  if (isAdminRoute && !token) {
    return NextResponse.redirect(new URL('/login', request.url))
  }

  if (token && isAdminRoute) {
    try {
      // Decodifica o payload de dentro do cookie seguro
      const payload = decodeJwt(token)
      
      // Mapeamento defensivo cobrindo variações do Spring
      const roles = 
        (payload.authorities as string[]) || 
        (payload.authoritities as string[]) || 
        (payload.roles as string[]) || 
        []

      // Verifica se possui o privilégio de admin
      const temAcessoAdmin = roles.includes('ROLE_ADMIN') || roles.includes('ADMIN')

      // Se não for admin, joga para a home do site
      if (!temAcessoAdmin) {
        return NextResponse.redirect(new URL('/', request.url))
      }
    } catch (error) {
      // Se o token estiver corrompido ou mal formado, limpa o cookie e manda logar de novo
      const response = NextResponse.redirect(new URL('/login', request.url))
      response.cookies.delete('accessToken')
      return response
    }
  }

  return NextResponse.next()
}

export const config = {
  matcher: ['/admin/:path*', '/dashboard/:path*'],
}