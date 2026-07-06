import { NavLink, Outlet } from 'react-router-dom';
import { Navbar } from './Navbar';
import { cn } from '../../utils/cn';

export interface NavItem {
  to: string;
  label: string;
  end?: boolean;
}

/** Shell with a left sidebar for the alumni cabinet and admin panel. */
export function DashboardLayout({ title, items }: { title: string; items: NavItem[] }) {
  return (
    <div className="flex min-h-full flex-col">
      <Navbar />
      <div className="mx-auto flex w-full max-w-6xl flex-1 gap-6 px-4 py-8 sm:px-6">
        <aside className="hidden w-56 shrink-0 md:block">
          <p className="mb-3 px-3 text-xs font-semibold uppercase tracking-wide text-brand-900/40">
            {title}
          </p>
          <nav className="space-y-1">
            {items.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) =>
                  cn(
                    'block rounded-lg px-3 py-2 text-sm font-medium transition-colors',
                    isActive
                      ? 'bg-brand-600 text-white'
                      : 'text-brand-900/70 hover:bg-brand-50 hover:text-brand-700',
                  )
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
        </aside>

        <main className="min-w-0 flex-1">
          {/* Mobile sub-nav */}
          <nav className="mb-5 flex gap-2 overflow-x-auto md:hidden">
            {items.map((item) => (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) =>
                  cn(
                    'whitespace-nowrap rounded-lg px-3 py-1.5 text-sm font-medium',
                    isActive ? 'bg-brand-600 text-white' : 'bg-white text-brand-900/70',
                  )
                }
              >
                {item.label}
              </NavLink>
            ))}
          </nav>
          <Outlet />
        </main>
      </div>
    </div>
  );
}
