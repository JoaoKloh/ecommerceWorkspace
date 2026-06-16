"use client"

import { Area, AreaChart, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts"

const data = [
  { mes: "Jan", vendas: 4000 },
  { mes: "Fev", vendas: 3000 },
  { mes: "Mar", vendas: 5000 },
  { mes: "Abr", vendas: 4500 },
  { mes: "Mai", vendas: 6000 },
  { mes: "Jun", vendas: 5500 },
]

export function SalesChart() {
  return (
    <div className="h-[300px] w-full">
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart data={data} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
          <defs>
            <linearGradient id="colorVendas" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="oklch(0.78 0.08 10)" stopOpacity={0.3} />
              <stop offset="95%" stopColor="oklch(0.78 0.08 10)" stopOpacity={0} />
            </linearGradient>
          </defs>
          <CartesianGrid strokeDasharray="3 3" stroke="oklch(0.90 0.02 10)" />
          <XAxis
            dataKey="mes"
            axisLine={false}
            tickLine={false}
            tick={{ fill: "oklch(0.45 0.02 260)", fontSize: 12 }}
          />
          <YAxis
            axisLine={false}
            tickLine={false}
            tick={{ fill: "oklch(0.45 0.02 260)", fontSize: 12 }}
            tickFormatter={(value) => `R$${value / 1000}k`}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: "oklch(1 0 0)",
              border: "1px solid oklch(0.90 0.02 10)",
              borderRadius: "0.75rem",
              boxShadow: "0 4px 6px -1px rgb(0 0 0 / 0.1)",
            }}
            labelStyle={{ color: "oklch(0.25 0.02 260)", fontWeight: 500 }}
            formatter={(value: number) => [
              new Intl.NumberFormat("pt-BR", {
                style: "currency",
                currency: "BRL",
              }).format(value),
              "Vendas",
            ]}
          />
          <Area
            type="monotone"
            dataKey="vendas"
            stroke="oklch(0.78 0.08 10)"
            strokeWidth={2}
            fillOpacity={1}
            fill="url(#colorVendas)"
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  )
}
