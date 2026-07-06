import { useCallback, useState } from 'react';

export type ViewMode = 'board' | 'list';

const STORAGE_KEY = 'cmc.viewMode';

/** Catalog view mode persisted in localStorage (default: board). */
export function useViewMode(): [ViewMode, (mode: ViewMode) => void] {
  const [mode, setModeState] = useState<ViewMode>(() => {
    const saved = localStorage.getItem(STORAGE_KEY);
    return saved === 'list' || saved === 'board' ? saved : 'board';
  });

  const setMode = useCallback((next: ViewMode) => {
    setModeState(next);
    localStorage.setItem(STORAGE_KEY, next);
  }, []);

  return [mode, setMode];
}
