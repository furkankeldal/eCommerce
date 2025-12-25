# PowerShell script to build Docker images for Kubernetes

Write-Host "Building Docker images for Kubernetes..." -ForegroundColor Green

# Build all service images
Write-Host "Building user-service..." -ForegroundColor Yellow
docker build -t user-service:latest ./user-service

Write-Host "Building product-service..." -ForegroundColor Yellow
docker build -t product-service:latest ./product-service

Write-Host "Building stock-service..." -ForegroundColor Yellow
docker build -t stock-service:latest ./stock-service

Write-Host "Building order-service..." -ForegroundColor Yellow
docker build -t order-service:latest ./order-service

Write-Host "Building api-gateway..." -ForegroundColor Yellow
docker build -t api-gateway:latest ./api-gateway

Write-Host "All images built successfully!" -ForegroundColor Green
docker images | Select-String -Pattern "user-service|product-service|stock-service|order-service|api-gateway"

