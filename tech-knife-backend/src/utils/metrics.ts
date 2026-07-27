// Tech Knife Health Check & Prometheus-Ready Application Metrics Collector

export interface SystemMetrics {
  status: 'ok' | 'degraded';
  uptime: number;
  timestamp: string;
  memory: {
    rssMb: number;
    heapTotalMb: number;
    heapUsedMb: number;
    externalMb: number;
  };
  cpu: {
    userUsageMicroseconds: number;
    systemUsageMicroseconds: number;
  };
  environment: string;
}

export function getSystemMetrics(): SystemMetrics {
  const mem = process.memoryUsage();
  const cpu = process.cpuUsage();

  return {
    status: 'ok',
    uptime: Math.floor(process.uptime()),
    timestamp: new Date().toISOString(),
    memory: {
      rssMb: Math.round(mem.rss / 1024 / 1024),
      heapTotalMb: Math.round(mem.heapTotal / 1024 / 1024),
      heapUsedMb: Math.round(mem.heapUsed / 1024 / 1024),
      externalMb: Math.round(mem.external / 1024 / 1024),
    },
    cpu: {
      userUsageMicroseconds: cpu.user,
      systemUsageMicroseconds: cpu.system,
    },
    environment: process.env.NODE_ENV || 'production',
  };
}
