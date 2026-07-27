# Tech Knife - System Maintenance & Upgrades Guide

## 1. Routine Maintenance Tasks

### Weekly Tasks
- Review system health and response latency metrics in Render and Vercel analytics.
- Check MongoDB Atlas disk usage and index performance.
- Inspect error log spikes in Nginx or backend application logs.

### Monthly Tasks
- Run automated database backup script (`./scripts/backup-mongodb.sh`) and verify restore integrity.
- Apply security updates for npm dependencies (`npm audit fix`).
- Rotate API access tokens and service account secrets.

### Quarterly Tasks
- Review employee access roles and revoke obsolete permissions.
- Test Disaster Recovery restore procedure on a staging database.
- Perform WCAG accessibility and Lighthouse performance audits.
