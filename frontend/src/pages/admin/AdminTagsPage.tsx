import { useState, type FormEvent } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { adminApi, type TagPayload } from '../../api/adminApi';
import { apiErrorMessage } from '../../api/httpClient';
import { useToast } from '../../components/ui/Toast';
import { Button } from '../../components/ui/Button';
import { Modal } from '../../components/ui/Modal';
import { Input, Label, FieldError } from '../../components/ui/Field';
import { Badge } from '../../components/ui/Badge';
import { LoadingState, EmptyState } from '../../components/ui/States';
import type { Tag } from '../../types';

export function AdminTagsPage() {
  const qc = useQueryClient();
  const { notify } = useToast();
  const [editing, setEditing] = useState<Tag | 'new' | null>(null);

  const { data, isLoading } = useQuery({ queryKey: ['admin', 'tags'], queryFn: adminApi.listTags });
  const invalidate = () => {
    qc.invalidateQueries({ queryKey: ['admin', 'tags'] });
    qc.invalidateQueries({ queryKey: ['tags'] });
  };

  const remove = useMutation({
    mutationFn: (id: number) => adminApi.deleteTag(id),
    onSuccess: () => {
      notify('Тег удалён', 'success');
      invalidate();
    },
    onError: (e) => notify(apiErrorMessage(e), 'error'),
  });

  if (isLoading) return <LoadingState />;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-brand-950">Теги</h1>
        <Button onClick={() => setEditing('new')}>Добавить тег</Button>
      </div>

      {!data || data.length === 0 ? (
        <EmptyState title="Тегов пока нет" />
      ) : (
        <div className="overflow-x-auto rounded-2xl bg-white shadow-card">
          <table className="w-full min-w-[560px] text-sm">
            <thead className="border-b border-surface-border text-left text-brand-900/50">
              <tr>
                <th className="px-4 py-3 font-medium">Название</th>
                <th className="px-4 py-3 font-medium">Slug</th>
                <th className="px-4 py-3 font-medium">Категория</th>
                <th className="px-4 py-3 font-medium">Действия</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-surface-border">
              {data.map((t) => (
                <tr key={t.id}>
                  <td className="px-4 py-3 font-medium text-brand-900">{t.name}</td>
                  <td className="px-4 py-3 text-brand-900/50">{t.slug}</td>
                  <td className="px-4 py-3">{t.category ? <Badge>{t.category}</Badge> : '—'}</td>
                  <td className="px-4 py-3">
                    <div className="flex gap-2">
                      <Button size="sm" variant="secondary" onClick={() => setEditing(t)}>
                        Изменить
                      </Button>
                      <Button
                        size="sm"
                        variant="danger"
                        disabled={remove.isPending}
                        onClick={() => remove.mutate(t.id)}
                      >
                        Удалить
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {editing && (
        <TagModal
          tag={editing === 'new' ? null : editing}
          onClose={() => setEditing(null)}
          onDone={() => {
            invalidate();
            setEditing(null);
          }}
        />
      )}
    </div>
  );
}

function TagModal({ tag, onClose, onDone }: { tag: Tag | null; onClose: () => void; onDone: () => void }) {
  const { notify } = useToast();
  const [name, setName] = useState(tag?.name ?? '');
  const [slug, setSlug] = useState(tag?.slug ?? '');
  const [category, setCategory] = useState(tag?.category ?? '');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    const payload: TagPayload = { name: name.trim(), slug: slug.trim(), category: category.trim() || undefined };
    try {
      if (tag) await adminApi.updateTag(tag.id, payload);
      else await adminApi.createTag(payload);
      notify(tag ? 'Тег обновлён' : 'Тег создан', 'success');
      onDone();
    } catch (err) {
      setError(apiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal open title={tag ? 'Изменить тег' : 'Новый тег'} onClose={onClose}>
      <form onSubmit={submit} className="space-y-4">
        <div>
          <Label htmlFor="t-name">Название</Label>
          <Input id="t-name" required value={name} onChange={(e) => setName(e.target.value)} />
        </div>
        <div>
          <Label htmlFor="t-slug">Slug (латиница, цифры, дефис)</Label>
          <Input
            id="t-slug"
            required
            value={slug}
            onChange={(e) => setSlug(e.target.value)}
            placeholder="backend"
          />
        </div>
        <div>
          <Label htmlFor="t-cat">Категория</Label>
          <Input id="t-cat" value={category} onChange={(e) => setCategory(e.target.value)} />
        </div>
        <FieldError>{error}</FieldError>
        <div className="flex justify-end gap-2">
          <Button type="button" variant="ghost" onClick={onClose}>
            Отмена
          </Button>
          <Button type="submit" loading={loading}>
            Сохранить
          </Button>
        </div>
      </form>
    </Modal>
  );
}
