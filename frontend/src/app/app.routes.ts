import { Routes } from '@angular/router';
import { authGuard } from './core/auth.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent) },
  {
    path: '',
    loadComponent: () => import('./layout/shell.component').then(m => m.ShellComponent),
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'sessions', loadComponent: () => import('./pages/sessions/sessions.component').then(m => m.SessionsComponent) },
      { path: 'risk', loadComponent: () => import('./pages/risk/risk.component').then(m => m.RiskComponent) },
      { path: 'policies', loadComponent: () => import('./pages/policies/policies.component').then(m => m.PoliciesComponent) },
      { path: 'compare', loadComponent: () => import('./pages/compare/compare.component').then(m => m.CompareComponent) },
      { path: 'attacks', loadComponent: () => import('./pages/attacks/attacks.component').then(m => m.AttacksComponent) },
      { path: 'incidents', loadComponent: () => import('./pages/incidents/incidents.component').then(m => m.IncidentsComponent) },
      { path: 'audit', loadComponent: () => import('./pages/audit/audit.component').then(m => m.AuditComponent) },
      { path: 'devices', loadComponent: () => import('./pages/devices/devices.component').then(m => m.DevicesComponent) },
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
