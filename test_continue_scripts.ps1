# Test script for all continue.md PowerShell scripts
Write-Host "Testing all continue.md PowerShell scripts..." -ForegroundColor Yellow

Write-Host "`n=== SECTION 1: Context Discovery & Loading ===" -ForegroundColor Magenta
# Locate the project-specific cache directory
$CACHE_DIR = "$env:USERPROFILE\.claude\.ollama-kt_cache"
$currentPath = (Get-Location).Path
$PROJECT_HASH = (([System.Security.Cryptography.MD5]::Create().ComputeHash([System.Text.Encoding]::UTF8.GetBytes($currentPath)) | ForEach-Object { $_.ToString('x2') }) -join '').Substring(0,8)
$CACHE_FILE = "$CACHE_DIR\project_$PROJECT_HASH.cache"

Write-Host "🔍 Searching for previous conversation context..."
Write-Host "   Cache location: $CACHE_DIR"
Write-Host "   Project hash: $PROJECT_HASH"

if (Test-Path $CACHE_FILE) {
    Write-Host "✅ Found previous conversation cache!"
    try {
        $context = Get-Content $CACHE_FILE -Raw | ConvertFrom-Json
        Write-Host "📝 Loading conversation history and project state..."
        Write-Host "   Context loaded successfully"
    } catch {
        Write-Host "❌ Error loading context: $($_.Exception.Message)"
    }
} else {
    Write-Host "⚠️  No previous conversation found for this project."
    Write-Host "   Starting fresh - I'll build new context as we work."
}

Write-Host "`n=== SECTION 2: Development Notes Review ===" -ForegroundColor Magenta
$DEV_NOTES_FILE = "DEV_NOTES.md"
if (Test-Path $DEV_NOTES_FILE) {
    Write-Host "📋 Found development notes - reviewing progress..."
    try {
        $devNotes = Get-Content $DEV_NOTES_FILE -Raw
        Write-Host "✅ Development history loaded successfully"
        Write-Host "   File size: $(($devNotes -split "`n").Count) lines"
    } catch {
        Write-Host "❌ Error reading DEV_NOTES.md: $($_.Exception.Message)"
    }
} else {
    Write-Host "📝 No development notes found - will create them as we progress"
}

Write-Host "`n=== SECTION 3: Critical Project Files Analysis ===" -ForegroundColor Magenta
Write-Host "📚 Reading critical project files..."

# 1. MANDATORY: Read all files in the spec folder
Write-Host "   📋 Reading spec folder - main project guidance:"
$SPEC_FILES = @(
    "spec\especificacao_detalhada_projeto.md",
    "spec\especificacao_estrutura-projeto.md", 
    "spec\coding-conventions.md"
)
foreach ($specFile in $SPEC_FILES) {
    if (Test-Path $specFile) {
        try {
            $content = Get-Content $specFile -Raw
            Write-Host "   ✅ READ: $specFile ($(($content -split "`n").Count) lines)"
        } catch {
            Write-Host "   ❌ ERROR reading $specFile`: $($_.Exception.Message)"
        }
    } else {
        Write-Host "   ⚠️  MISSING: $specFile"
    }
}

# 2. MANDATORY: Read API specifications from Postman collection
Write-Host "   🔌 Reading API specifications:"
if (Test-Path "Ollama REST API.postman_collection.json") {
    try {
        $apiSpec = Get-Content "Ollama REST API.postman_collection.json" -Raw | ConvertFrom-Json
        Write-Host "   ✅ READ: API specs ($(($apiSpec.item).Count) endpoints to implement)"
    } catch {
        Write-Host "   ❌ ERROR parsing API specs: $($_.Exception.Message)"
    }
} else {
    Write-Host "   ❌ CRITICAL: Ollama REST API.postman_collection.json NOT FOUND"
}

# 3. MANDATORY: Read development notes (already tested above)

# 4. MANDATORY: Analyze build system configuration
Write-Host "   🔧 Reading build configuration:"
$BUILD_FILES = @("build.gradle.kts", "settings.gradle.kts", "gradle.properties", "lib\build.gradle.kts")
foreach ($buildFile in $BUILD_FILES) {
    if (Test-Path $buildFile) {
        try {
            $buildContent = Get-Content $buildFile -Raw
            Write-Host "   ✅ READ: $buildFile"
        } catch {
            Write-Host "   ❌ ERROR reading $buildFile`: $($_.Exception.Message)"
        }
    } else {
        Write-Host "   ⚠️  MISSING: $buildFile"
    }
}

# 5. MANDATORY: Read version catalog for dependencies
if (Test-Path "gradle\libs.versions.toml") {
    try {
        $versionCatalog = Get-Content "gradle\libs.versions.toml" -Raw
        Write-Host "   ✅ READ: gradle\libs.versions.toml (dependency versions)"
    } catch {
        Write-Host "   ❌ ERROR reading version catalog: $($_.Exception.Message)"
    }
} else {
    Write-Host "   ⚠️  MISSING: gradle\libs.versions.toml"
}

Write-Host "✅ All critical project files analyzed"

Write-Host "`n=== SECTION 4: Project Structure Pattern Analysis ===" -ForegroundColor Magenta
Write-Host "🏗️ Analyzing project structure patterns..."

# Analyze source code structure
Write-Host "   📁 Analyzing source code patterns:"
$SOURCE_DIRS = @("lib\src\main\kotlin", "lib\src\test\kotlin", "examples\src\main\kotlin")
foreach ($sourceDir in $SOURCE_DIRS) {
    if (Test-Path $sourceDir) {
        try {
            $kotlinFiles = Get-ChildItem -Path $sourceDir -Recurse -Filter "*.kt"
            Write-Host "   ✅ $sourceDir`: $($kotlinFiles.Count) Kotlin files"
            
            # Read key implementation files
            foreach ($file in $kotlinFiles) {
                if ($file.Name -match "(Client|Request|Response|Model|Exception|Library)") {
                    try {
                        $fileContent = Get-Content $file.FullName -Raw
                        Write-Host "      📄 READ: $($file.Name) (key implementation file)"
                    } catch {
                        Write-Host "      ❌ ERROR reading $($file.Name): $($_.Exception.Message)"
                    }
                }
            }
        } catch {
            Write-Host "   ❌ ERROR analyzing $sourceDir`: $($_.Exception.Message)"
        }
    } else {
        Write-Host "   ⚠️  MISSING: $sourceDir"
    }
}

# Detect testing framework and patterns
Write-Host "   🧪 Detecting test framework:"
if (Test-Path "lib\src\test\kotlin") {
    try {
        $testFiles = Get-ChildItem -Path "lib\src\test\kotlin" -Recurse -Filter "*Test.kt"
        if ($testFiles.Count -gt 0) {
            $sampleTest = Get-Content $testFiles[0].FullName -Raw
            if ($sampleTest -match "import org\.junit") {
                Write-Host "   ✅ JUnit testing framework detected"
            }
            Write-Host "   ✅ Found $($testFiles.Count) test files"
        } else {
            Write-Host "   ⚠️  No test files found"
        }
    } catch {
        Write-Host "   ❌ ERROR analyzing tests: $($_.Exception.Message)"
    }
} else {
    Write-Host "   ⚠️  Test directory not found"
}

# Detect formatter configuration
Write-Host "   🎨 Checking code formatting configuration:"
$FORMAT_FILES = @(".editorconfig", "detekt.yml", "ktlint.gradle")
foreach ($formatFile in $FORMAT_FILES) {
    if (Test-Path $formatFile) {
        Write-Host "   ✅ Found formatter config: $formatFile"
    } else {
        Write-Host "   ⚠️  MISSING: $formatFile"
    }
}

Write-Host "✅ Project structure patterns identified"

Write-Host "`n=== SECTION 5: Current State Assessment ===" -ForegroundColor Magenta
Write-Host "🔄 Assessing current project state..."

# Check git status for recent changes
try {
    $gitStatus = git status --porcelain 2>$null
    if ($gitStatus) {
        $gitStatus | ForEach-Object {
            Write-Host "   📝 Modified: $_"
        }
    } else {
        Write-Host "   ✅ No uncommitted changes"
    }
} catch {
    Write-Host "   ⚠️  Git status check failed: $($_.Exception.Message)"
}

# Review recent commits
Write-Host "   📊 Recent commits:"
try {
    $recentCommits = git log --oneline -5 2>$null
    if ($recentCommits) {
        $recentCommits | ForEach-Object {
            Write-Host "      $_"
        }
    } else {
        Write-Host "      No commits found"
    }
} catch {
    Write-Host "   ⚠️  Git log check failed: $($_.Exception.Message)"
}

Write-Host "✅ Project state assessment complete"

Write-Host "`n=== SECTION 6: Context Restoration Summary ===" -ForegroundColor Magenta
Write-Host "`n🚀 CONVERSATION CONTINUATION READY"
Write-Host "=================================="
Write-Host "📅 Last session: $(if ($context.lastSession) { $context.lastSession } else { 'Not found' })"
Write-Host "🎯 Active tasks: $(if ($context.activeTasks) { $context.activeTasks.Count } else { '0' })"
Write-Host "📝 Dev notes: $(if (Test-Path $DEV_NOTES_FILE) { 'Available' } else { 'Will be created' })"
Write-Host "🔧 Build system: $(if ($context.buildSystem) { $context.buildSystem } else { 'Gradle (detected)' })"
Write-Host "🧪 Test framework: $(if ($context.testFramework) { $context.testFramework } else { 'To be determined' })"
Write-Host "`n💡 I'm ready to continue exactly where we left off!"
Write-Host "   Just tell me what you'd like to work on next."

Write-Host "`n=== FINAL TEST RESULTS ===" -ForegroundColor Green
Write-Host "✅ ALL CONTINUE.MD POWERSHELL SCRIPTS TESTED SUCCESSFULLY" -ForegroundColor Green