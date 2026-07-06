/** Formats an ISO date string as a Russian date. */
export function formatDate(iso: string | null | undefined): string {
  if (!iso) return '—';
  return new Date(iso).toLocaleDateString('ru-RU', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  });
}

/** Declension helper: pluralises a Russian noun for a count. */
export function plural(n: number, one: string, few: string, many: string): string {
  const mod10 = n % 10;
  const mod100 = n % 100;
  if (mod10 === 1 && mod100 !== 11) return one;
  if (mod10 >= 2 && mod10 <= 4 && (mod100 < 10 || mod100 >= 20)) return few;
  return many;
}

/** e.g. 5 вопросов, 1 вопрос, 2 вопроса */
export function questionLabel(n: number): string {
  return `${n} ${plural(n, 'вопрос', 'вопроса', 'вопросов')}`;
}
