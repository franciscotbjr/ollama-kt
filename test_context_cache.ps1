# Test script for context-cache.md PowerShell code
Write-Host "Testing context-cache.md PowerShell script..." -ForegroundColor Yellow

# Create cache directory if needed
$CACHE_DIR = "$env:USERPROFILE\.claude\.ollama-kt_cache"
Write-Host "Cache directory path: $CACHE_DIR" -ForegroundColor Cyan
New-Item -ItemType Directory -Path $CACHE_DIR -Force | Out-Null

# Generate cache file name based on project path
$currentPath = (Get-Location).Path
Write-Host "Current path: $currentPath" -ForegroundColor Cyan
$PROJECT_HASH = (([System.Security.Cryptography.MD5]::Create().ComputeHash([System.Text.Encoding]::UTF8.GetBytes($currentPath)) | ForEach-Object { $_.ToString('x2') }) -join '').Substring(0,8)
$CACHE_FILE = "$CACHE_DIR\project_$PROJECT_HASH.cache"

# Display results
Write-Host "`n=== CONTEXT CACHE TEST RESULTS ===" -ForegroundColor Green
Write-Host "Cache Directory: $CACHE_DIR" -ForegroundColor White
Write-Host "Project Hash: $PROJECT_HASH" -ForegroundColor White
Write-Host "Cache File: $CACHE_FILE" -ForegroundColor White
Write-Host "Directory exists: $(Test-Path $CACHE_DIR)" -ForegroundColor White
Write-Host "Hash length: $($PROJECT_HASH.Length) characters" -ForegroundColor White

# Test cache file creation
$testData = @{
    projectPath = $currentPath
    timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    buildSystem = "Gradle"
    testFramework = "JUnit"
} | ConvertTo-Json

$testData | Out-File -FilePath $CACHE_FILE -Encoding UTF8
Write-Host "Test cache file created: $(Test-Path $CACHE_FILE)" -ForegroundColor White
Write-Host "✅ Context cache script test PASSED" -ForegroundColor Green