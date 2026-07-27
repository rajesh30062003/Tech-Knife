// k6 Performance & Load Testing Script for Tech Knife Enterprise Gateway
// Usage: k6 run loadTest.k6.js

import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 50 },  // Ramp up to 50 concurrent users
    { duration: '1m', target: 200 },  // Sustained peak load of 200 concurrent users
    { duration: '30s', target: 0 },   // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<250', 'p(99)<500'], // 95% of requests under 250ms, 99% under 500ms
    http_req_failed: ['rate<0.01'],               // Error rate < 1%
  },
};

const BASE_URL = __ENV.API_BASE_URL || 'https://api.techknife.com/v1';

export default function () {
  // 1. Health Check Endpoint
  const healthRes = http.get(`${BASE_URL}/health`);
  check(healthRes, {
    'health status is 200': (r) => r.status === 200,
  });

  // 2. Authentication Flow
  const loginPayload = JSON.stringify({
    email: 'admin@techknife.com',
    password: 'SecureEnterprisePassword2026!',
  });

  const loginRes = http.post(`${BASE_URL}/auth/login`, loginPayload, {
    headers: { 'Content-Type': 'application/json' },
  });

  check(loginRes, {
    'login status is 200': (r) => r.status === 200,
    'has token': (r) => r.json('token') !== undefined,
  });

  const token = loginRes.json('token') || 'mock-jwt-token';
  const params = {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  };

  // 3. Dashboard KPI Summary Request
  const dashboardRes = http.get(`${BASE_URL}/analytics/kpis`, params);
  check(dashboardRes, {
    'dashboard response status is 200': (r) => r.status === 200,
  });

  // 4. Projects Query Request
  const projectsRes = http.get(`${BASE_URL}/projects?page=1&limit=20`, params);
  check(projectsRes, {
    'projects fetch status is 200': (r) => r.status === 200,
  });

  sleep(1);
}
