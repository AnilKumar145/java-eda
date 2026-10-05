import Link from 'next/link';
import { getAllModules, getUnitsForModule } from '@/lib/content';

export default function ModulesPage() {
  const modules = getAllModules();

  return (
    <div className="bg-[var(--background)]">
      {/* Header Section */}
      <div className="py-20">
        <div className="container mx-auto px-6">
          <div className="max-w-3xl mx-auto">
            {/* Breadcrumb */}
            <nav className="mb-8 text-xs text-[var(--text-tertiary)]">
              <Link href="/" className="hover:text-[var(--text-secondary)]">Home</Link>
              <span className="mx-2">/</span>
              <span>Modules</span>
            </nav>
            
            <h1 className="text-5xl font-bold mb-4">Modules</h1>
            <p className="text-lg text-[var(--text-secondary)]">
              Explore all available learning modules and their units
            </p>
          </div>
        </div>
      </div>

      {/* Content Section */}
      <div className="container mx-auto px-6 pb-20">
        <div className="max-w-5xl mx-auto">
          {modules.length > 0 ? (
            <div className="grid gap-4">
              {modules.map((module, idx) => {
                const units = getUnitsForModule(module);
                return (
                  <div key={module} className="card card-hover p-8">
                    <div className="flex items-start gap-6 mb-6">
                      {/* Module Number */}
                      <div className="flex-shrink-0">
                        <div className="w-14 h-14 rounded-lg bg-[var(--text-primary)] text-[var(--background)] flex items-center justify-center text-xl font-bold">
                          {idx + 1}
                        </div>
                      </div>

                      {/* Module Content */}
                      <div className="flex-1">
                        <Link
                          href={`/modules/${module}`}
                          className="text-2xl font-bold text-[var(--text-primary)] hover:text-[var(--text-secondary)] capitalize mb-2 inline-block transition-colors"
                        >
                          {module.replace(/-/g, ' ')}
                        </Link>
                        
                        <p className="text-sm text-[var(--text-secondary)] mb-4">
                          {units.length} {units.length === 1 ? 'unit' : 'units'} available
                        </p>

                        {/* Units */}
                        <div className="flex flex-wrap gap-2">
                          {units.map((unit) => (
                            <Link
                              key={unit}
                              href={`/modules/${module}/${unit}`}
                              className="px-3 py-1.5 bg-[var(--surface)] hover:bg-[var(--surface-secondary)] text-[var(--text-primary)] rounded-lg text-xs font-medium border border-[var(--border)] hover:border-[var(--border-strong)] transition-all"
                            >
                              {unit.replace(/_/g, ' ').replace('unit ', '')}
                            </Link>
                          ))}
                        </div>
                      </div>

                      {/* Arrow */}
                      <div className="flex-shrink-0 text-[var(--text-tertiary)]">
                        <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                        </svg>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          ) : (
            <div className="card p-12 text-center">
              <h2 className="text-2xl font-bold mb-2">No Modules Available</h2>
              <p className="text-[var(--text-secondary)]">
                Modules are being developed. Check back soon.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
