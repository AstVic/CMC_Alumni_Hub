import { Link } from 'react-router-dom';
import type { ProfileCard as ProfileCardType } from '../../types';
import { Avatar } from './Avatar';
import { TagBadge } from '../ui/Badge';
import { Button } from '../ui/Button';
import { questionLabel } from '../../utils/format';

/** Board-mode card with photo. */
export function ProfileCard({ profile }: { profile: ProfileCardType }) {
  return (
    <div className="flex flex-col rounded-2xl bg-white p-5 shadow-card transition-transform hover:-translate-y-0.5">
      <div className="flex items-start gap-4">
        <Avatar name={profile.fullName} url={profile.photoUrl} size="md" />
        <div className="min-w-0 flex-1">
          <h3 className="truncate font-semibold text-brand-950">{profile.fullName}</h3>
          <p className="truncate text-sm text-brand-900/70">
            {profile.currentPosition}
            {profile.company ? ` · ${profile.company}` : ''}
          </p>
          <p className="text-xs text-brand-900/50">
            {profile.graduationYear ? `Выпуск ${profile.graduationYear}` : ''}
            {profile.city ? ` · ${profile.city}` : ''}
          </p>
        </div>
      </div>

      {profile.shortDescription && (
        <p className="mt-3 line-clamp-3 text-sm text-brand-900/60">{profile.shortDescription}</p>
      )}

      {profile.tags.length > 0 && (
        <div className="mt-3 flex flex-wrap gap-1.5">
          {profile.tags.slice(0, 4).map((t) => (
            <TagBadge key={t.id} name={t.name} />
          ))}
        </div>
      )}

      <div className="mt-4 flex items-center justify-between border-t border-surface-border pt-4">
        <span className="text-sm font-medium text-accent-600">
          {questionLabel(profile.questionCount)}
        </span>
        <Link to={`/alumni-profile/${profile.id}`}>
          <Button size="sm" variant="secondary">
            Подробнее
          </Button>
        </Link>
      </div>
    </div>
  );
}
