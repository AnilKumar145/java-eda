import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import './globals.css';
import Link from 'next/link';

const inter = Inter({ subsets: ['latin'] });

export const metadata: Metadata = {
  title: 'Python EAD - Enterprise Application Development',
  description: 'Comprehensive Python learning platform for enterprise application development',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className="dark">
      <body className={inter.className}>
        <div className="min-h-screen flex flex-col">
          {/* Header */}
          <header className="sticky top-0 z-50 bg-[var(--background)]/80 backdrop-blur-2xl border-b border-[var(--border)]">
            <div className="container mx-auto px-6 py-3">
              <div className="flex items-center justify-between">
                <Link href="/" className="flex items-center gap-3 group">
                  <div className="w-9 h-9 bg-[var(--text-primary)] text-[var(--background)] rounded-lg flex items-center justify-center font-bold text-lg">
                    Py
                  </div>
                  <div>
                    <h1 className="text-sm font-semibold text-[var(--text-primary)]">Python EAD</h1>
                  </div>
                </Link>
                
                <nav className="flex items-center gap-8">
                  <Link href="/" className="text-sm font-medium text-[var(--text-secondary)] hover:text-[var(--text-primary)] transition-colors">
                    Home
                  </Link>
                  <Link href="/modules" className="text-sm font-medium text-[var(--text-secondary)] hover:text-[var(--text-primary)] transition-colors">
                    Modules
                  </Link>
                  <button className="btn btn-primary px-6 py-2 text-sm">
                    Get Started
                  </button>
                </nav>
              </div>
            </div>
          </header>
          
          {/* Main Content */}
          <main className="flex-1">
            {children}
          </main>
          
          {/* Footer */}
          <footer className="bg-[var(--surface)] border-t border-[var(--border)]">
            <div className="container mx-auto px-6 py-12">
              <div className="grid md:grid-cols-4 gap-12 mb-12">
                <div className="col-span-2">
                  <div className="flex items-center gap-3 mb-4">
                    <div className="w-9 h-9 bg-[var(--text-primary)] text-[var(--background)] rounded-lg flex items-center justify-center font-bold">
                      Py
                    </div>
                    <span className="text-sm font-semibold">Python EAD</span>
                  </div>
                  <p className="text-xs text-[var(--text-tertiary)] max-w-sm leading-relaxed">
                    Professional Python learning platform designed for enterprise application development. Master Python through comprehensive modules, hands-on exercises, and real-world projects.
                  </p>
                </div>
                
                <div>
                  <h3 className="font-semibold mb-4 text-xs uppercase tracking-widest text-[var(--text-primary)]">Learning</h3>
                  <ul className="space-y-3 text-xs text-[var(--text-secondary)]">
                    <li><Link href="/modules" className="hover:text-[var(--text-primary)] transition-colors">Modules</Link></li>
                    <li><Link href="/modules" className="hover:text-[var(--text-primary)] transition-colors">Exercises</Link></li>
                    <li><Link href="/modules" className="hover:text-[var(--text-primary)] transition-colors">Labs</Link></li>
                  </ul>
                </div>
                
                <div>
                  <h3 className="font-semibold mb-4 text-xs uppercase tracking-widest text-[var(--text-primary)]">Resources</h3>
                  <ul className="space-y-3 text-xs text-[var(--text-secondary)]">
                    <li><a href="#" className="hover:text-[var(--text-primary)] transition-colors">Documentation</a></li>
                    <li><a href="#" className="hover:text-[var(--text-primary)] transition-colors">Support</a></li>
                    <li><a href="#" className="hover:text-[var(--text-primary)] transition-colors">About</a></li>
                  </ul>
                </div>
              </div>
              
              <div className="border-t border-[var(--border)] pt-8 text-center text-xs text-[var(--text-tertiary)]">
                © {new Date().getFullYear()} Python EAD. All rights reserved.
              </div>
            </div>
          </footer>
        </div>
      </body>
    </html>
  );
}
