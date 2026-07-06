import { Link } from 'react-router-dom';
import type { ProfileCard as ProfileCardType } from '../../types';
import { Avatar } from './Avatar';
import { TagBadge } from '../ui/Badge';
import { Button } from '../ui/Button';
import { questionLabel } from '../../utils/format';

/** List-mode compact row (minimal thumbnail, shortened description). */
export function ProfileListItem({ profile }: { profile: ProfileCardType }) {
  return (
    <div className="flex flex-col gap-3 rounded-xl bg-white p-4 shadow-card sm:flex-row sm:items-center">
      <div className="flex min-w-0 flex-1 items-center gap-3">
        <Avatar name={profile.fullName} url={profile.photoUrl} size="sm" />
        <div className="min-w-0">
          <div className="flex flex-wrap items-center gap-x-2">
            <h3 className="font-semibold text-brand-950">{profile.fullName}</h3>
            {profile.graduationYear && (
              <span className="text-xs text-brand-900/50">Выпуск {profile.graduationYear}</span>
            )}
          </div>
          <p className="truncate text-sm text-brand-900/70">
            {profile.currentPosition}
            {profile.company ? ` · ${profile.company}` : ''}
          </p>
          {profile.shortDescription && (
            <p className="mt-0.5 line-clamp-1 text-sm text-brand-900/50">
              {profile.shortDescription}
            </p>
          )}
          {profile.tags.length > 0 && (
            <div className="mt-1.5 flex flex-wrap gap-1.5">
              {profile.tags.slice(0, 5).map((t) => (
                <TagBadge key={t.id} name={t.name} />
              ))}
            </div>
          )}
        </div>
      </div>

      <div className="flex items-center gap-4 sm:flex-col sm:items-end">
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
