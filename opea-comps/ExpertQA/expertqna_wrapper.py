import os

# Set all environment variables before any imports
os.environ.update({
    "EMBEDDING_SERVICE_PORT": "6006",
    "RETRIEVER_SERVICE_PORT": "7000",
    "RERANK_SERVICE_PORT": "8808",
    "LLM_SERVICE_PORT": "11434",
    "LLM_MODEL": "llama3.1:8b",
    "TELEMETRY_ENDPOINT": "https://api.honeycomb.io/v1/traces",
    "OTEL_EXPORTER_OTLP_PROTOCOL": "http/protobuf",
    "OTEL_EXPORTER_OTLP_HEADERS": "" 
})

from expertqna import main

if __name__ == "__main__":
    main()