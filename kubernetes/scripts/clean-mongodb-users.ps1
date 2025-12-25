# MongoDB Users Koleksiyonunu Temizle
# Bu script, MongoDB'deki users koleksiyonundaki tüm verileri temizler
# Böylece yeni kullanıcılar Long ID ile oluşturulacaktır

Write-Host "MongoDB Users Koleksiyonunu Temizliyorum..." -ForegroundColor Yellow
Write-Host ""

# MongoDB pod'una bağlan ve users koleksiyonunu temizle
kubectl exec -it -n ecommerce $(kubectl get pods -n ecommerce -l app=mongodb -o jsonpath='{.items[0].metadata.name}') -- mongosh ecommerce_users --eval "db.users.deleteMany({})"

Write-Host ""
Write-Host "Users koleksiyonu temizlendi!" -ForegroundColor Green
Write-Host "Artık yeni kullanıcılar Long ID (1, 2, 3...) ile oluşturulacaktır." -ForegroundColor Green
Write-Host ""
Write-Host "Not: Sequences koleksiyonu korunur, böylece ID'ler sıralı devam eder." -ForegroundColor Yellow

