# Tech Knife - Monitoring, Metrics & Observability Guide

## 1. Monitoring Stack Architecture
- **API Metrics**: Prometheus-ready metrics exposed via `/api/v1/health` and system metrics utility (`src/utils/metrics.ts`).
- **Nginx Access & Security Logs**: Structured JSON logs capturing response times (`urt`), status codes, and user agents.
- **Vercel Web Analytics**: Real-time Core Web Vitals (LCP, FID, CLS) and page view analytics for `tech-knife-frontend`.
- **Render Service Health**: Automatic container health checks monitoring uptime and memory usage.

## 2. Key Performance Indicators (KPIs) & Thresholds
- **HTTP 200 Success Rate**: Target > 99.9%.
- **API Response Latency**: 95th percentile < 250ms; 99th percentile < 500ms.
- **Node.js Heap Memory**: Alert when heap memory usage exceeds 85% of allocated memory.
- **Database Connection Pool**: Alert when active connections reach 80% of MongoDB Atlas connection limit.
