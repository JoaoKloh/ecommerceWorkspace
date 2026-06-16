/** @type {import('next').NextConfig} */
const nextConfig = {
  typescript: {
    ignoreBuildErrors: true,
  },
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'https://ron-unphilologic-ricky.ngrok-free.dev/api/:path*',
        destination: 'https://marie-anne-api.serveousercontent.com/api/:path*',
      },
    ];
  },
  images: {
    unoptimized: true,
  },
    allowedDevOrigins: [
      "ron-unphilologic-ricky.ngrok-free.dev",
      'https://marie-anne-api.serveousercontent.com',
      '*.serveousercontent.com', 
      '*.serveo.net'
    ],
  }



export default nextConfig
