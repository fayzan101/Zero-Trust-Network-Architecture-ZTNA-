import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('accessToken');
  if (token && !req.url.includes('/api/auth/login') && !req.url.includes('/api/auth/register')
      && !req.url.includes('/api/auth/mfa') && !req.url.includes('/api/auth/step-up')
      && !req.url.includes('/api/auth/refresh')) {
    req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }
  return next(req);
};
