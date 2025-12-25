# PowerShell script to deploy all services to Kubernetes

Write-Host "Deploying all services to Kubernetes..." -ForegroundColor Green

# Create namespace and config
Write-Host "Creating namespace and configuration..." -ForegroundColor Yellow
kubectl apply -f ../config/

# Deploy infrastructure and services
Write-Host "Deploying all services..." -ForegroundColor Yellow
kubectl apply -f ../deployments/

# Wait for infrastructure to be ready
Write-Host "Waiting for infrastructure to be ready..." -ForegroundColor Yellow
kubectl wait --for=condition=ready pod -l app=mongodb -n ecommerce --timeout=300s
kubectl wait --for=condition=ready pod -l app=redis -n ecommerce --timeout=300s

Write-Host "Deployment completed!" -ForegroundColor Green
Write-Host "Check status with: kubectl get pods -n ecommerce" -ForegroundColor Cyan

