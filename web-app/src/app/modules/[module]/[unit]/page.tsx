import Link from 'next/link';
import {
  getAllModules,
  getUnitsForModule,
  getKnowledgeContent,
  getExercisesContent,
  getAppLabsForUnit,
  getLearningOutcomes,
} from '@/lib/content';
import { markdownToHtml } from '@/lib/markdown';
import { Tabs } from '@/components/Tabs';

export default async function UnitPage({
  params,
}: {
  params: { module: string; unit: string };
}) {
  const { module, unit } = params;

  // Fetch all content for this unit
  const knowledge = getKnowledgeContent(module, unit);
  const exercises = getExercisesContent(module, unit);
  const labs = getAppLabsForUnit(module, unit);
  const outcomes = getLearningOutcomes(module, unit);

  // Convert markdown to HTML
  const knowledgeHtml = knowledge ? await markdownToHtml(knowledge.content) : null;
  const exercisesHtml = exercises ? await markdownToHtml(exercises.content) : null;
  const outcomesHtml = outcomes ? await markdownToHtml(outcomes.content) : null;

  const tabs = [
    {
      id: 'knowledge',
      label: 'Knowledge',
      icon: '📚',
      content: knowledgeHtml ? (
        <div
          className="markdown-content"
          dangerouslySetInnerHTML={{ __html: knowledgeHtml }}
        />
      ) : (
        <div className="card p-12 text-center">
          <h3 className="text-lg font-bold mb-2">Knowledge Content Coming Soon</h3>
          <p className="text-[var(--text-secondary)]">
            Knowledge content for this unit is being developed.
          </p>
        </div>
      ),
    },
    {
      id: 'exercises',
      label: 'Exercises',
      icon: '💡',
      content: exercisesHtml ? (
        <div
          className="markdown-content"
          dangerouslySetInnerHTML={{ __html: exercisesHtml }}
        />
      ) : (
        <div className="card p-12 text-center">
          <h3 className="text-lg font-bold mb-2">Exercises Coming Soon</h3>
          <p className="text-[var(--text-secondary)]">
            Practice exercises for this unit are being developed.
          </p>
        </div>
      ),
    },
    {
      id: 'labs',
      label: 'App Labs',
      icon: '🚀',
      content:
        labs.length > 0 ? (
          <div className="grid gap-4">
            {labs.map((lab) => (
              <Link
                key={lab.slug}
                href={`/modules/${module}/${unit}/labs/${lab.slug}`}
                className="card card-hover p-6"
              >
                <div className="flex items-start justify-between gap-4">
                  <div className="flex-1">
                    <h3 className="text-lg font-bold mb-2 group-hover:text-[var(--text-secondary)] transition-colors">
                      {lab.frontmatter.title}
                    </h3>
                    <div className="flex items-center gap-3 text-xs text-[var(--text-tertiary)] mb-3">
                      {lab.frontmatter.duration_hours && (
                        <span className="flex items-center gap-1">
                          <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                          </svg>
                          {lab.frontmatter.duration_hours} hours
                        </span>
                      )}
                    </div>
                  </div>
                  <span
                    className={`badge ${
                      lab.frontmatter.difficulty === 'easy'
                        ? 'badge-green'
                        : lab.frontmatter.difficulty === 'intermediate'
                        ? 'badge-yellow'
                        : lab.frontmatter.difficulty === 'advanced'
                        ? 'badge-orange'
                        : 'badge-red'
                    }`}
                  >
                    {lab.frontmatter.difficulty}
                  </span>
                </div>
                {lab.frontmatter.tags &&
                  typeof lab.frontmatter.tags === 'object' &&
                  'subtopics' in lab.frontmatter.tags && (
                    <div className="flex flex-wrap gap-2 mt-4">
                      {(lab.frontmatter.tags.subtopics as string[]).map((tag) => (
                        <span
                          key={tag}
                          className="px-2 py-1 bg-[var(--surface)] text-[var(--text-secondary)] rounded text-xs font-medium"
                        >
                          {tag}
                        </span>
                      ))}
                    </div>
                  )}
              </Link>
            ))}
          </div>
        ) : (
          <div className="card p-12 text-center">
            <h3 className="text-lg font-bold mb-2">App Labs Coming Soon</h3>
            <p className="text-[var(--text-secondary)]">
              Application labs for this unit are being developed.
            </p>
          </div>
        ),
    },
    {
      id: 'outcomes',
      label: 'Learning Outcomes',
      icon: '✅',
      content: outcomesHtml ? (
        <div
          className="markdown-content"
          dangerouslySetInnerHTML={{ __html: outcomesHtml }}
        />
      ) : (
        <div className="card p-12 text-center">
          <h3 className="text-lg font-bold mb-2">Learning Outcomes Coming Soon</h3>
          <p className="text-[var(--text-secondary)]">
            Learning outcomes for this unit are being developed.
          </p>
        </div>
      ),
    },
  ];

  return (
    <div className="bg-[var(--background)]">
      {/* Header Section */}
      <div className="py-16">
        <div className="container mx-auto px-6">
          <div className="max-w-5xl">
            {/* Breadcrumb */}
            <nav className="mb-6 text-xs text-[var(--text-tertiary)]">
              <Link href="/" className="hover:text-[var(--text-secondary)]">
                Home
              </Link>
              <span className="mx-2">/</span>
              <Link href="/modules" className="hover:text-[var(--text-secondary)]">
                Modules
              </Link>
              <span className="mx-2">/</span>
              <Link href={`/modules/${module}`} className="hover:text-[var(--text-secondary)] capitalize">
                {module.replace(/-/g, ' ')}
              </Link>
              <span className="mx-2">/</span>
              <span className="capitalize">{unit.replace(/_/g, ' ')}</span>
            </nav>

            {/* Title */}
            <h1 className="text-5xl font-bold mb-2 capitalize">
              {unit.replace(/_/g, ' ')}
            </h1>
            {knowledge?.frontmatter.title && (
              <p className="text-lg text-[var(--text-secondary)]">
                {knowledge.frontmatter.title}
              </p>
            )}
          </div>
        </div>
      </div>

      {/* Content Section */}
      <div className="container mx-auto px-6 pb-20">
        <div className="max-w-5xl mx-auto">
          <Tabs tabs={tabs} />
        </div>
      </div>
    </div>
  );
}

export async function generateStaticParams() {
  const modules = getAllModules();
  const paths: { module: string; unit: string }[] = [];

  for (const module of modules) {
    const units = getUnitsForModule(module);
    for (const unit of units) {
      paths.push({ module, unit });
    }
  }

  return paths;
}
