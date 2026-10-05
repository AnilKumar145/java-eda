import Link from 'next/link';
import { getAllModules, getUnitsForModule } from '@/lib/content';

export default function ModulePage({ params }: { params: { module: string } }) {
  const { module } = params;
  const units = getUnitsForModule(module);

  return (
    <div className="max-w-4xl mx-auto">
      {/* Breadcrumb */}
      <nav className="mb-6 text-sm text-gray-600 dark:text-gray-400">
        <Link href="/" className="hover:text-primary-blue">Home</Link>
        {' / '}
        <Link href="/modules" className="hover:text-primary-blue">Modules</Link>
        {' / '}
        <span className="capitalize">{module.replace(/-/g, ' ')}</span>
      </nav>

      {/* Module Header */}
      <header className="mb-8">
        <h1 className="text-4xl font-bold text-primary-blue mb-4 capitalize">
          {module.replace(/-/g, ' ')} Module
        </h1>
        <p className="text-lg text-gray-600 dark:text-gray-300">
          Explore units and master key concepts step by step.
        </p>
      </header>

      {/* Units List */}
      <section>
        <h2 className="text-2xl font-semibold mb-4">Units</h2>
        <div className="grid gap-4">
          {units.length > 0 ? (
            units.map((unit) => (
              <Link
                key={unit}
                href={`/modules/${module}/${unit}`}
                className="border rounded-lg p-6 hover:border-primary-blue hover:shadow-lg transition"
              >
                <h3 className="text-xl font-semibold mb-2 capitalize">
                  {unit.replace(/_/g, ' ').replace('unit ', 'Unit ')}
                </h3>
                <p className="text-gray-600 dark:text-gray-300">
                  Knowledge • Exercises • App Labs →
                </p>
              </Link>
            ))
          ) : (
            <p className="text-gray-600 dark:text-gray-300">
              No units available yet for this module.
            </p>
          )}
        </div>
      </section>
    </div>
  );
}

export async function generateStaticParams() {
  const modules = getAllModules();
  return modules.map((module) => ({
    module,
  }));
}
