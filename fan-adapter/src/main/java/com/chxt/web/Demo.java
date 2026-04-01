package com.chxt.web;

import dev.langchain4j.model.openai.OpenAiChatModel;

public class Demo {


    public static void main(String[] args) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();

        String answer = model.chat("什么是ai agent");
        System.out.println(answer); // Hello World
    }
}
