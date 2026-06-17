/** @type {import('next').NextConfig} */
const nextConfig = {
  typescript: {
    ignoreBuildErrors: true,
  },
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: `${process.env.NEXT_PUBLIC_API_URL}/api/:path*`,
      },
    ];
  },
  images: {
    unoptimized: true,
  },
    allowedDevOrigins: [
      '*.loca.lt',
      "ron-unphilologic-ricky.ngrok-free.dev",
      'https://marie-anne-api.serveousercontent.com',
      '*.serveousercontent.com', 
      '*.serveo.net',
      '.trycloudflare.com'
    ],
  }



export default nextConfig
