import Link from 'next/link';
import { getAllModules, getUnitsForModule, getAppLabsForUnit } from '@/lib/content';
import { markdownToHtml } from '@/lib/markdown';
import fs from 'fs';
import path from 'path';
import matter from 'gray-matter';

export default async function LabPage({
  params,
}: {
  params: { module: string; unit: string; lab: string };
}) {
  const { module, unit, lab } = params;

  // Get lab content
  const contentDirectory = path.join(process.cwd(), '..', 'content');
  const labPath = path.join(
    contentDirectory,
    'modules',
    module,
    unit,
    'app_labs',
    lab,
    'README.md'
  );

  if (!fs.existsSync(labPath)) {
    return <div>Lab not found</div>;
  }

  const fileContents = fs.readFileSync(labPath, 'utf8');
  const { data: frontmatter, content } = matter(fileContents);
  const labHtml = await markdownToHtml(content);

  // Get tasks if available
  const tasksPath = path.join(
    contentDirectory,
    'modules',
    module,
    unit,
    'app_labs',
    lab,
    'tasks.md'
  );

  let tasksHtml = null;
  if (fs.existsSync(tasksPath)) {
    const tasksContent = fs.readFileSync(tasksPath, 'utf8');
    const { content: tasksMarkdown } = matter(tasksContent);
    tasksHtml = await markdownToHtml(tasksMarkdown);
  }

  return (
    <div className="max-w-5xl mx-auto">
      {/* Breadcrumb */}
      <nav className="mb-6 text-sm text-gray-600 dark:text-gray-400">
        <Link href="/" className="hover:text-primary-blue">
          Home
        </Link>
        {' / '}
        <Link href="/modules" className="hover:text-primary-blue">
          Modules
        </Link>
        {' / '}
        <Link href={`/modules/${module}`} className="hover:text-primary-blue capitalize">
          {module.replace(/-/g, ' ')}
        </Link>
        {' / '}
        <Link href={`/modules/${module}/${unit}`} className="hover:text-primary-blue capitalize">
          {unit.replace(/_/g, ' ')}
        </Link>
        {' / '}
        <span>{frontmatter.title || lab}</span>
      </nav>

      {/* Lab Header */}
      <header className="mb-8 p-6 bg-gradient-to-r from-blue-50 to-purple-50 dark:from-blue-900/20 dark:to-purple-900/20 rounded-lg">
        <div className="flex items-start justify-between mb-4">
          <h1 className="text-4xl font-bold text-primary-blue">
            {frontmatter.title || lab}
          </h1>
          <span
            className={`px-4 py-2 rounded-full text-sm font-semibold ${
              frontmatter.difficulty === 'easy'
                ? 'bg-green-100 text-green-800'
                : frontmatter.difficulty === 'intermediate'
                ? 'bg-yellow-100 text-yellow-800'
                : frontmatter.difficulty === 'advanced'
                ? 'bg-orange-100 text-orange-800'
                : 'bg-red-100 text-red-800'
            }`}
          >
            {frontmatter.difficulty}
          </span>
        </div>

        {/* Lab Metadata */}
        <div className="flex flex-wrap gap-4 text-sm">
          {frontmatter.duration_hours && (
            <div className="flex items-center gap-2">
              <span className="font-semibold">⏱️ Duration:</span>
              <span>{frontmatter.duration_hours} hours</span>
            </div>
          )}
          {frontmatter.domain && (
            <div className="flex items-center gap-2">
              <span className="font-semibold">🏥 Domain:</span>
              <span className="capitalize">{frontmatter.domain}</span>
            </div>
          )}
        </div>

        {/* Tags */}
        {frontmatter.tags &&
          typeof frontmatter.tags === 'object' &&
          'subtopics' in frontmatter.tags && (
            <div className="mt-4">
              <p className="text-sm font-semibold mb-2">Skills Covered:</p>
              <div className="flex flex-wrap gap-2">
                {(frontmatter.tags.subtopics as string[]).map((tag) => (
                  <span
                    key={tag}
                    className="px-3 py-1 bg-white dark:bg-gray-800 rounded-full text-xs border"
                  >
                    {tag}
                  </span>
                ))}
              </div>
            </div>
          )}
      </header>

      {/* Tab Navigation */}
      <div className="border-b mb-8">
        <nav className="flex gap-8">
          <a
            href="#overview"
            className="pb-4 border-b-2 border-primary-blue font-semibold text-primary-blue"
          >
            Overview
          </a>
          {tasksHtml && (
            <a href="#tasks" className="pb-4 hover:text-primary-blue">
              Tasks
            </a>
          )}
        </nav>
      </div>

      {/* Lab Overview */}
      <section id="overview" className="mb-16">
        <div
          className="markdown-content prose dark:prose-invert max-w-none"
          dangerouslySetInnerHTML={{ __html: labHtml }}
        />
      </section>

      {/* Tasks Section */}
      {tasksHtml && (
        <section id="tasks" className="mb-16">
          <h2 className="text-3xl font-bold mb-6">📋 Tasks</h2>
          <div
            className="markdown-content prose dark:prose-invert max-w-none"
            dangerouslySetInnerHTML={{ __html: tasksHtml }}
          />
        </section>
      )}

      {/* Action Buttons */}
      <section className="border-t pt-8 flex gap-4">
        <Link
          href={`/modules/${module}/${unit}`}
          className="px-6 py-3 border rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800"
        >
          ← Back to Unit
        </Link>
        <button className="px-6 py-3 bg-primary-blue text-white rounded-lg hover:bg-primary-blue-dark">
          Download Starter Code
        </button>
      </section>
    </div>
  );
}

export async function generateStaticParams() {
  const modules = getAllModules();
  const paths: { module: string; unit: string; lab: string }[] = [];

  for (const module of modules) {
    const units = getUnitsForModule(module);
    for (const unit of units) {
      const labs = getAppLabsForUnit(module, unit);
      for (const lab of labs) {
        paths.push({ module, unit, lab: lab.slug });
      }
    }
  }

  return paths;
}
