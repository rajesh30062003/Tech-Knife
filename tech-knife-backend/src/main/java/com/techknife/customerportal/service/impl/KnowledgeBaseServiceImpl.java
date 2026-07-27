package com.techknife.customerportal.service.impl;

import com.techknife.customerportal.dto.KnowledgeArticleDTO;
import com.techknife.customerportal.dto.KnowledgeCategoryDTO;
import com.techknife.customerportal.entity.KnowledgeBase;
import com.techknife.customerportal.entity.KnowledgeCategory;
import com.techknife.customerportal.repository.KnowledgeBaseRepository;
import com.techknife.customerportal.repository.KnowledgeCategoryRepository;
import com.techknife.customerportal.service.KnowledgeBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final KnowledgeCategoryRepository knowledgeCategoryRepository;

    @Override
    public List<KnowledgeArticleDTO> getArticles(String categoryId, Boolean popular, String query) {
        List<KnowledgeBase> articles;

        if (query != null && !query.isBlank()) {
            articles = knowledgeBaseRepository.findByTitleContainingIgnoreCaseOrSummaryContainingIgnoreCaseOrContentContainingIgnoreCase(query, query, query);
        } else if (categoryId != null && !categoryId.isBlank()) {
            articles = knowledgeBaseRepository.findByCategoryIdAndIsPublishedTrue(categoryId);
        } else if (Boolean.TRUE.equals(popular)) {
            articles = knowledgeBaseRepository.findByIsPopularTrueAndIsPublishedTrue();
        } else {
            articles = knowledgeBaseRepository.findByIsPublishedTrue();
        }

        return articles.stream().map(this::mapArticleToDTO).collect(Collectors.toList());
    }

    @Override
    public KnowledgeArticleDTO getArticleBySlug(String slug) {
        KnowledgeBase article = knowledgeBaseRepository.findBySlugAndIsPublishedTrue(slug)
                .orElseThrow(() -> new IllegalArgumentException("Knowledge article not found with slug: " + slug));

        article.setViewsCount((article.getViewsCount() != null ? article.getViewsCount() : 0L) + 1);
        KnowledgeBase saved = knowledgeBaseRepository.save(article);

        return mapArticleToDTO(saved);
    }

    @Override
    public List<KnowledgeCategoryDTO> getCategories() {
        return knowledgeCategoryRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(this::mapCategoryToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public KnowledgeArticleDTO markHelpful(String articleId) {
        KnowledgeBase article = knowledgeBaseRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article not found"));

        article.setHelpfulCount((article.getHelpfulCount() != null ? article.getHelpfulCount() : 0L) + 1);
        KnowledgeBase saved = knowledgeBaseRepository.save(article);
        return mapArticleToDTO(saved);
    }

    @Override
    public KnowledgeArticleDTO createArticle(KnowledgeArticleDTO dto) {
        String slug = dto.getSlug() != null ? dto.getSlug() : dto.getTitle().toLowerCase().replaceAll("[^a-z0-9]+", "-");
        KnowledgeBase article = KnowledgeBase.builder()
                .slug(slug)
                .title(dto.getTitle())
                .summary(dto.getSummary())
                .content(dto.getContent())
                .categoryId(dto.getCategoryId())
                .categoryName(dto.getCategoryName())
                .viewsCount(0L)
                .helpfulCount(0L)
                .isPublished(dto.getIsPublished() != null ? dto.getIsPublished() : true)
                .isPopular(dto.getIsPopular() != null ? dto.getIsPopular() : false)
                .authorName(dto.getAuthorName() != null ? dto.getAuthorName() : "Support Team")
                .tags(dto.getTags())
                .attachments(dto.getAttachments())
                .build();

        KnowledgeBase saved = knowledgeBaseRepository.save(article);
        return mapArticleToDTO(saved);
    }

    @Override
    public KnowledgeCategoryDTO createCategory(KnowledgeCategoryDTO dto) {
        KnowledgeCategory category = KnowledgeCategory.builder()
                .categoryName(dto.getCategoryName())
                .description(dto.getDescription())
                .icon(dto.getIcon())
                .articleCount(0)
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .build();

        KnowledgeCategory saved = knowledgeCategoryRepository.save(category);
        return mapCategoryToDTO(saved);
    }

    private KnowledgeArticleDTO mapArticleToDTO(KnowledgeBase k) {
        return KnowledgeArticleDTO.builder()
                .id(k.getId())
                .slug(k.getSlug())
                .title(k.getTitle())
                .summary(k.getSummary())
                .content(k.getContent())
                .categoryId(k.getCategoryId())
                .categoryName(k.getCategoryName())
                .viewsCount(k.getViewsCount())
                .helpfulCount(k.getHelpfulCount())
                .isPublished(k.getIsPublished())
                .isPopular(k.getIsPopular())
                .authorName(k.getAuthorName())
                .tags(k.getTags())
                .attachments(k.getAttachments())
                .createdAt(k.getCreatedAt())
                .updatedAt(k.getUpdatedAt())
                .build();
    }

    private KnowledgeCategoryDTO mapCategoryToDTO(KnowledgeCategory c) {
        return KnowledgeCategoryDTO.builder()
                .id(c.getId())
                .categoryName(c.getCategoryName())
                .description(c.getDescription())
                .icon(c.getIcon())
                .articleCount(c.getArticleCount())
                .displayOrder(c.getDisplayOrder())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
