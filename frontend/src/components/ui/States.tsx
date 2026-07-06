import type { ReactNode } from 'react';

export function LoadingState({ label = 'Загрузка…' }: { label?: string }) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-16 text-brand-900/60">
      <span className="h-8 w-8 animate-spin rounded-full border-2 border-brand-300 border-t-brand-600" />
      <p className="text-sm">{label}</p>
    </div>
  );
}

export function EmptyState({
  title,
  description,
  action,
}: {
  title: string;
  description?: string;
  action?: ReactNode;
}) {
  return (
    <div className="flex flex-col items-center justify-center gap-2 rounded-2xl border border-dashed border-surface-border bg-white/60 py-16 text-center">
      <p className="text-lg font-semibold text-brand-900">{title}</p>
      {description && <p className="max-w-md text-sm text-brand-900/60">{description}</p>}
      {action && <div className="mt-3">{action}</div>}
    </div>
  );
}

export function ErrorState({ message }: { message: string }) {
  return (
    <div className="rounded-2xl border border-red-100 bg-red-50 px-4 py-6 text-center text-sm text-red-700">
      {message}
    </div>
  );
}
