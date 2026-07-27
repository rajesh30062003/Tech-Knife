package com.techknife.customerportal.service;

import com.techknife.customerportal.dto.KnowledgeArticleDTO;
import com.techknife.customerportal.dto.KnowledgeCategoryDTO;

import java.util.List;

public interface KnowledgeBaseService {

    List<KnowledgeArticleDTO> getArticles(String categoryId, Boolean popular, String query);

    KnowledgeArticleDTO getArticleBySlug(String slug);

    List<KnowledgeCategoryDTO> getCategories();

    KnowledgeArticleDTO markHelpful(String articleId);

    KnowledgeArticleDTO createArticle(KnowledgeArticleDTO dto);

    KnowledgeCategoryDTO createCategory(KnowledgeCategoryDTO dto);
}
