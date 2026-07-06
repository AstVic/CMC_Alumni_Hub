import { API_BASE_URL } from './config';

/**
 * Skeleton landing placeholder.
 *
 * Real routing, pages and components are introduced in the frontend stages.
 * This screen only confirms that the toolchain (React + Tailwind + Vite) and
 * the theme palette render correctly.
 */
export default function App() {
  return (
    <div className="min-h-full">
      <header className="border-b border-surface-border bg-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
          <div className="flex items-center gap-2">
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-brand-600 font-bold text-white">
              A
            </div>
            <span className="text-lg font-semibold text-brand-900">
              CMC Alumni Hub
            </span>
          </div>
          <span className="rounded-full bg-brand-50 px-3 py-1 text-sm font-medium text-brand-700">
            MVP · каркас
          </span>
        </div>
      </header>

      <main className="mx-auto max-w-6xl px-6 py-16">
        <div className="rounded-2xl bg-white p-10 shadow-card">
          <p className="mb-3 inline-block rounded-full bg-accent-500/10 px-3 py-1 text-sm font-medium text-accent-600">
            Проект инициализирован
          </p>
          <h1 className="max-w-2xl text-4xl font-bold leading-tight text-brand-950">
            Платформа для взаимодействия студентов ВМК МГУ с выпускниками
          </h1>
          <p className="mt-4 max-w-2xl text-lg text-brand-900/70">
            Каркас фронтенда готов. Далее по плану — раскладка, роутинг, страницы
            каталога, личного кабинета выпускника и админ-панели.
          </p>
          <p className="mt-8 text-sm text-brand-900/50">
            API base URL:{' '}
            <code className="rounded bg-surface-muted px-2 py-1 text-brand-700">
              {API_BASE_URL}
            </code>
          </p>
        </div>
      </main>
    </div>
  );
}
