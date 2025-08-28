# Ollama Kotlin Library
An Ollama Kotlin library provides the easiest way to integrate your Kotlin project with [Ollama](https://github.com/ollama).

This lib is based on [Ollama's](https://github.com/ollama/ollama-js) libs.

# Ollama-KT Examples

Practical examples demonstrating all Ollama APIs using ollama-kt.

## ⚙️ Prerequisites

1. **Ollama running**: `ollama serve` (port 11434)
2. **Models available**: `ollama pull qwen3:0.6b` (small model for testing)

## 🚀 Running Examples

### ✅ Working (tested)

```bash
# Conversational chat
./gradlew examples:runChat

# List available models
./gradlew examples:runList

# List running processes
./gradlew examples:runPs

# HTTP client overview
./gradlew examples:runHttpClient
```

### ⚠️ Partial functionality

```bash
# Copy model (works, but response parsing has issues)
./gradlew examples:runCopy

# Create custom model (requires longer timeout)
./gradlew examples:runCreate
```

### ❌ Requires fixes

```bash
# Generate text (compilation issue)
./gradlew examples:runGenerate

# Model information (compilation issue)
./gradlew examples:runShow

# Delete model (requires HTTP DELETE method)
./gradlew examples:runDelete

# Generate embeddings (requires embedding model)
./gradlew examples:runEmbed
```

## 💡 Example Output

### Chat
```
Model: qwen3:0.6b
Response: The capital of Brazil is **Brasília**.
Done: true
```

### List Models
```
Available models (10):
- qwen3:0.6b
  Size: 498 MB
  Format: gguf
  Family: qwen3
```

## 🔧 Troubleshooting

**Connection errors:**
- Check if Ollama is running: `ollama serve`
- Test: `curl http://localhost:11434/api/tags`

**Model not found:**
- Download a model: `ollama pull qwen3:0.6b`
- List models: `ollama list`

**Timeout:**
- Large models can take more than 30s
- Use smaller models for testing: `qwen3:0.6b` (498MB)

## 📁 Examples List

| Example | Endpoint | Status | Function |
|---------|----------|---------|----------|
| `runChat` | `/api/chat` | ✅ | Conversational chat |
| `runList` | `/api/tags` | ✅ | List models |
| `runPs` | `/api/ps` | ✅ | Running processes |
| `runHttpClient` | multiple | ✅ | Complete demo |
| `runCopy` | `/api/copy` | ⚠️ | Copy model |
| `runCreate` | `/api/create` | ⚠️ | Create model |
| `runGenerate` | `/api/generate` | ❌ | Generate text |
| `runShow` | `/api/show` | ❌ | Model info |
| `runDelete` | `/api/delete` | ❌ | Delete model |
| `runEmbed` | `/api/embed` | ❌ | Embeddings |

**Status:** ✅ Working | ⚠️ Partial | ❌ Requires fixes