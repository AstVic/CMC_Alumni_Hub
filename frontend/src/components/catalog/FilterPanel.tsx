import type { Tag } from '../../types';
import { cn } from '../../utils/cn';
import { Input } from '../ui/Field';

export interface CatalogFilters {
  search: string;
  tags: string[];
  graduationYear: string;
  company: string;
}

interface FilterPanelProps {
  tags: Tag[];
  filters: CatalogFilters;
  onChange: (patch: Partial<CatalogFilters>) => void;
}

export function FilterPanel({ tags, filters, onChange }: FilterPanelProps) {
  const toggleTag = (slug: string) => {
    const next = filters.tags.includes(slug)
      ? filters.tags.filter((s) => s !== slug)
      : [...filters.tags, slug];
    onChange({ tags: next });
  };

  return (
    <div className="space-y-5 rounded-2xl bg-white p-5 shadow-card">
      <div>
        <p className="mb-2 text-sm font-semibold text-brand-900">Год выпуска</p>
        <Input
          type="number"
          placeholder="Например, 2018"
          value={filters.graduationYear}
          onChange={(e) => onChange({ graduationYear: e.target.value })}
        />
      </div>

      <div>
        <p className="mb-2 text-sm font-semibold text-brand-900">Компания</p>
        <Input
          placeholder="Например, Yandex"
          value={filters.company}
          onChange={(e) => onChange({ company: e.target.value })}
        />
      </div>

      <div>
        <p className="mb-2 text-sm font-semibold text-brand-900">Теги</p>
        <div className="flex flex-wrap gap-1.5">
          {tags.map((t) => {
            const active = filters.tags.includes(t.slug);
            return (
              <button
                key={t.id}
                onClick={() => toggleTag(t.slug)}
                className={cn(
                  'rounded-full px-2.5 py-1 text-xs font-medium transition-colors',
                  active
                    ? 'bg-brand-600 text-white'
                    : 'bg-surface-muted text-brand-900/70 hover:bg-brand-50',
                )}
              >
                {t.name}
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
}
