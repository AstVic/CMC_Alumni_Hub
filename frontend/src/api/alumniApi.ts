import { http } from './httpClient';
import type { AlumniProfile, AlumniQuestion, UpdateProfilePayload } from '../types';

export const alumniApi = {
  async getProfile(): Promise<AlumniProfile> {
    const { data } = await http.get<AlumniProfile>('/alumni/profile');
    return data;
  },
  async updateProfile(payload: UpdateProfilePayload): Promise<AlumniProfile> {
    const { data } = await http.put<AlumniProfile>('/alumni/profile', payload);
    return data;
  },
  async submitForModeration(): Promise<AlumniProfile> {
    const { data } = await http.post<AlumniProfile>('/alumni/profile/submit-for-moderation');
    return data;
  },
  async uploadPhoto(file: File): Promise<AlumniProfile> {
    const form = new FormData();
    form.append('file', file);
    const { data } = await http.post<AlumniProfile>('/alumni/profile/photo', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return data;
  },
  async listQuestions(filter: 'new' | 'read' | 'archived'): Promise<AlumniQuestion[]> {
    const { data } = await http.get<AlumniQuestion[]>('/alumni/questions', { params: { filter } });
    return data;
  },
  async markRead(id: number): Promise<AlumniQuestion> {
    const { data } = await http.patch<AlumniQuestion>(`/alumni/questions/${id}/read`);
    return data;
  },
  async answerQuestion(id: number, answerText: string): Promise<AlumniQuestion> {
    const { data } = await http.patch<AlumniQuestion>(`/alumni/questions/${id}/answer`, {
      answerText,
    });
    return data;
  },
};
