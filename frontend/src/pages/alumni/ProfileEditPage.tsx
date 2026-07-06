import { useEffect, useState, type FormEvent } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { alumniApi } from '../../api/alumniApi';
import { publicApi } from '../../api/publicApi';
import { apiErrorMessage } from '../../api/httpClient';
import { useToast } from '../../components/ui/Toast';
import { Avatar } from '../../components/catalog/Avatar';
import { Button } from '../../components/ui/Button';
import { Input, Textarea, Label } from '../../components/ui/Field';
import { ProfileStatusBadge } from '../../components/ui/Badge';
import { LoadingState, ErrorState } from '../../components/ui/States';
import { cn } from '../../utils/cn';
import type { UpdateProfilePayload } from '../../types';

interface FormState {
  fullName: string;
  graduationYear: string;
  department: string;
  currentPosition: string;
  company: string;
  city: string;
  country: string;
  careerDescription: string;
  interestsDescription: string;
  tagSlugs: string[];
}

const EMPTY: FormState = {
  fullName: '',
  graduationYear: '',
  department: '',
  currentPosition: '',
  company: '',
  city: '',
  country: '',
  careerDescription: '',
  interestsDescription: '',
  tagSlugs: [],
};

export function ProfileEditPage() {
  const qc = useQueryClient();
  const { notify } = useToast();
  const [form, setForm] = useState<FormState>(EMPTY);
  const [saving, setSaving] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [uploading, setUploading] = useState(false);

  const { data: profile, isLoading, isError } = useQuery({
    queryKey: ['alumni', 'profile'],
    queryFn: alumniApi.getProfile,
  });
  const { data: tags } = useQuery({ queryKey: ['tags'], queryFn: publicApi.listTags });

  useEffect(() => {
    if (profile) {
      setForm({
        fullName: profile.fullName ?? '',
        graduationYear: profile.graduationYear?.toString() ?? '',
        department: profile.department ?? '',
        currentPosition: profile.currentPosition ?? '',
        company: profile.company ?? '',
        city: profile.city ?? '',
        country: profile.country ?? '',
        careerDescription: profile.careerDescription ?? '',
        interestsDescription: profile.interestsDescription ?? '',
        tagSlugs: profile.tags.map((t) => t.slug),
      });
    }
  }, [profile]);

  if (isLoading) return <LoadingState />;
  if (isError || !profile) return <ErrorState message="Не удалось загрузить карточку" />;

  const set = (patch: Partial<FormState>) => setForm((f) => ({ ...f, ...patch }));
  const toggleTag = (slug: string) =>
    set({
      tagSlugs: form.tagSlugs.includes(slug)
        ? form.tagSlugs.filter((s) => s !== slug)
        : [...form.tagSlugs, slug],
    });

  const payload = (): UpdateProfilePayload => ({
    fullName: form.fullName.trim(),
    graduationYear: form.graduationYear ? Number(form.graduationYear) : null,
    department: form.department || null,
    currentPosition: form.currentPosition || null,
    company: form.company || null,
    city: form.city || null,
    country: form.country || null,
    careerDescription: form.careerDescription || null,
    interestsDescription: form.interestsDescription || null,
    tagSlugs: form.tagSlugs,
  });

  const save = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await alumniApi.updateProfile(payload());
      await qc.invalidateQueries({ queryKey: ['alumni', 'profile'] });
      notify('Карточка сохранена как черновик', 'success');
    } catch (err) {
      notify(apiErrorMessage(err), 'error');
    } finally {
      setSaving(false);
    }
  };

  const submitForModeration = async () => {
    setSubmitting(true);
    try {
      await alumniApi.updateProfile(payload());
      await alumniApi.submitForModeration();
      await qc.invalidateQueries({ queryKey: ['alumni', 'profile'] });
      notify('Карточка отправлена на модерацию', 'success');
    } catch (err) {
      notify(apiErrorMessage(err), 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const onPhoto = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setUploading(true);
    try {
      await alumniApi.uploadPhoto(file);
      await qc.invalidateQueries({ queryKey: ['alumni', 'profile'] });
      notify('Фото загружено', 'success');
    } catch (err) {
      notify(apiErrorMessage(err), 'error');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-2xl font-bold text-brand-950">Моя карточка</h1>
        <ProfileStatusBadge status={profile.status} />
      </div>

      {/* Photo */}
      <div className="flex items-center gap-4 rounded-2xl bg-white p-5 shadow-card">
        <Avatar name={form.fullName || '—'} url={profile.photoUrl} size="lg" />
        <div>
          <p className="font-medium text-brand-900">Фотография</p>
          <p className="mb-2 text-sm text-brand-900/50">JPEG, PNG или WebP, до 5 МБ.</p>
          <label
            className={cn(
              'inline-flex cursor-pointer items-center gap-2 rounded-lg border border-surface-border',
              'bg-white px-3 py-1.5 text-sm font-medium text-brand-700 hover:bg-surface-muted',
              uploading && 'pointer-events-none opacity-60',
            )}
          >
            <input type="file" accept="image/*" className="hidden" onChange={onPhoto} disabled={uploading} />
            {uploading ? 'Загрузка…' : 'Загрузить фото'}
          </label>
        </div>
      </div>

      <form onSubmit={save} className="space-y-5 rounded-2xl bg-white p-6 shadow-card">
        <div className="grid gap-4 sm:grid-cols-2">
          <Row label="ФИО *">
            <Input required value={form.fullName} onChange={(e) => set({ fullName: e.target.value })} />
          </Row>
          <Row label="Год выпуска">
            <Input
              type="number"
              value={form.graduationYear}
              onChange={(e) => set({ graduationYear: e.target.value })}
            />
          </Row>
          <Row label="Кафедра / направление">
            <Input value={form.department} onChange={(e) => set({ department: e.target.value })} />
          </Row>
          <Row label="Должность">
            <Input
              value={form.currentPosition}
              onChange={(e) => set({ currentPosition: e.target.value })}
            />
          </Row>
          <Row label="Компания">
            <Input value={form.company} onChange={(e) => set({ company: e.target.value })} />
          </Row>
          <Row label="Город">
            <Input value={form.city} onChange={(e) => set({ city: e.target.value })} />
          </Row>
          <Row label="Страна">
            <Input value={form.country} onChange={(e) => set({ country: e.target.value })} />
          </Row>
        </div>

        <Row label="Карьерный путь">
          <Textarea
            value={form.careerDescription}
            onChange={(e) => set({ careerDescription: e.target.value })}
            placeholder="Расскажите о своём профессиональном пути…"
          />
        </Row>
        <Row label="Профессиональные интересы">
          <Textarea
            value={form.interestsDescription}
            onChange={(e) => set({ interestsDescription: e.target.value })}
          />
        </Row>

        <div>
          <Label>Теги</Label>
          <div className="flex flex-wrap gap-1.5">
            {tags?.map((t) => {
              const active = form.tagSlugs.includes(t.slug);
              return (
                <button
                  key={t.id}
                  type="button"
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

        <div className="flex flex-wrap gap-2 border-t border-surface-border pt-4">
          <Button type="submit" variant="secondary" loading={saving}>
            Сохранить черновик
          </Button>
          <Button type="button" loading={submitting} onClick={submitForModeration}>
            Отправить на модерацию
          </Button>
        </div>
      </form>
    </div>
  );
}

function Row({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div>
      <Label>{label}</Label>
      {children}
    </div>
  );
}
