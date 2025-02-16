from comps import MegaServiceEndpoint, MicroService, ServiceOrchestrator, ServiceRoleType, ServiceType
from comps.cores.mega.utils import handle_message
from comps.cores.proto.api_protocol import (
    ChatCompletionRequest,
    ChatCompletionResponse,
    ChatCompletionResponseChoice,
    ChatMessage,
    UsageInfo,
    TranslationRequest
)
import os

# Service configuration
MEGA_SERVICE_PORT = int(os.getenv("MEGA_SERVICE_PORT", 8888))
EMBEDDING_SERVER_HOST_IP = os.getenv("EMBEDDING_SERVER_HOST_IP", "0.0.0.0")
EMBEDDING_SERVER_PORT = int(os.getenv("EMBEDDING_SERVER_PORT", 80))
RETRIEVER_SERVICE_HOST_IP = os.getenv("RETRIEVER_SERVICE_HOST_IP", "0.0.0.0")
RETRIEVER_SERVICE_PORT = int(os.getenv("RETRIEVER_SERVICE_PORT", 7000))
LLM_SERVER_HOST_IP = os.getenv("LLM_SERVER_HOST_IP", "0.0.0.0")
LLM_SERVER_PORT = int(os.getenv("LLM_SERVER_PORT", 80))
TRANSLATION_SERVER_HOST_IP = os.getenv("TRANSLATION_SERVER_HOST_IP", "0.0.0.0")
TRANSLATION_SERVER_PORT = int(os.getenv("TRANSLATION_SERVER_PORT", 80))

class ExpertQAService:
    def __init__(self, host="0.0.0.0", port=8000):
        self.host = host
        self.port = port
        self.megaservice = ServiceOrchestrator()
        self.endpoint = "/v1/expert-qa"

    def setup_services(self):
        # Initialize translation service
        translation = MicroService(
            name="translation",
            host=TRANSLATION_SERVER_HOST_IP,
            port=TRANSLATION_SERVER_PORT,
            endpoint="/v1/translation",
            use_remote_service=True,
            service_type=ServiceType.TRANSLATION,
        )

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
        self.megaservice.add(translation).add(embedding).add(retriever).add(llm)
        
        # If source is Romanian, translate to English first
        self.megaservice.flow_to(translation, embedding, condition="source_lang=='ro'")
        self.megaservice.flow_to(embedding, retriever)
        self.megaservice.flow_to(retriever, llm)
        # Translate answer back to Romanian if needed
        self.megaservice.flow_to(llm, translation, condition="target_lang=='ro'")

    async def handle_request(self, request: Request):
        data = await request.json()
        source_lang = data.get("source_lang", "en")
        target_lang = data.get("target_lang", "ro")
        
        chat_request = ChatCompletionRequest.parse_obj(data)
        
        # Build translation request if needed
        if source_lang == "ro":
            translation_request = TranslationRequest(
                text=chat_request.messages[-1].content,
                source_lang="ro",
                target_lang="en"
            )
            
        result_dict, runtime_graph = await self.megaservice.schedule(
            initial_inputs={
                "text": chat_request.messages[-1].content,
                "source_lang": source_lang,
                "target_lang": target_lang
            }
        )
        
        # Get final response from last node
        last_node = runtime_graph.all_leaves()[-1]
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