"use client"

import { DollarSign, ShoppingCart, Users, TrendingUp } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { SalesChart } from "@/components/admin/sales-chart"

function formatCurrency(value: number) {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(value)
}

const stats = [
  {
    title: "Total de Vendas",
    value: "R$ 15.780,50",
    change: "+12.5%",
    changeType: "positive" as const,
    icon: DollarSign,
    description: "vs. mês anterior",
  },
  {
    title: "Ticket Médio",
    value: "R$ 125,40",
    change: "+4.2%",
    changeType: "positive" as const,
    icon: ShoppingCart,
    description: "vs. mês anterior",
  },
  {
    title: "Novos Clientes",
    value: "42",
    change: "+8",
    changeType: "positive" as const,
    icon: Users,
    description: "este mês",
  },
  {
    title: "Taxa de Conversão",
    value: "3.2%",
    change: "+0.5%",
    changeType: "positive" as const,
    icon: TrendingUp,
    description: "vs. mês anterior",
  },
]

const recentOrders = [
  { id: "001", cliente: "Maria Silva", valor: 250.0, status: "Confirmado", data: "Hoje, 14:30" },
  { id: "002", cliente: "João Santos", valor: 180.5, status: "Preparando", data: "Hoje, 12:15" },
  { id: "003", cliente: "Ana Costa", valor: 320.0, status: "Entregue", data: "Ontem, 18:00" },
  { id: "004", cliente: "Pedro Oliveira", valor: 95.0, status: "Pendente", data: "Ontem, 10:30" },
]

function getStatusColor(status: string) {
  switch (status) {
    case "Confirmado":
      return "bg-blue-100 text-blue-700"
    case "Preparando":
      return "bg-amber-100 text-amber-700"
    case "Entregue":
      return "bg-green-100 text-green-700"
    case "Pendente":
      return "bg-gray-100 text-gray-700"
    default:
      return "bg-gray-100 text-gray-700"
  }
}

export default function AdminDashboardPage() {
  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="font-serif text-2xl font-bold text-foreground md:text-3xl">
          Dashboard
        </h1>
        <p className="mt-1 text-muted-foreground">
          Visão geral do seu negócio
        </p>
      </div>

      {/* KPI Cards */}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {stats.map((stat) => (
          <Card key={stat.title}>
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary/10">
                  <stat.icon className="h-5 w-5 text-primary" />
                </div>
                <span
                  className={`text-xs font-medium ${
                    stat.changeType === "positive"
                      ? "text-green-600"
                      : "text-red-600"
                  }`}
                >
                  {stat.change}
                </span>
              </div>
              <div className="mt-4">
                <p className="text-2xl font-bold text-foreground">{stat.value}</p>
                <p className="text-sm text-muted-foreground">{stat.title}</p>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Charts and Recent Orders */}
      <div className="grid gap-6 lg:grid-cols-2">
        {/* Sales Chart */}
        <Card className="lg:col-span-1">
          <CardHeader>
            <CardTitle className="font-serif">Vendas Mensais</CardTitle>
            <CardDescription>Tendência de vendas dos últimos 6 meses</CardDescription>
          </CardHeader>
          <CardContent>
            <SalesChart />
          </CardContent>
        </Card>

        {/* Recent Orders */}
        <Card className="lg:col-span-1">
          <CardHeader>
            <CardTitle className="font-serif">Pedidos Recentes</CardTitle>
            <CardDescription>Últimos pedidos realizados</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {recentOrders.map((order) => (
                <div
                  key={order.id}
                  className="flex items-center justify-between rounded-xl border border-border p-4"
                >
                  <div className="flex-1">
                    <p className="font-medium text-foreground">{order.cliente}</p>
                    <p className="text-sm text-muted-foreground">{order.data}</p>
                  </div>
                  <div className="flex items-center gap-4">
                    <span
                      className={`rounded-full px-2.5 py-0.5 text-xs font-medium ${getStatusColor(
                        order.status
                      )}`}
                    >
                      {order.status}
                    </span>
                    <p className="font-semibold text-foreground">
                      {formatCurrency(order.valor)}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
