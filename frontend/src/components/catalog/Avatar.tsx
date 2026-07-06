import { cn } from '../../utils/cn';

interface AvatarProps {
  name: string;
  url: string | null;
  size?: 'sm' | 'md' | 'lg';
}

const sizes = {
  sm: 'h-10 w-10 text-sm',
  md: 'h-14 w-14 text-base',
  lg: 'h-28 w-28 text-2xl',
};

export function Avatar({ name, url, size = 'md' }: AvatarProps) {
  if (url) {
    return (
      <img
        src={url}
        alt={name}
        className={cn('rounded-full object-cover', sizes[size])}
      />
    );
  }
  const initials =
    name
      .split(' ')
      .slice(0, 2)
      .map((s) => s[0]?.toUpperCase())
      .join('') || '?';
  return (
    <span
      className={cn(
        'flex items-center justify-center rounded-full bg-brand-100 font-semibold text-brand-700',
        sizes[size],
      )}
    >
      {initials}
    </span>
  );
}
