import type { ReactNode } from 'react';
import { Link } from 'react-router-dom';

/** Centered card shell for standalone auth pages (login, reset, etc.). */
export function AuthShell({ children }: { children: ReactNode }) {
  return (
    <div className="mx-auto flex min-h-screen max-w-md flex-col justify-center px-4 py-12">
      <Link to="/" className="mb-8 flex items-center justify-center gap-2">
        <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-brand-600 font-bold text-white">
          A
        </span>
        <span className="text-xl font-semibold text-brand-900">CMC Alumni Hub</span>
      </Link>
      <div className="rounded-2xl bg-white p-8 shadow-card">{children}</div>
    </div>
  );
}
