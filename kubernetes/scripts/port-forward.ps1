# PowerShell script to port-forward all services
# Includes API Gateway and all microservices

$Namespace = "ecommerce"

Write-Host "========================================" -ForegroundColor Green
Write-Host "Port-Forward İşlemleri Başlatılıyor..." -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host "Durdurmak için Ctrl+C tuşlarına basın." -ForegroundColor Yellow
Write-Host ""

# Function to start port-forward in a new process
function Start-PortForward {
    param (
        [string]$ServiceName,
        [int]$LocalPort,
        [int]$RemotePort
    )
    Write-Host "Başlatılıyor: $ServiceName ($LocalPort -> $RemotePort)..." -ForegroundColor Cyan
    
    # Wait for service to be available
    $ready = $false
    while (-not $ready) {
        $pods = kubectl get pods -n $Namespace -l app=$ServiceName --no-headers 2>$null
        # If not found by full name, try removing -service suffix (for some deployments)
        if (-not $pods) {
            $appLabel = $ServiceName.Replace("-service","")
            $pods = kubectl get pods -n $Namespace -l app=$appLabel --no-headers 2>$null
        }
        
        if ($pods -match "Running") {
            $ready = $true
        } else {
            Write-Host "Pod'un hazır olması bekleniyor ($ServiceName)..." -ForegroundColor Gray
            Start-Sleep -Seconds 2
        }
    }

    # Start port-forward as a background job if not already running
    $jobName = "pf-$ServiceName"
    $existingJob = Get-Job -Name $jobName -ErrorAction SilentlyContinue
    if ($existingJob) {
        Write-Host "Zaten çalışıyor: $jobName" -ForegroundColor Yellow
        return
    }

    Start-Job -Name $jobName -ScriptBlock {
        param($Namespace, $ServiceName, $LocalPort, $RemotePort)
        kubectl port-forward -n $Namespace service/$ServiceName "${LocalPort}:${RemotePort}"
    } -ArgumentList $Namespace, $ServiceName, $LocalPort, $RemotePort
}

# Start port-forwards
Start-PortForward -ServiceName "api-gateway-service" -LocalPort 8090 -RemotePort 80
Start-PortForward -ServiceName "user-service" -LocalPort 9011 -RemotePort 9011
Start-PortForward -ServiceName "product-service" -LocalPort 9012 -RemotePort 9012
Start-PortForward -ServiceName "stock-service" -LocalPort 9013 -RemotePort 9013
Start-PortForward -ServiceName "order-service" -LocalPort 9014 -RemotePort 9014

Write-Host ""
Write-Host "Tüm port-forward işlemleri arka plan (Background Job) olarak başlatıldı." -ForegroundColor Green
Write-Host "Pencere açılmayacaktır. Durdurmak için: Get-Job pf-* | Stop-Job" -ForegroundColor Yellow
Write-Host ""
Write-Host "Adresler:" -ForegroundColor Yellow
Write-Host "  - API Gateway:     http://localhost:8090" -ForegroundColor White
Write-Host "  - User Service:    http://localhost:9011" -ForegroundColor White
Write-Host "  - Product Service: http://localhost:9012" -ForegroundColor White
Write-Host "  - Stock Service:   http://localhost:9013" -ForegroundColor White
Write-Host "  - Order Service:   http://localhost:9014" -ForegroundColor White
Write-Host ""
Write-Host "Swagger UI Adresleri:" -ForegroundColor Yellow
Write-Host "  - API Gateway (Aggregator): http://localhost:8090/swagger-ui.html" -ForegroundColor Cyan
Write-Host "  - User Service:             http://localhost:9011/swagger-ui.html" -ForegroundColor Cyan
Write-Host "  - Product Service:          http://localhost:9012/swagger-ui.html" -ForegroundColor Cyan
Write-Host "  - Stock Service:            http://localhost:9013/swagger-ui.html" -ForegroundColor Cyan
Write-Host "  - Order Service:            http://localhost:9014/swagger-ui.html" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Green

