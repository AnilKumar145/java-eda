/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  // Enable static exports for deployment
  output: 'standalone',
  // Markdown content is in parent directory
  webpack: (config) => {
    config.resolve.alias = {
      ...config.resolve.alias,
      '@content': require('path').resolve(__dirname, '../content'),
    };
    return config;
  },
};

module.exports = nextConfig;
