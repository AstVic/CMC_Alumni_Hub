import type { ReactNode } from 'react';
import { cn } from '../../utils/cn';
import type { InviteStatus, ProfileStatus, QuestionStatus } from '../../types';

type Tone = 'gray' | 'blue' | 'green' | 'amber' | 'red' | 'violet';

const tones: Record<Tone, string> = {
  gray: 'bg-surface-muted text-brand-900/70',
  blue: 'bg-brand-50 text-brand-700',
  green: 'bg-emerald-50 text-emerald-700',
  amber: 'bg-amber-50 text-amber-700',
  red: 'bg-red-50 text-red-700',
  violet: 'bg-accent-500/10 text-accent-600',
};

export function Badge({ tone = 'gray', children }: { tone?: Tone; children: ReactNode }) {
  return (
    <span
      className={cn(
        'inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium',
        tones[tone],
      )}
    >
      {children}
    </span>
  );
}

export function TagBadge({ name }: { name: string }) {
  return (
    <span className="inline-flex items-center rounded-full bg-brand-50 px-2.5 py-0.5 text-xs font-medium text-brand-700">
      {name}
    </span>
  );
}

const profileStatusMap: Record<ProfileStatus, { tone: Tone; label: string }> = {
  DRAFT: { tone: 'gray', label: 'Черновик' },
  PENDING_MODERATION: { tone: 'amber', label: 'На модерации' },
  PUBLISHED: { tone: 'green', label: 'Опубликована' },
  REJECTED: { tone: 'red', label: 'Отклонена' },
};

export function ProfileStatusBadge({ status }: { status: ProfileStatus }) {
  const { tone, label } = profileStatusMap[status];
  return <Badge tone={tone}>{label}</Badge>;
}

const inviteStatusMap: Record<InviteStatus, { tone: Tone; label: string }> = {
  CREATED: { tone: 'blue', label: 'Создано' },
  SENT: { tone: 'violet', label: 'Отправлено' },
  USED: { tone: 'green', label: 'Использовано' },
  EXPIRED: { tone: 'gray', label: 'Истекло' },
  REVOKED: { tone: 'red', label: 'Отозвано' },
};

export function InviteStatusBadge({ status }: { status: InviteStatus }) {
  const { tone, label } = inviteStatusMap[status];
  return <Badge tone={tone}>{label}</Badge>;
}

const questionStatusMap: Record<QuestionStatus, { tone: Tone; label: string }> = {
  PENDING_MODERATION: { tone: 'gray', label: 'В обработке' },
  AI_APPROVED: { tone: 'blue', label: 'AI одобрил' },
  AI_REJECTED: { tone: 'red', label: 'AI отклонил' },
  PENDING_ADMIN_REVIEW: { tone: 'amber', label: 'Нужна проверка' },
  APPROVED_BY_ADMIN: { tone: 'green', label: 'Одобрен' },
  REJECTED_BY_ADMIN: { tone: 'red', label: 'Отклонён' },
  VISIBLE_TO_ALUMNI: { tone: 'green', label: 'Опубликован' },
  ARCHIVED: { tone: 'gray', label: 'В архиве' },
};

export function QuestionStatusBadge({ status }: { status: QuestionStatus }) {
  const { tone, label } = questionStatusMap[status];
  return <Badge tone={tone}>{label}</Badge>;
}
