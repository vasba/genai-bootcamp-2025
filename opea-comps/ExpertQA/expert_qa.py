from comps import MegaServiceEndpoint, MicroService, ServiceOrchestrator, ServiceRoleType, ServiceType
from comps.cores.mega.utils import handle_message
from comps.cores.proto.api_protocol import (
    ChatCompletionRequest,
    ChatCompletionResponse,
    ChatCompletionResponseChoice,
    ChatMessage,
    UsageInfo,
)
from fastapi import Request
from pydantic import BaseModel
import os

# Service configuration
MEGA_SERVICE_PORT = int(os.getenv("MEGA_SERVICE_PORT", 8888))
EMBEDDING_SERVER_HOST_IP = os.getenv("EMBEDDING_SERVER_HOST_IP", "0.0.0.0")
EMBEDDING_SERVER_PORT = int(os.getenv("EMBEDDING_SERVER_PORT", 80))
RETRIEVER_SERVICE_HOST_IP = os.getenv("RETRIEVER_SERVICE_HOST_IP", "0.0.0.0")
RETRIEVER_SERVICE_PORT = int(os.getenv("RETRIEVER_SERVICE_PORT", 7000))
LLM_SERVER_HOST_IP = os.getenv("LLM_SERVER_HOST_IP", "0.0.0.0")
LLM_SERVER_PORT = int(os.getenv("LLM_SERVER_PORT", 80))

class ExpertQAService:
    def __init__(self, host="0.0.0.0", port=8000):
        self.host = host
        self.port = port
        self.megaservice = ServiceOrchestrator()
        self.endpoint = "/v1/expert-qa"

    def setup_services(self):
        # Initialize embedding service
        embedding = MicroService(
            name="embedding",
            host=EMBEDDING_SERVER_HOST_IP,
            port=EMBEDDING_SERVER_PORT,
            endpoint="/embed",
            use_remote_service=True,
            service_type=ServiceType.EMBEDDING,
        )

        # Initialize retriever service
        retriever = MicroService(
            name="retriever",
            host=RETRIEVER_SERVICE_HOST_IP,
            port=RETRIEVER_SERVICE_PORT,
            endpoint="/v1/retrieval",
            use_remote_service=True,
            service_type=ServiceType.RETRIEVER,
        )

        # Initialize LLM service
        llm = MicroService(
            name="llm",
            host=LLM_SERVER_HOST_IP,
            port=LLM_SERVER_PORT,
            endpoint="/v1/chat/completions",
            use_remote_service=True,
            service_type=ServiceType.LLM,
        )

        # Add services to orchestrator and define flow
        self.megaservice.add(embedding).add(retriever).add(llm)
        
        self.megaservice.flow_to(embedding, retriever)
        self.megaservice.flow_to(retriever, llm)

    async def handle_request(self, request: Request):
        data = await request.json()
        source_lang = data.get("source_lang", "en")
        target_lang = data.get("target_lang", "ro")
        
        chat_request = ChatCompletionRequest.parse_obj(data)
        text = chat_request.messages[-1].content

        # If source language is Romanian, translate to English first using LLM
        if source_lang == "ro":
            translate_prompt = f"""
            Translate this from Romanian to English:

            Romanian:
            {text}

            English:
            """
            result_dict, _ = await self.megaservice.schedule(
                initial_inputs={"query": translate_prompt}
            )
            last_node = list(result_dict.keys())[-1]
            text = result_dict[last_node]["text"]

        # Get response from LLM using translated text
        result_dict, runtime_graph = await self.megaservice.schedule(
            initial_inputs={"text": text}
        )
        
        # Get final response
        last_node = runtime_graph.all_leaves()[-1]
        response = result_dict[last_node]["text"]

        # If target language is Romanian, translate response back
        if target_lang == "ro":
            translate_prompt = f"""
            Translate this from English to Romanian:

            English:
            {response}

            Romanian:
            """
            result_dict, _ = await self.megaservice.schedule(
                initial_inputs={"query": translate_prompt}
            )
            last_node = list(result_dict.keys())[-1]
            response = result_dict[last_node]["text"]
        
        choices = []
        usage = UsageInfo()
        choices.append(
            ChatCompletionResponseChoice(
                index=0,
                message=ChatMessage(role="assistant", content=response),
                finish_reason="stop",
            )
        )
        return ChatCompletionResponse(model="expertqa", choices=choices, usage=usage)

    def start(self):
        self.service = MicroService(
            self.__class__.__name__,
            service_role=ServiceRoleType.MEGASERVICE,
            host=self.host,
            port=self.port,
            endpoint=self.endpoint,
            input_datatype=ChatCompletionRequest,
            output_datatype=ChatCompletionResponse,
        )
        self.setup_services()
        self.service.add_route(self.endpoint, self.handle_request, methods=["POST"])
        self.service.start()

if __name__ == "__main__":
    expert_qa = ExpertQAService(port=MEGA_SERVICE_PORT)
    expert_qa.start()