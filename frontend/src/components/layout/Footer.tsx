export function Footer() {
  return (
    <footer className="mt-16 border-t border-surface-border bg-white">
      <div className="mx-auto flex max-w-6xl flex-col items-center justify-between gap-2 px-4 py-6 text-sm text-brand-900/60 sm:flex-row sm:px-6">
        <p>CMC Alumni Hub — платформа выпускников ВМК МГУ</p>
        <p>© {new Date().getFullYear()} · Учебный проект</p>
      </div>
    </footer>
  );
}
