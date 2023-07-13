package learn.scraibe.controllers;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import learn.scraibe.models.Note;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/generate-completion")
public class OpenAIController {
    @Value("${openai.api.key}")
    private String openaiApiKey;

    @PostMapping
    public ResponseEntity<Object> generateCompletion(@RequestBody Note note) {

        if(note.getContent() == null || note.getContent().isBlank()){
            return new ResponseEntity<>("Cannot have blank notes", HttpStatus.BAD_REQUEST);
        }

        //create service that will route to OpenAI endpoint, provide key and timeout value incase openai takes a long time
        OpenAiService service = new OpenAiService(openaiApiKey, Duration.ofSeconds(60));

        //set up messages and Roles
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "organize with bullet points, only respond with bullet points "+ note.getContent());
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.ASSISTANT.value(), "you are a helpful assistant");
        messages.add(userMessage);
        messages.add((systemMessage));

        // configure chatCompletionRequest object that will be sent over via the api
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo-0613")
                .messages(messages)
                .build();

        //use service to make the request to OpenAI and then get the specific message to send back to the frontend.
        ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();
        note.setContent(responseMessage.getContent());
        return new ResponseEntity<>(note, HttpStatus.OK);

        //TODO make a conditional statement based on the success of a response message,
        //one previous error occurred because the request timed out(openai took too long to send back a request)
        // but extending the duration seemed to solved the issue, just wondering what other issues to anticipate.
    }

}
