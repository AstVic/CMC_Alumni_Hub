import { useState, type FormEvent } from 'react';
import { publicApi } from '../../api/publicApi';
import { apiErrorMessage } from '../../api/httpClient';
import { Button } from '../ui/Button';
import { Input, Textarea, Label, FieldError } from '../ui/Field';

export function QuestionForm({ profileId }: { profileId: number }) {
  const [senderName, setSenderName] = useState('');
  const [senderEmail, setSenderEmail] = useState('');
  const [questionText, setQuestionText] = useState('');
  const [acceptedRules, setAcceptedRules] = useState(false);
  const [website, setWebsite] = useState(''); // honeypot
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [done, setDone] = useState<string | null>(null);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    if (!acceptedRules) {
      setError('Необходимо принять правила сайта');
      return;
    }
    setLoading(true);
    try {
      const res = await publicApi.askQuestion(profileId, {
        senderName: senderName.trim() || undefined,
        senderEmail: senderEmail.trim() || undefined,
        questionText: questionText.trim(),
        acceptedRules,
        website,
      });
      setDone(res.message);
      setSenderName('');
      setSenderEmail('');
      setQuestionText('');
      setAcceptedRules(false);
    } catch (err) {
      setError(apiErrorMessage(err, 'Не удалось отправить вопрос'));
    } finally {
      setLoading(false);
    }
  };

  if (done) {
    return (
      <div className="rounded-2xl border border-emerald-100 bg-emerald-50 p-6 text-center">
        <p className="text-lg font-semibold text-emerald-800">Вопрос отправлен</p>
        <p className="mt-1 text-sm text-emerald-700">{done}</p>
        <Button variant="secondary" size="sm" className="mt-4" onClick={() => setDone(null)}>
          Задать ещё вопрос
        </Button>
      </div>
    );
  }

  return (
    <form onSubmit={onSubmit} className="space-y-4">
      <div className="grid gap-4 sm:grid-cols-2">
        <div>
          <Label htmlFor="q-name">Ваше имя (необязательно)</Label>
          <Input id="q-name" value={senderName} onChange={(e) => setSenderName(e.target.value)} />
        </div>
        <div>
          <Label htmlFor="q-email">Email (необязательно)</Label>
          <Input
            id="q-email"
            type="email"
            value={senderEmail}
            onChange={(e) => setSenderEmail(e.target.value)}
          />
        </div>
      </div>
      <div>
        <Label htmlFor="q-text">Вопрос</Label>
        <Textarea
          id="q-text"
          required
          minLength={5}
          maxLength={2000}
          value={questionText}
          onChange={(e) => setQuestionText(e.target.value)}
          placeholder="Например: как вы выбрали специализацию и с чего посоветуете начать?"
        />
      </div>

      {/* Honeypot: hidden from users, bots tend to fill it. */}
      <div className="absolute -left-[9999px]" aria-hidden="true">
        <label>
          Не заполняйте это поле
          <input
            tabIndex={-1}
            autoComplete="off"
            value={website}
            onChange={(e) => setWebsite(e.target.value)}
          />
        </label>
      </div>

      <label className="flex items-start gap-2 text-sm text-brand-900/70">
        <input
          type="checkbox"
          className="mt-0.5"
          checked={acceptedRules}
          onChange={(e) => setAcceptedRules(e.target.checked)}
        />
        <span>
          Я согласен с правилами сайта и понимаю, что вопрос проходит модерацию перед публикацией.
        </span>
      </label>

      <FieldError>{error}</FieldError>

      <Button type="submit" loading={loading}>
        Отправить вопрос
      </Button>
    </form>
  );
}
