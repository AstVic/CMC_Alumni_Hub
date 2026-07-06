import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useQuery, keepPreviousData } from '@tanstack/react-query';
import { publicApi } from '../../api/publicApi';
import { useViewMode } from '../../hooks/useViewMode';
import { useDebounce } from '../../hooks/useDebounce';
import { ProfileCard } from '../../components/catalog/ProfileCard';
import { ProfileListItem } from '../../components/catalog/ProfileListItem';
import { ViewModeToggle } from '../../components/catalog/ViewModeToggle';
import { FilterPanel, type CatalogFilters } from '../../components/catalog/FilterPanel';
import { Input, Select } from '../../components/ui/Field';
import { Button } from '../../components/ui/Button';
import { Badge } from '../../components/ui/Badge';
import { EmptyState, ErrorState, LoadingState } from '../../components/ui/States';
import { SORT_OPTIONS, DEFAULT_SORT } from '../../utils/sortOptions';
import { plural } from '../../utils/format';

const PAGE_SIZE = 12;

export function CatalogPage() {
  const [params, setParams] = useSearchParams();
  const [viewMode, setViewMode] = useViewMode();

  const filters: CatalogFilters = useMemo(
    () => ({
      search: params.get('search') ?? '',
      tags: params.get('tags')?.split(',').filter(Boolean) ?? [],
      graduationYear: params.get('graduationYear') ?? '',
      company: params.get('company') ?? '',
    }),
    [params],
  );
  const sort = params.get('sort') ?? DEFAULT_SORT;
  const page = Number(params.get('page') ?? '0');

  const [searchInput, setSearchInput] = useState(filters.search);
  const debouncedSearch = useDebounce(searchInput, 400);

  // Push debounced search into the URL (resets page).
  useEffect(() => {
    if (debouncedSearch === filters.search) return;
    patchParams({ search: debouncedSearch || undefined, page: undefined });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedSearch]);

  function patchParams(patch: Record<string, string | undefined>) {
    const next = new URLSearchParams(params);
    Object.entries(patch).forEach(([k, v]) => {
      if (v === undefined || v === '') next.delete(k);
      else next.set(k, v);
    });
    setParams(next, { replace: true });
  }

  const onFilterChange = (patch: Partial<CatalogFilters>) => {
    const mapped: Record<string, string | undefined> = { page: undefined };
    if ('tags' in patch) mapped.tags = patch.tags?.length ? patch.tags.join(',') : undefined;
    if ('graduationYear' in patch) mapped.graduationYear = patch.graduationYear || undefined;
    if ('company' in patch) mapped.company = patch.company || undefined;
    patchParams(mapped);
  };

  const resetFilters = () => {
    setSearchInput('');
    setParams(new URLSearchParams(), { replace: true });
  };

  const { data: tags } = useQuery({ queryKey: ['tags'], queryFn: publicApi.listTags });

  const { data, isLoading, isError } = useQuery({
    queryKey: ['catalog', filters, sort, page],
    queryFn: () =>
      publicApi.listProfiles({
        search: filters.search || undefined,
        tags: filters.tags.length ? filters.tags : undefined,
        graduationYear: filters.graduationYear ? Number(filters.graduationYear) : undefined,
        company: filters.company || undefined,
        sort,
        page,
        size: PAGE_SIZE,
      }),
    placeholderData: keepPreviousData,
  });

  const hasActiveFilters =
    filters.search || filters.tags.length > 0 || filters.graduationYear || filters.company;

  const tagName = (slug: string) => tags?.find((t) => t.slug === slug)?.name ?? slug;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-brand-950">Выпускники</h1>
        <p className="mt-1 text-brand-900/60">
          {data
            ? `Найдено: ${data.totalElements} ${plural(data.totalElements, 'выпускник', 'выпускника', 'выпускников')}`
            : 'Загрузка каталога…'}
        </p>
      </div>

      {/* Controls row */}
      <div className="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">
        <div className="flex-1">
          <Input
            placeholder="Поиск по имени, компании, должности…"
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
          />
        </div>
        <div className="flex items-center gap-2">
          <Select value={sort} onChange={(e) => patchParams({ sort: e.target.value, page: undefined })}>
            {SORT_OPTIONS.map((o) => (
              <option key={o.value} value={o.value}>
                {o.label}
              </option>
            ))}
          </Select>
          <ViewModeToggle mode={viewMode} onChange={setViewMode} />
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-[260px_1fr]">
        <aside className="lg:sticky lg:top-20 lg:self-start">
          <FilterPanel tags={tags ?? []} filters={filters} onChange={onFilterChange} />
        </aside>

        <div className="space-y-4">
          {/* Active filters */}
          {hasActiveFilters && (
            <div className="flex flex-wrap items-center gap-2">
              {filters.search && <FilterChip label={`Поиск: ${filters.search}`} onRemove={() => { setSearchInput(''); patchParams({ search: undefined, page: undefined }); }} />}
              {filters.graduationYear && (
                <FilterChip label={`Год: ${filters.graduationYear}`} onRemove={() => patchParams({ graduationYear: undefined, page: undefined })} />
              )}
              {filters.company && (
                <FilterChip label={`Компания: ${filters.company}`} onRemove={() => patchParams({ company: undefined, page: undefined })} />
              )}
              {filters.tags.map((slug) => (
                <FilterChip key={slug} label={tagName(slug)} onRemove={() => onFilterChange({ tags: filters.tags.filter((s) => s !== slug) })} />
              ))}
              <Button variant="ghost" size="sm" onClick={resetFilters}>
                Сбросить всё
              </Button>
            </div>
          )}

          {isError ? (
            <ErrorState message="Не удалось загрузить каталог" />
          ) : isLoading && !data ? (
            <LoadingState />
          ) : data && data.content.length === 0 ? (
            <EmptyState
              title="Ничего не найдено"
              description="Попробуйте изменить фильтры или сбросить их."
              action={<Button variant="secondary" onClick={resetFilters}>Сбросить фильтры</Button>}
            />
          ) : (
            <>
              {viewMode === 'board' ? (
                <div className="grid gap-5 sm:grid-cols-2 xl:grid-cols-3">
                  {data?.content.map((p) => <ProfileCard key={p.id} profile={p} />)}
                </div>
              ) : (
                <div className="space-y-3">
                  {data?.content.map((p) => <ProfileListItem key={p.id} profile={p} />)}
                </div>
              )}

              {data && data.totalPages > 1 && (
                <div className="flex items-center justify-center gap-3 pt-4">
                  <Button
                    variant="secondary"
                    size="sm"
                    disabled={page === 0}
                    onClick={() => patchParams({ page: String(page - 1) })}
                  >
                    ← Назад
                  </Button>
                  <span className="text-sm text-brand-900/60">
                    Страница {page + 1} из {data.totalPages}
                  </span>
                  <Button
                    variant="secondary"
                    size="sm"
                    disabled={!data.hasNext}
                    onClick={() => patchParams({ page: String(page + 1) })}
                  >
                    Вперёд →
                  </Button>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}

function FilterChip({ label, onRemove }: { label: string; onRemove: () => void }) {
  return (
    <button onClick={onRemove} className="inline-flex items-center gap-1">
      <Badge tone="blue">
        {label} <span className="ml-1 text-brand-500">✕</span>
      </Badge>
    </button>
  );
}

