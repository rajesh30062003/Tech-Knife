package com.techknife.project.converter;

import com.techknife.project.entity.ProjectStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class ProjectStatusWritingConverter implements Converter<ProjectStatus, String> {

    @Override
    public String convert(ProjectStatus source) {
        return source != null ? source.name() : ProjectStatus.PLANNED.name();
    }
}
