package com.hoidap.hoidapdemo.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class VectorStoreConfig {

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingModel);
        
        // Cố gắng tải lại trí nhớ (dữ liệu Vector) từ file đã lưu trước đó nếu có
        File vectorStoreFile = new File("hoidap_vector_store.json");
        if (vectorStoreFile.exists()) {
            simpleVectorStore.load(vectorStoreFile);
        }
        
        return simpleVectorStore;
    }
}
