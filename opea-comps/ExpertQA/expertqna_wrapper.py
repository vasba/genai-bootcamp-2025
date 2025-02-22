import os

# Set all environment variables before any imports
os.environ.update({
    "EMBEDDING_SERVICE_PORT": "6006",
    "RETRIEVER_SERVICE_PORT": "7000",
    "RERANK_SERVICE_PORT": "8808",
    "LLM_SERVICE_PORT": "11434",
    "LLM_MODEL": "llama3.2:1b"
})

from expertqna import main

if __name__ == "__main__":
    main()