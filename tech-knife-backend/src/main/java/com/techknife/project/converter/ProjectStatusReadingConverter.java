package com.techknife.project.converter;

import com.techknife.project.entity.ProjectStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class ProjectStatusReadingConverter implements Converter<String, ProjectStatus> {

    @Override
    public ProjectStatus convert(String source) {
        return ProjectStatus.fromString(source);
    }
}
