from comps import MicroService, ServiceType
from comps.cores.proto.api_protocol import EmbeddingRequest
import PyPDF2
from typing import List, Dict
import numpy as np

class DocumentProcessor:
    def __init__(self, embedding_service: MicroService):
        self.embedding_service = embedding_service
        
    async def process_pdf(self, pdf_path: str, chunk_size: int = 1000) -> Dict[str, List]:
        """Process PDF document and initialize embeddings using OPEA embedding service"""
        # Extract text from PDF
        chunks = self._extract_and_chunk_pdf(pdf_path, chunk_size)
        
        # Get embeddings through OPEA embedding service
        embeddings = []
        for chunk in chunks:
            embedding_request = EmbeddingRequest(text=chunk)
            embedding_response = await self.embedding_service.ainvoke(embedding_request)
            embeddings.append(embedding_response.embedding)
            
        return {
            "chunks": chunks,
            "embeddings": np.array(embeddings)
        }
    
    def _extract_and_chunk_pdf(self, pdf_path: str, chunk_size: int) -> List[str]:
        """Extract text from PDF and split into chunks"""
        chunks = []
        with open(pdf_path, 'rb') as file:
            reader = PyPDF2.PdfReader(file)
            text = ""
            for page in reader.pages:
                text += page.extract_text()
                
            # Split into chunks of roughly equal size
            words = text.split()
            current_chunk = []
            current_size = 0
            
            for word in words:
                current_chunk.append(word)
                current_size += len(word) + 1  # +1 for space
                
                if current_size >= chunk_size:
                    chunks.append(" ".join(current_chunk))
                    current_chunk = []
                    current_size = 0
                    
            # Add any remaining text as final chunk
            if current_chunk:
                chunks.append(" ".join(current_chunk))
                
        return chunks