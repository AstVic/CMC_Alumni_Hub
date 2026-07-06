import { http } from './httpClient';
import type { TokenResponse, User } from '../types';

export const authApi = {
  async login(email: string, password: string): Promise<TokenResponse> {
    const { data } = await http.post<TokenResponse>('/auth/login', { email, password });
    return data;
  },
  async me(): Promise<User> {
    const { data } = await http.get<User>('/auth/me');
    return data;
  },
};
