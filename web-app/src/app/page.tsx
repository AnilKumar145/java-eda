import Link from 'next/link';
import { getAllModules } from '@/lib/content';

export default function Home() {
  const modules = getAllModules();

  return (
    <div className="relative">
      {/* Hero Section */}
      <section className="relative py-32 overflow-hidden">
        <div className="container mx-auto px-6 relative z-10">
          <div className="max-w-5xl mx-auto text-center">
            <h1 className="text-6xl md:text-7xl font-bold mb-6 leading-tight tracking-tight">
              Master Python for Enterprise Development
            </h1>
            <p className="text-xl text-[var(--text-secondary)] mb-8 leading-relaxed max-w-3xl mx-auto">
              Professional learning platform with comprehensive modules, hands-on exercises, and real-world application labs.
            </p>
            <div className="flex gap-4 justify-center">
              <Link href="/modules" className="btn btn-primary px-8 py-3 text-base">
                Explore Modules
              </Link>
              <Link href="/modules" className="btn btn-secondary px-8 py-3 text-base">
                Learn More
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* Learning Path Section */}
      <section className="py-24 bg-[var(--surface)]">
        <div className="container mx-auto px-6">
          <div className="max-w-5xl mx-auto">
            <div className="text-center mb-16">
              <h2 className="text-4xl font-bold mb-4">Your Learning Journey</h2>
              <p className="text-lg text-[var(--text-secondary)]">
                A structured path from foundational concepts to enterprise-level applications
              </p>
            </div>
            
            <div className="grid gap-6 md:grid-cols-3">
              {[
                {
                  step: '1',
                  title: 'Knowledge',
                  description: 'Comprehensive lessons covering core concepts, best practices, and real-world examples.',
                  icon: '📚',
                },
                {
                  step: '2',
                  title: 'Exercises',
                  description: 'Practice with progressive difficulty levels and guided hints to master each concept.',
                  icon: '💡',
                },
                {
                  step: '3',
                  title: 'Application Labs',
                  description: 'Build complete projects from healthcare to e-commerce and enterprise systems.',
                  icon: '🚀',
                },
              ].map((item) => (
                <div
                  key={item.step}
                  className="card card-hover p-8"
                >
                  <div className="text-5xl mb-5">{item.icon}</div>
                  <div className="flex items-center gap-3 mb-4">
                    <span className="flex items-center justify-center w-7 h-7 rounded-lg bg-[var(--text-primary)] text-[var(--background)] font-bold text-sm">
                      {item.step}
                    </span>
                    <h3 className="text-xl font-semibold">{item.title}</h3>
                  </div>
                  <p className="text-[var(--text-secondary)] leading-relaxed">
                    {item.description}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Modules Section */}
      <section className="py-24">
        <div className="container mx-auto px-6">
          <div className="max-w-5xl mx-auto">
            <div className="text-center mb-16">
              <h2 className="text-4xl font-bold mb-4">Learning Modules</h2>
              <p className="text-lg text-[var(--text-secondary)]">
                Explore comprehensive modules covering all aspects of Python development
              </p>
            </div>

            <div className="grid gap-4">
              {modules.length > 0 ? (
                modules.map((module, idx) => (
                  <Link
                    key={module}
                    href={`/modules/${module}`}
                    className="card card-hover p-6 group"
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-4 flex-1">
                        <span className="flex items-center justify-center w-12 h-12 rounded-lg bg-[var(--text-primary)] text-[var(--background)] font-bold text-lg">
                          {idx + 1}
                        </span>
                        <div>
                          <h3 className="text-lg font-semibold capitalize">
                            {module.replace(/-/g, ' ')}
                          </h3>
                          <p className="text-sm text-[var(--text-tertiary)] mt-1">
                            Comprehensive module with lessons, exercises, and labs
                          </p>
                        </div>
                      </div>
                      <div className="text-[var(--text-tertiary)] group-hover:text-[var(--text-secondary)] transition-colors">
                        <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                        </svg>
                      </div>
                    </div>
                  </Link>
                ))
              ) : (
                <div className="card p-12 text-center">
                  <h3 className="text-lg font-semibold mb-2">Modules Coming Soon</h3>
                  <p className="text-[var(--text-secondary)]">
                    Content is being carefully developed
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-24 bg-[var(--surface)]">
        <div className="container mx-auto px-6">
          <div className="max-w-5xl mx-auto">
            <div className="text-center mb-16">
              <h2 className="text-4xl font-bold mb-4">Why Python EAD?</h2>
            </div>
            
            <div className="grid md:grid-cols-2 gap-6">
              {[
                {
                  title: 'Structured Learning',
                  description: 'Carefully designed curriculum from fundamentals to advanced enterprise patterns.',
                },
                {
                  title: 'Hands-on Practice',
                  description: 'Learn by doing with 200+ exercises and progressive difficulty levels.',
                },
                {
                  title: 'Real-world Projects',
                  description: 'Build complete applications mirroring enterprise development scenarios.',
                },
                {
                  title: 'Expert Guidance',
                  description: 'Progressive hints and detailed learning outcomes for self-assessment.',
                },
              ].map((feature, idx) => (
                <div key={idx} className="card p-8">
                  <h3 className="text-lg font-semibold mb-3">{feature.title}</h3>
                  <p className="text-[var(--text-secondary)] leading-relaxed">{feature.description}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="py-16">
        <div className="container mx-auto px-6">
          <div className="max-w-5xl mx-auto">
            <div className="grid md:grid-cols-4 gap-8">
              {[
                { label: 'Modules', value: modules.length.toString() },
                { label: 'Learning Hours', value: '100+' },
                { label: 'Exercises', value: '200+' },
                { label: 'App Labs', value: '50+' },
              ].map((stat, i) => (
                <div key={i} className="text-center">
                  <div className="text-3xl font-bold mb-2">{stat.value}</div>
                  <div className="text-sm text-[var(--text-tertiary)]">{stat.label}</div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
