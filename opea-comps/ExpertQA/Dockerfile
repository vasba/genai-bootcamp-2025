FROM python:3.10-slim

WORKDIR /app

# Install dependencies
COPY requirements.txt .
RUN pip install -r requirements.txt

# Copy service files
COPY expert_qa.py .
COPY document_processor.py .

# Create data directory for PDF documents
RUN mkdir -p /app/data

EXPOSE 8888

CMD ["python", "expert_qa.py"]