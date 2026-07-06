import type { ViewMode } from '../../hooks/useViewMode';
import { cn } from '../../utils/cn';

export function ViewModeToggle({
  mode,
  onChange,
}: {
  mode: ViewMode;
  onChange: (mode: ViewMode) => void;
}) {
  return (
    <div className="inline-flex rounded-lg border border-surface-border bg-white p-0.5">
      {(['board', 'list'] as ViewMode[]).map((m) => (
        <button
          key={m}
          onClick={() => onChange(m)}
          className={cn(
            'rounded-md px-3 py-1.5 text-sm font-medium transition-colors',
            mode === m ? 'bg-brand-600 text-white' : 'text-brand-900/60 hover:text-brand-700',
          )}
        >
          {m === 'board' ? '▦ Доска' : '☰ Список'}
        </button>
      ))}
    </div>
  );
}
